angular.module('messageHubDemoApp')

.factory('MessageCountHistoryFactory', function ($resource) {
  return $resource('provider/:providerKey/counts/year/:year');
})

.factory('MessageListFactory', function ($resource) {
  return $resource(
      'messages/:providerKey/date/:date/messages/:messages/page/:page/?filters=:filters');
})

.factory('MessageDetailFactory', function ($resource) {
  return $resource('messages/:id');
})

// .factory("FacilityDetail", function($resource, $stateParams) {
//   return {
//     getFacility: function(urlParts) {
//       console.log(urlParts);
//       return $resource('facilities/pin/:pin').get({pin: urlParts.pin})
//       .$promise.then(function(data) {
//         var returnData = {'urlParts':urlParts ,'facility':data};
//         console.log("about to return some great data: ");
//         console.log(returnData);
//         return returnData;
//       });
//     }
//   }
// })

.factory('FacilityList', function ($resource) {
  return $resource('facilities/');
})

.factory('Hl7JsonPoster', function ($resource) {
  return $resource('messages/json/notsaved');//posts a json object
})

.factory('Hl7JsonExample', function ($resource) {
  return $resource('messages/json/example');//gets a json object with an example message
})

.factory('ReportExample', function ($resource) {
  return $resource('report/demo');//gets a json object with an example message
})

.factory('Reporter', function ($resource) {
  return $resource('report/:providerKey/date/:date');//gets a json object with an example message
})

.factory('ReportMessage', function ($resource) {
  return $resource('report/message');//gets a json object with an example message
})

.directive('fileModel', ['$parse', function ($parse) {
  return {
    restrict: 'A',
    link: function (scope, element, attrs) {
      var model = $parse(attrs.fileModel);
      var modelSetter = model.assign;

      element.bind('change', function () {
        scope.$apply(function () {
          modelSetter(scope, element[0].files[0]);
        });
      });
    }
  };
}])

.service('fileUpload', ['$http', function ($http) {
  this.uploadFileToUrl = function (file) {
    var fd = new FormData();
    fd.append('file', file);

    return $http.post("file/upload-messages", fd, {
      transformRequest: angular.identity,
      headers: {'Content-Type': undefined}
    });
  };

  this.initiateFileProcess = function (fileId) {
    var fd = new FormData();
    fd.append('fileId', fileId);
    return $http.post("file/process-file", fd, {
      transformRequest: angular.identity,
      headers: {'Content-Type': undefined}
    });
  };

  this.reportFileProcess = function (fileId) {
    return $http.get("file/report-file?fileId=" + fileId);
  };

  this.pauseFileProcess = function (fileId) {
    return $http.get("file/stop-file?fileId=" + fileId);
  };

  this.unpauseFileProcess = function (fileId) {
    return $http.get("file/unpause-file?fileId=" + fileId);
  };

  this.getAcks = function (fileId) {
    return $http.get("file/report-acks?fileId=" + fileId);
  };
  this.removeFile = function (fileId) {
    return $http.get("file/remove-file?fileId=" + fileId);
  };
  this.getQueues = function () {
    return $http.get("file/get-queues");
  };
}])

.factory('MessageResult', function ($resource) {
  return $resource('messages/msg-result');//gets a string with ack info
})

.factory('MessageDownload', function ($resource) {
  return $resource('messages/msg-download');//gets a string with ack info
})

.factory('Coder', function ($resource) {
  return $resource('codes/:providerKey/:dateStart/:dateEnd');//gets a collection of codes from the provider
})

.factory('VaccineCodes', function ($resource) {
  return $resource('codes/vaccinations/:providerKey/:dateStart/:dateEnd');//gets a collection of codes from the provider
})

.directive('fileModel', ['$parse', function ($parse) {
  return {
    restrict: 'A',
    link: function (scope, element, attrs) {
      var model = $parse(attrs.fileModel);
      var modelSetter = model.assign;

      element.bind('change', function () {
        scope.$apply(function () {
          modelSetter(scope, element[0].files[0]);
        });
      });
    }
  };
}])

.filter('newline', function ($sce) {
  return function (text) {
    return text ? $sce.trustAsHtml(text.replace(/\r/g, '<br/>')) : '';
  };
})

.factory('SettingNameGetterAndSetter', function ($resource) {
  return $resource('settings/name/:settingName');//gets and posts setting information to database
})

.factory('NistUrlGetterAndSetter', function ($resource) {
  return $resource('nist/validator/url');//gets and sets nist validator url
})

.factory('NistConnectionStatusGetter', function ($resource) {
  return $resource('nist/validator/connection');//gets the nist validator connection status
})

.factory('NistExceptionGetter', function ($resource) {
  return $resource('nist/validator/exception');//gets the nist validator exception
})

.factory('NistClearException', function ($resource) {
  return $resource('nist/validator/clear-exception');//clears the nist validator exception
})

;
