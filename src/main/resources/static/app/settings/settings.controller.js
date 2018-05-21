angular.module('messageHubDemoApp')

.controller('SettingsController',
    ['$scope', 'SettingNameGetterAndSetter', 'NistUrlGetterAndSetter', 'NistConnectionStatusGetter', 'NistExceptionGetter', 'NistClearException',
      function ($scope, SettingNameGetterAndSetter, NistUrlGetterAndSetter, NistConnectionStatusGetter, NistExceptionGetter, NistClearException) {

//        $scope.messageIn = {
//    				user:""
//    				,profileCode:""
//    				,message:""
//        };
        
        $scope.smartyStreetsAuthID = "";
        $scope.smartyStreetsAPIKey = "";
        $scope.smartyStreetsActivation = "";
        
        $scope.nistURL = "";
        $scope.nistActivation = "";
        $scope.nistConnectionStatus = "";
        $scope.nistException = "";

        //$scope.messageEvaluation = {};

        activate();

        function activate() {
          console.log("SettingsController ACTIVATE");
           
          SettingNameGetterAndSetter.get({
              settingName : "smartyStreetsAuthID"
          }, function(data) {
              $scope.smartyStreetsAuthID = data.value;
              //console.log($scope.messageEvaluation);
          });
          
          SettingNameGetterAndSetter.get({
              settingName : "smartyStreetsAPIKey"
          }, function(data) {
              $scope.smartyStreetsAPIKey = data.value;
              //console.log($scope.messageEvaluation);
          });
          
          SettingNameGetterAndSetter.get({
              settingName : "smartyStreetsActivation"
          }, function(data) {
              $scope.smartyStreetsActivation = data.value;
              //console.log($scope.messageEvaluation);
          });
          
//          SettingNameGetterAndSetter.get({
//              settingName : "nistURL"
//          }, function(data) {
//              $scope.nistURL = data.value;
//          });
          
          NistUrlGetterAndSetter.get(function (data) {
              $scope.nistURL = data.url;
              //console.log(data.url);
          });
          
          NistConnectionStatusGetter.get(function (data) {
              $scope.nistConnectionStatus = data.status;
              //console.log(data);
          });
          
          NistExceptionGetter.get(function (data) {
              $scope.nistException = data.exception;
          });
          
          SettingNameGetterAndSetter.get({
              settingName : "nistActivation"
          }, function(data) {
              $scope.nistActivation = data.status;
              //console.log($scope.messageEvaluation);
          });
          
          
        }

        // submit an address to be cleansed (or find already-mapped address)
        $scope.ok = function () {
          console.log("Submitted");
          //console.log($scope.smartyStreetsActivation);
			
          SettingNameGetterAndSetter.save({settingName : "smartyStreetsAuthID"}, $scope.smartyStreetsAuthID, function (data) {
            if (data != null) {
              //$scope.messageEvaluation = data;
              $scope.error = false;
              //console.log($scope.messageEvaluation);
            } else {
              $scope.errorMessage = "An unknown error occurred saving the Auth ID.";
              $scope.error = true;
            }
          }, function (error) {
            $scope.error = true;
            $scope.errorMessage = error.statusText + " - " + error.data;
          });
          
          SettingNameGetterAndSetter.save({settingName : "smartyStreetsAPIKey"}, $scope.smartyStreetsAPIKey, function (data) {
            if (data != null) {
              //$scope.messageEvaluation = data;
              $scope.error = false;
            } else {
              $scope.errorMessage = "An unknown error occurred saving the API key.";
              $scope.error = true;
            }
          }, function (error) {
            $scope.error = true;
            $scope.errorMessage = error.statusText + " - " + error.data;
          });
          
//          SettingNameGetterAndSetter.save({settingName : "nistURL"}, $scope.nistURL, function (data) {
//            if (data != null) {
//              $scope.error = false;
//            } else {
//              $scope.errorMessage = "An unknown error occurred saving the NIST URL value.";
//              $scope.error = true;
//            }
//          }, function (error) {
//            $scope.error = true;
//            $scope.errorMessage = error.statusText + " - " + error.data;
//          });
          
          NistUrlGetterAndSetter.save($scope.nistURL, function (data) {
            if (data != null) {
              NistClearException.save(function (data) {
	            if (data == null) {
	              $scope.error = false;
	            } else {
	              $scope.errorMessage = "An unknown error occurred clearing the exception.";
	              $scope.error = true;
	            }
	          }, function (error) {
	            $scope.error = true;
	            $scope.errorMessage = error.statusText + " - " + error.data;
	          });
              $scope.error = false;
            } else {
              $scope.errorMessage = "An unknown error occurred saving the NIST URL value.";
              $scope.error = true;
            }
          }, function (error) {
            $scope.error = true;
            $scope.errorMessage = error.statusText + " - " + error.data;
          });
          
          SettingNameGetterAndSetter.save({settingName : "nistActivation"}, $scope.nistActivation, function (data) {
            if (data != null) {
              //$scope.messageEvaluation = data;
              $scope.error = false;
            } else {
              $scope.errorMessage = "An unknown error occurred saving the activation value.";
              $scope.error = true;
            }
          }, function (error) {
            $scope.error = true;
            $scope.errorMessage = error.statusText + " - " + error.data;
          });
          
          if ($scope.nistActivation == "enable") {
          
          
          }
          else {
          
          
          }
        };
      }]);
