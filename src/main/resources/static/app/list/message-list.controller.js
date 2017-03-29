angular.module('messageHubDemoApp')

.controller('MessageListController', ['$scope', '$state','$stateParams', '$uibModal', '$filter','$location', 'googleChartApiConfig', 'MessageCountHistoryFactory', 'MessageListFactory','$q',
                                     function($scope, $state, $stateParams, $uibModal, $filter, $location, googleChartApiConfig, MessageCountHistoryFactory, MessageListFactory, $q){
	
	/**
	 * On PAGE LOAD: 
	 * 1. Get calendar data for the filter/facility.
	 * 2. Get Messages for the date/facility/filters
	 * 3. Get the ESSR stats for the date/facility.  
	 * 
	 * On new DATE:  
	 * 1. Get ESSR stats for the new date/facility
	 * 2. Get messages for this date/facility/filter 
	 * 
	 * On new FILTER: 
	 * 1. Get Calendar data for the filter/facility
	 * 2. Get messages for this filter.   
	 * 
	 */
	$scope.yearDirection = 0;
	$scope.hasNextYear = false;
	$scope.hasPrevYear = false;
	$scope.currentYear = new Date().getFullYear();
	$scope.searchOptions = {};
	$scope.provider = {key:""};
	$scope.calendarPopulated=false;
	$scope.historyData = [];
	$scope.emptyRows = [];
	$scope.calendarDisplayYear = '';
	$scope.resultsMetaData = {
			page:0,
			elementsPerPage:10,
			maxSize:10,
			totalElements:0,//Don't modify this except with the actual list length. Modifying this causes a page reload...  
			messages:[],
	};
	
	$scope.toggleStat = function(id) {
		$scope.searchOptions.filters.statisticId = id;
		$scope.filterStatName = lookupStatName(id);
		populateCalendar();
		$scope.reloadPageData();
        $scope.showStats = false;
	}
	
	$scope.readyHandler = function(chartWrapper) {
		console.log("Message list Controller.  READY HANDLER");
		$scope.calendarChartWrapper = chartWrapper;
	}
	
	$scope.goToSiteSearch = function() {
		$window.location.href = '/#/messages/';
	}
	
	$scope.incrementDate = function(incAmt) {
		console.log("incrementDate: ENTRY");
		var workingDate = new moment($scope.searchOptions.date);

		//find nerest date in history...
		var entries = $scope.historyData; 
		
		//Find the index of the current date in the history: 
		var next = null;
		var max = entries.length;
		
		var directionPrev = incAmt < 0;
		var directionNext = incAmt > 0;
		var atCurrentYear = $scope.calendarDisplayYear == $scope.currentYear;
		
		for (idx in entries) {
			if (entries[idx].day == workingDate.format("YYYY-MM-DD")) {
				var nextIndex = parseInt(idx) + parseInt(incAmt);
				console.log("incrementDate: index: " + idx + " next index: " + nextIndex + " max: " + max + " incAmt: " + incAmt);
				var canDoNext = nextIndex < max && nextIndex >= 0;
				if (canDoNext) {
					console.log("incrementDate: next index: " + nextIndex);
					next = new moment(entries[nextIndex].day);
					repopulatePageForDate(next.toDate());
					return;
				}
			}
		}
		
		if (next == null) {
			//There is no next date in the current calendar...
			if (directionNext && atCurrentYear) {
				console.log("At last entry of current year.  no next date. ");
				return;
			}
			
			//Assume that we don't have another date this year to check.  go to the next year.
			console.log("incrementDate:  current display year: " + $scope.calendarDisplayYear);
			
			var calendarPromise = changeYear(incAmt);
			calendarPromise.then(function () {
					var index = $scope.historyData.length-1;
					if (directionNext) {
						index = 0;
					}
        			var entry = $scope.historyData[index];
        			console.log(entry);
        			next = new moment(entry.day);
        			repopulatePageForDate(next.toDate());
                });
		}
	}
	
	function repopulatePageForDate(date) {
		$scope.searchOptions.date = date;
		//now render the new date's stuff.
		$scope.resultsMetaData.page = 1;
		$scope.reloadPageData();
	}
	 
	$scope.listloaded = false;

	$scope.pagingClicked = function() {
		console.log("PAGING!!!");
		$scope.reloadPageData();
	}
	
	$scope.reloadPageData = function(){
		console.log("Loading New Page Data...")
		populateURL();
		getMessageList();
	}
	
	function populateURL() {
		console.log("filters: ");
		console.log($scope.searchOptions.filters);
		var filterString = $filter('filterObjectToQueryString')($scope.searchOptions.filters);
		var formattedDate = $filter('date')($scope.searchOptions.date, 'yyyyMMdd');
		console.log("URL CHANGE START: provider[" + $scope.provider.key + "] filter["+filterString+"] page[" + $scope.resultsMetaData.page + "] date[" + formattedDate + "]");
		$state.go('.', {providerKey: $scope.provider.key, date:formattedDate, filters:(filterString), page:$scope.resultsMetaData.page}, {notify: false}, {reload: false});
		console.log("URL CHANGE END  : provider[" + $scope.provider.key + "] filter["+filterString+"] page[" + $scope.resultsMetaData.page + "] date[" + formattedDate + "]");
	}
	
	function clearChartData() {
		$scope.myChartObject.data.rows = [];
	}
	
	$scope.prevYear = function() {
		changeYear(-1);
	}
	
	$scope.nextYear = function() {
		changeYear(1);
	}
	
	function changeYear(inc) {
		if (inc > 0 && $scope.calendarDisplayYear < $scope.currentYear) {
			console.log("moving to nextYear.  current display year: " + $scope.calendarDisplayYear);
			$scope.calendarDisplayYear+=1;
		} else if (inc < 0 && $scope.calendarDisplayYear > 2010) {
			$scope.calendarDisplayYear+=-1;
		} else {
			console.log("NO NEXT YEAR");
		}
		return populateCalendar();
	}
	
	function populateCalendar() {
		console.log("POPULATING calendar");
		$scope.calendarPopulated=false;

		var deferred = $q.defer();
		MessageCountHistoryFactory.get({
            providerKey: $scope.provider.key,
            year: $scope.calendarDisplayYear,
            filters: $filter('filterObjectToQueryString')($scope.searchOptions.filters)
        }, function(dataIn) {
        }).$promise.then(function(dataIn) {
            delete dataIn.$promise;
            delete dataIn.$resolved;
            console.log(dataIn);
            $scope.historyData=dataIn.messageHistory;
            console.log('populateCalendar - ');
            console.log($scope.historyData);
            pushHistoryDataToCalendar();
            $scope.calendarPopulated=true;
            deferred.resolve();
        });
		
		return deferred.promise;
	}
	
	function pushHistoryDataToCalendar()  {
		console.log("pushHistoryDataToCalendar - data: ");
		console.log($scope.historyData);
		clearChartData();
		dateCountArray = $scope.historyData;
		
		var mStart = stringToMoment($scope.calendarDisplayYear + "-01-01");
		var mEnd = stringToMoment($scope.calendarDisplayYear + "-12-31");
		var firstTime = true;
		var mPrev = stringToMoment($scope.calendarDisplayYear + "-01-01");
		
		if (dateCountArray.length > 0) {
			for (idx in dateCountArray) {
				var mNew = stringToMoment(dateCountArray[idx].day);
				var cnt = parseInt(dateCountArray[idx].count);
				if (mNew.year() == $scope.calendarDisplayYear) {
					if (mStart.isBefore(mNew) && firstTime) {
						pushToCalendar(mStart, null);
						mPrev = mStart;
						firstTime = false;
					}
					
					var days = mPrev.diff(mNew, 'days');
					if (days < 1) {
						fillInEmptyDates(moment(mPrev).add(1, "days"), moment(mNew).subtract(1, "days"));
					}
					console.log("pushing: "+ mNew.format() + " count: " + cnt);
					pushToCalendar(mNew, cnt);
					mPrev = mNew;
				}
			}
			
			if (mPrev.isBefore(mEnd)) {
				fillInEmptyDates(moment(mPrev).add(1, "days"), mEnd);
			}
			
		} else {
			console.log("no data!");
			var mStart = stringToMoment($scope.calendarDisplayYear + "-01-01");
			var mEnd = stringToMoment($scope.calendarDisplayYear + "-12-31");
			fillInEmptyDates(mStart, mEnd);
		}
	}
	
	function fillInEmptyDates(mStart, mEnd) {
		console.log("Filling dates INCLUDING " + mStart.format() + " and " + mEnd.format());
		var itr = moment.twix(mStart.toDate(), mEnd.toDate()).iterate("days");
		console.log(itr);
		while(itr.hasNext()){
			var mmnt = itr.next();
			pushToCalendar(mmnt, null);
		}
	}
	
	function pushToCalendar(date, count) {
		//date is a moment object. 
		var row = {
				c : [ {
					v : date.toDate()
				}, {
					v : count
				}, {v : '<div class="calToolTip"><p class="key">'+date.format('MM · DD · YYYY')+"</p>" + (count != null ? ("<p class='value'>Received: <strong>" + count + "<strong></p>") : "<p class='value'>no activity</p>") } ]
			};
		$scope.myChartObject.data.rows.push(row);
	}
	
	function stringToMoment(dateString) {
		return new moment(dateString, "YYYY-MM-DD", true);
	}
	
	$scope.handleChartClick = function(selectedItem) {
		console.log("handleChartClick");
        console.log(selectedItem);
        if (selectedItem) {
//        		&& selectedItem.row >= 0) {
        	var selectedDate = convertChartDateToLocalDate(selectedItem.date);
        	$scope.searchOptions.date = selectedDate;
        	$scope.calendarDisplayYear = new moment($scope.searchOptions.date).year();
        	$scope.resultsMetaData.page = 1;
        	$scope.reloadPageData();
        }
      }

	//So...  for some reason google charts sends the date back as if it were in UTC time...
	function convertChartDateToLocalDate(chartDate) {
		var m = moment.utc(chartDate);
		var offsetMinutes = m.toDate().getTimezoneOffset();
		m.add(offsetMinutes, 'minutes');
		return m.toDate();
	}
	
	googleChartApiConfig.optionalSettings = { packages: ['corechart', 'calendar'] };
	googleChartApiConfig.version = '1.1';
	  
	$scope.myChartObject = {};
	$scope.myChartObject.type = "Calendar";
	$scope.myChartObject.data = {
	    	"cols": [
	    	         {id: "Date", label: "Day", type: "date"},
	    	         {id: "count", label: "Slices", type: "number"}, 
	    	         {id: "tooltip", type: 'string', role: 'tooltip', 'p': {'html': true}}
	    	         ], 
	        "rows": []
	    };

	 $scope.myChartObject.options = {
	        title: '',
	        
//	        height: 220,
	        calendar: { 
	        	cellSize: 20 ,
//	        	yearLabel : {display:'none', color:'grey', /*fontSize: 100*/},
//	        forceIFrame: true, //This doesn't seem to work.  
	        	focusedCellColor: {
	        		stroke: '#d3362d',
	        		strokeOpacity: 1,
	        		strokeWidth: 1,
	        	},
				 monthOutlineColor: {
				      stroke: 'grey',
				      strokeOpacity: 0.5,
				      strokeWidth: 1
				    }
	        },
	        
	        colorAxis: {colors:['#9AF3BF','#259253']},
	        tooltip: {isHtml: true},
//	        noDataPattern: {
//	            backgroundColor: '#76a7fa',
//	            color: '#a0c3ff'
//	          }, 
//	          calendar: {
//	              cellColor: {
//	                stroke: '#76a7fa',
//	                strokeOpacity: 0.5,
//	                strokeWidth: 1,
//	              }
//	            }
	    };
	
	function getMessageList() {
		console.log("getMessageList");
		$scope.listloaded = false;
//		$scope.resultsMetaData.messages = {};
		console.log("MessageListFactory.get")
		MessageListFactory.get({
			providerKey : $scope.provider.key,
			date : $filter('date')($scope.searchOptions.date, 'yyyyMMdd'),
			messages : $scope.resultsMetaData.elementsPerPage,
			page : $scope.resultsMetaData.page-1,
			filters: $filter('filterObjectToQueryString')($scope.searchOptions.filters)
		}, function(data) {
			$scope.resultsMetaData.messages = data.messageList;
			$scope.resultsMetaData.totalElements = data.totalMessages;
			$scope.listloaded = true;
			console.log("getMessageList message list loaded.");
//		}).$promise.then(function() {
		})
		
		console.log("getMessageList call started");
	};
	
//	$scope.clearFilter = function(name) {
//		console.log("clearFilter")
//		if (name == 'messageSearchText') {
//			$scope.searchOptions.filters.messageSearchText = null;
//		} else if (name == 'ackStatus') {
//			$scope.searchOptions.filters.ackStatus = null;
//		} else if (name == 'statisticId') {
//			$scope.searchOptions.filters.statisticId = null;
//		}
//		populateCalendar();
//		$scope.reloadPageData();
//	}
	
//	$scope.goDetail = function(messageId) {
//		console.log(messageId);
////		ui-sref="message-detail({id: message.id})"
//		$state.go('message-detail', {id: messageId});
//	}
	
//	//Modal calling:
//    $scope.openModal = function () {
//        var modalInstance = $uibModal.open({
//          templateUrl: 'app/list/message-list.modal.html',
//          size: 'md',
//          controller: 'MessageListModalController',
//          resolve: {
//        	  searchOptionsModal: function () {
//              return $scope.searchOptions;
//            }
//          }
//        });
//        
//        modalInstance.result.then(function(returnSearchOptions) {
//        	
//            $scope.searchOptions = returnSearchOptions;
//            
//            console.log($scope.searchOptions);
//            
//            $scope.resultsMetaData = {
//        			page:1,
//        			elementsPerPage:10,
//        			maxSize:10,
//        			totalElements:0,
//        			messages:[],
//        	};
//            console.log("Modal success...  looking up history and message list");
//            $scope.reloadPageData();
//            populateCalendar();
//        })
//    }
    
//	function lookupStatName(statId) {
//			var stats = $scope.statistics.statList;
//			for (idx in stats) {
//				if (stats[idx].statisticId == statId) {
//					return stats[idx].statisticName;
//				}
//			} 
//		return statId;
//	}
	
//	function lookupFacilityDateESSRStats() {
//		$scope.statsQueried = false;
//		$scope.statsFound = false;
//		ESSRFacilityDateStatsFactory.get({providerKey: $scope.provider.key, date:($filter('date')($scope.searchOptions.date, 'yyyyMMdd'))}, function(data) {
//			console.log("got the stats data...");
//			$scope.statistics = data;
//			console.log("finished the stats lookup. ");
//			console.log($scope.statistics);
//			$scope.statsQueried = true;
//			
//			$scope.statsFound = $scope.statistics.statList.length > 0;
//			console.log("statistic filter id: " + $scope.searchOptions.filters.statisticId);
//			$scope.filterStatName = lookupStatName($scope.searchOptions.filters.statisticId);
//			console.log("statistic filter name: " + $scope.filterStatName );
//			
////		}).$promise.then(function() {
//		
//		});
//	}
	
	
	 function activate() {
			console.log("Activating MESSAGE LIST Controller!");
			console.log("activate - data from ui-router resolve: "); 
			console.log($stateParams);
			var m = new moment($stateParams.date, "YYYY-MM-DD");
			$scope.resultsMetaData.page = parseInt($stateParams.page, 10);
			$scope.searchOptions.date = m.toDate();
			$scope.calendarDisplayYear = new moment($scope.searchOptions.date).year();
			
			var filters = $filter('filterObjectFromQueryString')($stateParams.filters);
			$scope.searchOptions.filters = filters;
			$scope.resultsMetaData.page = $stateParams.page;
			if (!$scope.resultsMetaData.page) {
				$scope.resultsMetaData.page = 1;
			} 
			$scope.provider.key = $stateParams.providerKey;

			console.log("activate - About to get Messages");
			getMessageList();
			console.log("activate - About to get ESSR stats");
			console.log("activate - About to get Message/Date history");
			populateCalendar();
	 };
	 
	 activate();
	 
	 
}])