angular.module('messageHubDemoApp')

.controller('LandingPageController', ['$scope', '$state', '$filter', 'googleChartApiConfig', 'urlParams', 'FacilityList','MessageCountHistoryFactory',
                                     function($scope, $state, $filter, googleChartApiConfig, urlParams, FacilityList , MessageCountHistoryFactory){
	$scope.provider = {pin:""};
	$scope.providerList = [];
	$scope.providerChosen = false;
	$scope.searchOptionsForm = {
			date: new Date(), 
			filters: {
			}
	}
	$scope.ready = true;
	$scope.calendarStatus = {
			one: {ready: false},
			two: {ready: false}
	};
	
	activate();
	
	function activate() {
		console.log("LANDING PAGE");
	}
	
	$scope.search = function() {
		if ($scope.searchOptionsForm.provider && $scope.searchOptionsForm.date) {
			$state.go('message-list', {providerKey: $scope.provider, date:($filter('date')($scope.searchOptionsForm.date, 'yyyyMMdd')) }, {notify: true});
		}
		
		console.log("going to message-list");
	}
	
	$scope.getProviderHistory = function() {
		$scope.providerChosen = false;
		$scope.calendarStatus.one.ready = false;
		$scope.calendarStatus.two.ready = false;
		$scope.providerChosen = true;
		$scope.provider = $scope.searchOptionsForm.provider;
//		console.log($scope.provider);
		$scope.populateCalendar(1);
		$scope.populateCalendar(2);
	}
	
	$scope.populateCalendar = function(calendarNumber) {
//		console.log("Getting calendar " + calendarNumber);
		//expects either a one or two. 
		var calendarJunk = {};
		if (calendarNumber == 2) {
			resetRowsCalendar2();
			calendarJunk = {
					chart: $scope.myChart2Object,
					year: new Date().getFullYear() - 1, 
					calendarNumber: 2, 
					status: $scope.calendarStatus.two
			}
		} else if (calendarNumber == 1) {
			resetRowsCalendar1();
			calendarJunk = {
					chart: $scope.myChartObject,
					year: new Date().getFullYear(),
					calendarNumber: 1, 
					status: $scope.calendarStatus.one
			}
		}
		makeCalendar(calendarJunk);
		
	}
	
	function makeCalendar(calendarJunk) {
		MessageCountHistoryFactory.get({
				providerKey: $scope.searchOptionsForm.provider, 
				year: calendarJunk.year 
			}, 
			function(dataIn) {}).$promise.then(function(dataIn) {
				delete dataIn.$promise;
		        delete dataIn.$resolved;
		        var historyData = dataIn.messageHistory;
		        calendarJunk.chart.data.rows = dataToRows(historyData, calendarJunk.year);
		        console.log("Calendar rows are set!");
		        calendarJunk.status.ready = true;
			}, function(error) {
				$scope.errorMsg = "Error Looking up facility history!"
			});
	}
	
	function dataToRows(dataIn, year) {
		var rows = [];
		var mStart = stringToMoment(year + "-01-01");
		var mEnd = stringToMoment(year + "-12-31");
		var mPrev = stringToMoment(year + "-01-01"); 
		
		var firstTime = true;
		
       if (dataIn.length > 0) {
			for (idx in dataIn) {
				var mNew = stringToMoment(dataIn[idx].day);
				var count = parseInt(dataIn[idx].count);
				
				if (mStart.isBefore(mNew) && firstTime) {
//					console.log("START!"+mStart.format());
					rows.push(makeCalendarEntry(mStart,null));
					firstTime = false;
				}
				
				var days = mPrev.diff(mNew, 'days');
				
//				console.log("days diff: " + days);
				
				if (days < 1) {
					var fillRows = fillInEmptyDates(moment(mPrev).add(1, "days"), moment(mNew).subtract(1, "days"));
					rows = rows.concat(fillRows);
				}
				
				var row = makeCalendarEntry(mNew,count);
				rows.push(row);
				mPrev = mNew;
			}
			
			if (mPrev.isBefore(mEnd)) {
				var fillRows = fillInEmptyDates(moment(mPrev).add(1, "days"), mEnd);
				rows = rows.concat(fillRows);
			}
			
       }  else {
    	   rows = fillInEmptyDates(mStart, mEnd);
   		}
	   return rows;    
	} 
	
	function stringToMoment(dateString) {
		return new moment(dateString, "YYYY-MM-DD", true);
	}
	
	function makeCalendarEntry(date, count) {
		//date is a moment object. 
		var row = {
				c : [ {
					v : date.toDate()
				}, {
					v : count
				}, {v : '<div class="calToolTip"><p class="key">'+date.format('MM · DD · YYYY')+"</p>" + (count != null ? ("<p class='value'>Received: <strong>" + count + "<strong></p>") : "<p class='value'>no activity</p>") } ]
			};
		return row;
	}
	
	
	function fillInEmptyDates(mStart, mEnd) {
		var rows = [];
//		console.log("Filling dates INCLUDING " + mStart.format() + " and " + mEnd.format());
		var itr = moment.twix(mStart.toDate(), mEnd.toDate()).iterate("days");
//		console.log(itr);
		while(itr.hasNext()){
			var mmnt = itr.next();
			rows.push(makeCalendarEntry(mmnt, null));
		}
		return rows;
	}
	
	
	$scope.handleChartClick = function(selectedItem) {
        if (selectedItem && selectedItem.row >= 0) {
        	var m = moment.utc(selectedItem.date);
        	//So...  for some reason google charts sends the date back as if it were in UTC time... 
        	var offset = m.toDate().getTimezoneOffset()*60*1000;
        	m.add(offset);
        	$scope.searchOptionsForm.date = m.toDate();
        	$scope.search();
        }
    }	

	FacilityList.query(function(facilities) {
        $scope.providerList = facilities;
    	}).$promise.then(function () {
		if (urlParams.pin) {
			var list = $scope.providerList;
			for (provider in list) {
				if (provider == urlParams.pin) {
					$scope.searchOptionsForm.provider = provider;
					$scope.getProviderHistory();
					return;
				}
			}
		}
		if ($scope.providerList.length == 1) {
			$scope.searchOptionsForm.provider = $scope.providerList[0];
			$scope.getProviderHistory();
		}
	});
	
	
	function resetRows() {
		resetRowsCalendar1();
		resetRowsCalendar2();
	}
	
	function resetRowsCalendar1() {
		$scope.myChartObject.data.rows = [{
			c : [ {
				v : new moment('2017-01-01', "YYYY-MM-DD", true).toDate()
			}, {
				v : null
			}, {v : '<div class="calToolTip"><p class="key">'+new moment('2016-01-01', "YYYY-MM-DD", true).format('MM · DD · YYYY')+"</p><p class='value'>no activity</p>"} ]
		}];
	}
	
	function resetRowsCalendar2() {
		$scope.myChart2Object.data.rows = [{
			c : [ {
				v : new moment('2016-01-01', "YYYY-MM-DD", true).toDate()
			}, {
				v : null
			}, {v : '<div class="calToolTip"><p class="key">'+new moment('2016-01-01', "YYYY-MM-DD", true).format('MM · DD · YYYY')+"</p><p class='value'>no activity</p>"} ]
		}];
	}
	
	googleChartApiConfig.optionalSettings = { packages: ['corechart', 'calendar'] };
	googleChartApiConfig.version = '1.1';
	  
	//This year's calendar. 
	$scope.myChartObject = {};
	    
	$scope.myChartObject.type = "Calendar";
	    
	    $scope.myChartObject.data = {
	    	"cols": [
	    	         {id: "date", label: "Date", type: "date"},
	    	         {id: "count", label: "Message Count", type: "number"},
	    	         {id: "tooltip", type: 'string', role: 'tooltip', 'p': {'html': true}}
	    	         ], 
	        "rows": []
	    };

	    $scope.myChartObject.options = {
//	        title: 'Recent Activity',
	        
	        height: 200,
	        calendar: { cellSize: 19 , 
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
	    };
	    
	    //Last year's calendar. 
	    $scope.myChart2Object = {};
	    
		$scope.myChart2Object.type = "Calendar";
		    
		    $scope.myChart2Object.data = {
		    	"cols": [
		    	         {id: "date", label: "Date", type: "date"},
		    	         {id: "count", label: "Message Count", type: "number"},
		    	         {id: "tooltip", type: 'string', role: 'tooltip', 'p': {'html': true}}], 
		        "rows": [{
					c : [ {
						v : new moment('2016-01-01', "YYYY-MM-DD", true).toDate()
					}, {
						v : null
					}, {v : '<div class="calToolTip"><p class="key">'+new moment('2016-01-01', "YYYY-MM-DD", true).format('MM · DD · YYYY')+"</p><p class='value'>no activity</p>"} ]
				}]
		    };

		    $scope.myChart2Object.options = {
		        title: '',
		        height: 200,
		        calendar: { cellSize: 19 , 
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
		    };
	
	
}])