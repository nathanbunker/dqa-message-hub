angular.module('messageHubDemoApp')

.factory('MessageCountHistoryFactory', function ($resource) {
  return $resource('api/provider/:providerKey/counts/year/:year');
})

.factory('MessageListFactory', function ($resource) {
  return $resource(
      'messages/:providerKey/date/:date/messages/:messages/page/:page/?filters=:filters');
})

.factory('MessageDetailFactory', function ($resource) {
  return $resource('api/messages/:id');
})

// .factory("FacilityDetail", function($resource, $stateParams) {
//   return {
//     getFacility: function(urlParts) {
//       console.log(urlParts);
//       return $resource('api/facilities/pin/:pin').get({pin: urlParts.pin})
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
  return $resource('api/facilities/');
})

.factory('Hl7JsonPoster', function ($resource) {
  return $resource('api/messages/json/notsaved');//posts a json object
})

.factory('Hl7JsonExample', function ($resource) {
  return $resource('api/messages/json/example');//gets a json object with an example message
})

.factory('ReportExample', function ($resource) {
  return $resource('api/report/demo');//gets a json object with an example message
})

.factory('Reporter', function ($resource) {
  return $resource('api/report/:providerKey/date/:date');//gets a json object with an example message
})

.factory('ReportMessage', function ($resource) {
  return $resource('api/report/message');//gets a json object with an example message
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

    return $http.post("api/file/upload-messages", fd, {
      transformRequest: angular.identity,
      headers: {'Content-Type': undefined}
    });
  };

  this.initiateFileProcess = function (fileId) {
    var fd = new FormData();
    fd.append('fileId', fileId);
    return $http.post("api/file/process-file", fd, {
      transformRequest: angular.identity,
      headers: {'Content-Type': undefined}
    });
  };

  this.reportFileProcess = function (fileId) {
    return $http.get("api/file/report-file?fileId=" + fileId);
  };

  this.pauseFileProcess = function (fileId) {
    return $http.get("api/file/stop-file?fileId=" + fileId);
  };

  this.unpauseFileProcess = function (fileId) {
    return $http.get("api/file/unpause-file?fileId=" + fileId);
  };

  this.getAcks = function (fileId) {
    return $http.get("api/file/report-acks?fileId=" + fileId);
  };
  this.removeFile = function (fileId) {
    return $http.get("api/file/remove-file?fileId=" + fileId);
  };
  this.getQueues = function () {
    return $http.get("api/file/get-queues");
  };
}])

.factory('MessageResult', function ($resource) {
  return $resource('api/messages/msg-result');//gets a string with ack info
})

.factory('MessageDownload', function ($resource) {
  return $resource('api/messages/msg-download');//gets a string with ack info
})

.factory('Coder', function ($resource) {
  return $resource('api/codes/:providerKey/:dateStart/:dateEnd');//gets a collection of codes from the provider
})

.factory('VaccineCodes', function ($resource) {
  return $resource('api/codes/vaccinations/:providerKey/:dateStart/:dateEnd');//gets a collection of codes from the provider
})

.factory('VaccineCodesExpected', function ($resource) {
  return $resource('api/codes/vaccinationsExpected/:providerKey/:dateStart/:dateEnd');
})

.factory('VaccineCodesNotExpected', function ($resource) {
  return $resource('api/codes/vaccinationsNotExpected/:providerKey/:dateStart/:dateEnd');
})

.factory('VaccineReportGroupList', function ($resource) {
  return $resource('api/codes/vaccineReportGroupList/:providerKey');
})

.factory('AgeCategoryList', function ($resource) {
  return $resource('api/codes/ageCategoryList/:providerKey');
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
  return $resource('api/settings/name/:settingName');//gets and posts setting information to database
})

.factory('NistUrlGetterAndSetter', function ($resource) {
  return $resource('api/nist/validator/url');//gets and sets nist validator url
})

.factory('NistConnectionStatusGetter', function ($resource) {
  return $resource('api/nist/validator/connection');//gets the nist validator connection status
})

.factory('NistExceptionGetter', function ($resource) {
  return $resource('api/nist/validator/exception');//gets the nist validator exception
})

.factory('NistClearException', function ($resource) {
  return $resource('api/nist/validator/clear-exception');//clears the nist validator exception
})

.factory('CodeFactory', function($resource) {
  return $resource('api/codes/:codesetType');
})


.factory('CodeTypeFactory', function($resource) {
  return $resource('api/codes/');
})

;
