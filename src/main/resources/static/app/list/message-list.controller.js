angular.module('messageHubDemoApp')

.controller('MessageListController',
    ['$scope', '$state', '$uibModal', '$filter', '$location', 'pageData',
      'googleChartApiConfig', 'MessageCountHistoryFactory',
      'MessageListFactory', 'Reporter', '$q', 'Coder', 'VaccineCodes', 
      'VaccineCodesExpected', 'VaccineCodesNotExpected', 'VaccineReportGroupList', 'AgeCategoryList',
      function ($scope, $state, $uibModal, $filter, $location, pageData,
          googleChartApiConfig, MessageCountHistoryFactory, MessageListFactory,
          Reporter, $q, Coder, VaccineCodes, VaccineCodesExpected, VaccineCodesNotExpected,
          VaccineReportGroupList, AgeCategoryList) {

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
        $scope.provider = {key: ""};
        $scope.calendarPopulated = false;
        $scope.historyData = [];
        $scope.emptyRows = [];
        $scope.calendarDisplayYear = '';
        $scope.resultsMetaData = {
          page: 0,
          elementsPerPage: 10,
          maxSize: 10,
          totalElements: 0,//Don't modify this except with the actual list length. Modifying this causes a page reload...
          messages: [],
        };
        $scope.report = {};
        $scope.codesMap = {
          test: [{
            "type": "Vaccination Information Source",
            "attribute": "RXA-9",
            "value": "02",
            "count": 1,
            "status": "Valid",
            "label": "Historical information - from other provider"
          }]
        };
        $scope.vaccinationsMap = {
          test: [{
            "vaccinationVisits": 0,
            "count": 6,
            "status": "Possible",
            "vaccine": "HepB",
            "percent": 0.0,
            "age": "OTHER"
          }]
        };
        $scope.vaccinationsExpectedMap = {
                test: [{
                  "vaccinationVisits": 0,
                  "count": 6,
                  "status": "Possible",
                  "vaccine": "HepB",
                  "percent": 0.0,
                  "age": "Adolescent"
                }]
              };
        $scope.vaccinationsNotExpectedMap = {
                test: [{
                  "vaccinationVisits": 0,
                  "count": 6,
                  "status": "Possible",
                  "vaccine": "HepB",
                  "percent": 0.0,
                  "age": "Adolescent"
                }]
              };

        function lookupDetectionName(detectionId) {
          var dList = $scope.report.detectionCounts;
          var filterDetectionName = "";
          for (dtc in dList) {
            dx = dList[dtc];
            if (detectionId === dx.applicationErrorCode.alternateIdentifier) {
              filterDetectionName = dx.applicationErrorCode.alternateText;
              break;
            }
          }
          return filterDetectionName;
        }

        $scope.toggleDetection = function (detectionId) {
          $scope.searchOptions.filters.detectionId = detectionId;
          $scope.filterDetectionName = lookupDetectionName(detectionId);
          // populateCalendar();
          // $scope.reloadPageData();
          populateURL();
          loadMessages();
          $scope.activeJustified = 0;
          // $scope.showStats = false;
        };

        $scope.toggleCodeFilter = function (codeValue, codeType) {
          $scope.searchOptions.filters.codeValue = codeValue;
          $scope.searchOptions.filters.codeType = codeType;
          // populateCalendar();
          // $scope.reloadPageData();
          populateURL();
          loadMessages();
          $scope.activeJustified = 0;
          // $scope.showStats = false;
        };

        $scope.readyHandler = function (chartWrapper) {
          console.log("Message list Controller.  READY HANDLER");
          $scope.calendarChartWrapper = chartWrapper;
        };

        $scope.goToSiteSearch = function () {
          $window.location.href = '/#/messages/';
        };

        $scope.incrementDate = function (incAmt) {
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
            if (entries[idx].day === workingDate.format("YYYY-MM-DD")) {
              var nextIndex = parseInt(idx) + parseInt(incAmt);
              console.log("incrementDate: index: " + idx + " next index: "
                  + nextIndex + " max: " + max + " incAmt: " + incAmt);
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
            console.log("incrementDate:  current display year: "
                + $scope.calendarDisplayYear);

            var calendarPromise = changeYear(incAmt);
            calendarPromise.then(function () {
              var index = $scope.historyData.length - 1;
              if (directionNext) {
                index = 0;
              }
              var entry = $scope.historyData[index];
              console.log(entry);
              next = new moment(entry.day);
              repopulatePageForDate(next.toDate());
            });
          }
        };

        function repopulatePageForDate(date) {
          $scope.searchOptions.date = date;
          //now render the new date's stuff.
          $scope.resultsMetaData.page = 1;
          $scope.reloadPageData();
        }

        $scope.listloaded = false;

        $scope.pagingClicked = function () {
          console.log("PAGING!!!");
          $scope.reloadPageData();
        }

        $scope.reloadPageData = function () {
          console.log("Loading New Page Data...");
          populateURL();
          getMessageList();
        };

        function populateURL() {
          console.log("filters: ");
          console.log($scope.searchOptions.filters);
          var filterString = $filter('filterObjectToQueryString')(
              $scope.searchOptions.filters);
          var formattedDate = $filter('date')($scope.searchOptions.date,
              'yyyyMMdd');
          console.log("URL CHANGE START: provider[" + $scope.provider.key
              + "] filter[" + filterString + "] page["
              + $scope.resultsMetaData.page + "] date[" + formattedDate + "]");
          $state.go('.', {
            providerKey: $scope.provider.key,
            date: formattedDate,
            filters: (filterString),
            page: $scope.resultsMetaData.page
          }, {notify: false}, {reload: false});
          console.log("URL CHANGE END  : provider[" + $scope.provider.key
              + "] filter[" + filterString + "] page["
              + $scope.resultsMetaData.page + "] date[" + formattedDate + "]");
        }

        function clearChartData() {
          $scope.myChartObject.data.rows = [];
        }

        $scope.prevYear = function () {
          changeYear(-1);
        };

        $scope.nextYear = function () {
          changeYear(1);
        };

        function changeYear(inc) {
          if (inc > 0 && $scope.calendarDisplayYear < $scope.currentYear) {
            console.log("moving to nextYear.  current display year: "
                + $scope.calendarDisplayYear);
            $scope.calendarDisplayYear += 1;
          } else if (inc < 0 && $scope.calendarDisplayYear > 2010) {
            $scope.calendarDisplayYear += -1;
          } else {
            console.log("NO NEXT YEAR");
          }
          return populateCalendar();
        }

        function populateCalendar() {
          console.log("POPULATING calendar");
          $scope.calendarPopulated = false;

          var deferred = $q.defer();
          MessageCountHistoryFactory.get({
            providerKey: $scope.provider.key,
            year: $scope.calendarDisplayYear,
            filters: $filter('filterObjectToQueryString')(
                $scope.searchOptions.filters)
          }, function (dataIn) {
          }).$promise.then(function (dataIn) {
            delete dataIn.$promise;
            delete dataIn.$resolved;
            console.log(dataIn);
            $scope.historyData = dataIn.messageHistory;
            console.log('populateCalendar - ');
            console.log($scope.historyData);
            pushHistoryDataToCalendar();
            $scope.calendarPopulated = true;
            deferred.resolve();
          });

          return deferred.promise;
        }

        function pushHistoryDataToCalendar() {
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
                  fillInEmptyDates(moment(mPrev).add(1, "days"),
                      moment(mNew).subtract(1, "days"));
                }
                console.log("pushing: " + mNew.format() + " count: " + cnt);
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
          console.log("Filling dates INCLUDING " + mStart.format() + " and "
              + mEnd.format());
          var itr = moment.twix(mStart.toDate(), mEnd.toDate()).iterate("days");
          console.log(itr);
          while (itr.hasNext()) {
            var mmnt = itr.next();
            pushToCalendar(mmnt, null);
          }
        }

        function pushToCalendar(date, count) {
          //date is a moment object.
          var row = {
            c: [{
              v: date.toDate()
            }, {
              v: count
            }, {
              v: '<div class="calToolTip"><p class="key">' + date.format(
                  'MM · DD · YYYY') + "</p>" + (count != null
                  ? ("<p class='value'>Received: <strong>" + count
                      + "<strong></p>") : "<p class='value'>no activity</p>")
            }]
          };
          $scope.myChartObject.data.rows.push(row);
        }

        function stringToMoment(dateString) {
          return new moment(dateString, "YYYY-MM-DD", true);
        }

        $scope.handleChartClick = function (selectedItem) {
          console.log("handleChartClick");
          console.log(selectedItem);
          if (selectedItem) {
//        		&& selectedItem.row >= 0) {
            var selectedDate = convertChartDateToLocalDate(selectedItem.date);
            $scope.searchOptions.date = selectedDate;
            $scope.calendarDisplayYear = new moment(
                $scope.searchOptions.date).year();
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

        googleChartApiConfig.optionalSettings = {
          packages: ['corechart', 'calendar']
        };
        googleChartApiConfig.version = '1.1';

        $scope.myChartObject = {};
        $scope.myChartObject.type = "Calendar";
        $scope.myChartObject.data = {
          "cols": [
            {id: "Date", label: "Day", type: "date"},
            {id: "count", label: "Slices", type: "number"},
            {
              id: "tooltip",
              type: 'string',
              role: 'tooltip',
              'p': {'html': true}
            }
          ],
          "rows": []
        };

        $scope.myChartObject.options = {
          title: '',

//	        height: 220,
          calendar: {
            cellSize: 20,
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

          colorAxis: {colors: ['#9AF3BF', '#259253']},
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

        function loadMessages() {
          console.log("getMessageList");
          $scope.listloaded = false;
//		$scope.resultsMetaData.messages = {};
          console.log("MessageListFactory.get");
          MessageListFactory.get({
            providerKey: $scope.provider.key,
            date: $filter('date')($scope.searchOptions.date, 'yyyyMMdd'),
            messages: $scope.resultsMetaData.elementsPerPage,
            page: $scope.resultsMetaData.page - 1,
            filters: $filter('filterObjectToQueryString')($scope.searchOptions.filters)
          }, function (data) {
            $scope.resultsMetaData.messages = data.messageList;
            $scope.resultsMetaData.totalElements = data.totalMessages;
            $scope.listloaded = true;
            console.log("getMessageList message list loaded.");
//		}).$promise.then(function() {
          });
        }

        function getReport() {
          console.log("Reporter.get");
          $scope.report = {};
          Reporter.get({
            providerKey: $scope.provider.key,
            date: $filter('date')($scope.searchOptions.date, 'yyyyMMdd')
          }, function (data) {
            $scope.report = data;
            console.log("looking up filter detection name for " + $scope.searchOptions.filters.detectionId);
            $scope.filterDetectionName = lookupDetectionName($scope.searchOptions.filters.detectionId);
            console.log("report data loaded.");
//		}).$promise.then(function() {
          });
        }

        function getCodes() {
          Coder.get({
            providerKey: $scope.provider.key,
            dateStart: $filter('date')($scope.searchOptions.date, 'yyyyMMdd'),
            dateEnd: $filter('date')($scope.searchOptions.date, 'yyyyMMdd')
          }, function (data) {

            $scope.codesMap = data;
            //alert(codeTypeArray);
            console.log("getMessageList codes list loaded.");
//		}).$promise.then(function() {
          });
        }

        function getVaccines() {
          VaccineCodes.get({
            providerKey: $scope.provider.key,
            dateStart: $filter('date')($scope.searchOptions.date, 'yyyyMMdd'),
            dateEnd: $filter('date')($scope.searchOptions.date, 'yyyyMMdd')
          }, function (data) {

            $scope.vaccinationsMap = data;
            $scope.vaccinationsAge = Object.keys(data.map);
            //alert(codeTypeArray);
            console.log("getMessageList vaccines list loaded.");
//		}).$promise.then(function() {
          });
        }

        function getVaccineReportGroupList() {
            VaccineReportGroupList.query({
              providerKey: $scope.provider.key
            }, function (data) {
              $scope.vaccineReportGroupList = data;
              console.log("getVaccineReportGroupList vaccine group list added.");
            });
          }
        
        function getAgeCategoryList() {
        	AgeCategoryList.query({
              providerKey: $scope.provider.key
            }, function (data) {
              $scope.ageCategoryList = data;
              console.log("getAgeCategoryList age category list added.");
            });
          }

        function getVaccinesExpected() {
            VaccineCodesExpected.get({
              providerKey: $scope.provider.key,
              dateStart: $filter('date')($scope.searchOptions.date, 'yyyyMMdd'),
              dateEnd: $filter('date')($scope.searchOptions.date, 'yyyyMMdd')
            }, function (data) {

              $scope.vaccinationsExpectedMap = data;
              //alert(codeTypeArray);
              console.log("getMessageList vaccines expected list loaded.");
//  		}).$promise.then(function() {
            });
          }

          function getVaccinesNotExpected() {
              VaccineCodesNotExpected.get({
                providerKey: $scope.provider.key,
                dateStart: $filter('date')($scope.searchOptions.date, 'yyyyMMdd'),
                dateEnd: $filter('date')($scope.searchOptions.date, 'yyyyMMdd')
              }, function (data) {

                $scope.vaccinationsExpectedMap = data;
                //alert(codeTypeArray);
                console.log("getMessageList vaccines not expected list loaded.");
//    		}).$promise.then(function() {
              });
            }


        function getMessageList() {
          loadMessages();
          getReport();
          getCodes();
          getVaccines();
          getVaccinesExpected();
          getVaccinesNotExpected();
          getVaccineReportGroupList();
          getAgeCategoryList();
         
          console.log("getMessageList call started");
        }

        $scope.clearFilter = function (name) {
          console.log("clearFilter");
          if (name === 'messageSearchText') {
            $scope.searchOptions.filters.messageSearchText = null;
          } else if (name === 'ackStatus') {
            $scope.searchOptions.filters.ackStatus = null;
          } else if (name === 'detectionId') {
            $scope.searchOptions.filters.detectionId = null;
          } else if (name === 'codeValue') {
            $scope.searchOptions.filters.codeValue = null;
            $scope.searchOptions.filters.codeType = null;
          }
          // populateCalendar();
          $scope.reloadPageData();
        };

//	//Modal calling:
        $scope.openModal = function () {
          var modalInstance = $uibModal.open({
            templateUrl: 'app/list/message-list.modal.html',
            size: 'md',
            controller: 'MessageListModalController',
            resolve: {
              searchOptionsModal: function () {
                return $scope.searchOptions;
              }
            }
          });

          modalInstance.result.then(function (returnSearchOptions) {

            $scope.searchOptions = returnSearchOptions;

            console.log($scope.searchOptions);

            $scope.resultsMetaData = {
              page: 1,
              elementsPerPage: 10,
              maxSize: 10,
              totalElements: 0,
              messages: [],
            };
            console.log(
                "Modal success...  looking up history and message list");
            $scope.reloadPageData();
            populateCalendar();
          })
        };

        function activate() {
          console.log("Activating MESSAGE LIST Controller!");
          console.log("activate - data from ui-router resolve: ");
          console.log(pageData);
          var m = new moment(pageData.date, "YYYY-MM-DD");
          $scope.resultsMetaData.page = parseInt(pageData.page, 10);
          $scope.searchOptions.date = m.toDate();
          $scope.calendarDisplayYear = new moment(
              $scope.searchOptions.date).year();

          var filters = $filter('filterObjectFromQueryString')(pageData.filters);
          $scope.searchOptions.filters = filters;
          $scope.resultsMetaData.page = pageData.page;
          if (!$scope.resultsMetaData.page) {
            $scope.resultsMetaData.page = 1;
          }
          $scope.provider.key = pageData.providerKey;

          console.log("activate - About to get Messages");
          getMessageList();
          console.log("activate - About to get ESSR stats");
          console.log("activate - About to get Message/Date history");
          populateCalendar();
        };

        activate();

      }])
