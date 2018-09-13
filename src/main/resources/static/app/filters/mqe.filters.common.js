angular
.module('messageHubDemoApp')
.filter("filterObjectToQueryString", function () {
  return function (filterObject) {
//			console.log("filter object: ");
//			console.log(filterObject);
    var filtersString = '';
    for (property in filterObject) {
      if (angular.isNotUndefinedOrNullOrEmpty(filterObject[property])) {
//					console.log("filter object property["+property+"] value[" + filterObject[property]+"]");

        if (filtersString === '') {
          filtersString += property + '::' + filterObject[property];
        } else {
          filtersString += '|' + property + '::' + filterObject[property];
        }

      }
    }
    return filtersString;
  }
})

.filter("filterObjectFromQueryString", function () {
  return function (filterString) {

    if (typeof filterString != 'string') {

      return filterString;
    }
    var filtersObject = {};

    if (angular.isNotUndefinedOrNullOrEmpty(filterString)) {

      var filterPairs = filterString.split("|");

      for (pair in filterPairs) {

        if (angular.isNotUndefinedOrNullOrEmpty(filterPairs[pair])) {

          var paramAndVal = filterPairs[pair].split("::");

          if (angular.isNotUndefinedOrNullOrEmpty(paramAndVal)) {

            var name = paramAndVal[0];
            var value = paramAndVal[1];
            filtersObject[name] = value;
          }
        }
      }
    }
    return filtersObject;
  }
})

//	.filter("statNameLookup", function() {
//		return function(statId) {
//			
//			if (typeof statId == 'string') {
//				return 'Awesome Stat #' + statId;
//			}
//			
//			return statId;
//		}
//	})

.filter('byRange', [function () {
  return function (inputArray, fieldName, min, max) {
    var data = [];
    angular.forEach(inputArray, function (item) {
//					console.log("field value: " + item[fieldName] + " invoking by range with " + fieldName + " min: " + min + " max: "+ max);
      if (item[fieldName] > min && item[fieldName] < max) {
        data.push(item);
      }
    });
    return data;
  };
}])

.filter('immunizationStats', ['$filter', function ($filter) {
  return function (inputArray) {
    return $filter('byRange')(inputArray, 'statisticId', 20, 29);
  };
}])

.filter('respPartyStats', ['$filter', function ($filter) {
  return function (inputArray) {
    return $filter('byRange')(inputArray, 'statisticId', 14, 21);
  };
}])

.filter('personStats', ['$filter', function ($filter) {
  return function (inputArray) {
    return $filter('byRange')(inputArray, 'statisticId', 0, 15);
  };
}])

.filter('duration', function () {
  //Returns duration from milliseconds in hh:mm:ss format.
  return function (millseconds) {
    var seconds = Math.floor(millseconds / 1000);
    var h = 3600;
    var m = 60;
    var hours = Math.floor(seconds / h);
    var minutes = Math.floor((seconds % h) / m);
    var scnds = Math.floor((seconds % m));
    var timeString = '';
    if (scnds < 10) {
      scnds = "0" + scnds;
    }
    if (hours < 10) {
      hours = "0" + hours;
    }
    if (minutes < 10) {
      minutes = "0" + minutes;
    }

    timeString =
        (hours > '00' ? (hours + ' h ') : '') +
        (minutes > '00' ? (minutes + ' m ') : '') +
        (scnds > '00' ? (scnds + ' s') : '');

    if (!timeString) {
      return millseconds + ' ms';
    }
    return (timeString)// === '' ? (milliseconds + ' ms') : timeString );
  }
})


//This filter allows you to slice an array from a given starting point.
.filter('startFrom', function () {
  return function (input, start) {
    if (input) {
      start = +start;
      return input.slice(start);
    }
    return [];
  };
});
;
