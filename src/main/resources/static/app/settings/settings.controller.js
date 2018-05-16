angular.module('messageHubDemoApp')

.controller('SettingsController',
    ['$scope', 'SettingNameGetterAndSetter',
      function ($scope, SettingNameGetterAndSetter) {

        $scope.messageIn = {
//    				user:""
//    				,profileCode:""
//    				,message:""
        };
        
        $scope.smartyStreetsAuthID = "";
        $scope.smartyStreetsAPIKey = "";
        $scope.smartyStreetsActivation = "";

        $scope.messageEvaluation = {};

        activate();

        function activate() {
          console.log("SettingsController ACTIVATE");
          $scope.nistURL = "https://not a good url/";
        }

        // submit an address to be cleansed (or find already-mapped address)
        $scope.ok = function () {
          console.log("Submitted");
          //console.log($scope.smartyStreetsActivation);
          
          SettingNameGetterAndSetter.get({
              settingName : "smartyStreetsActivation"
          }, function(data) {
              $scope.messageEvaluation = data;
              //console.log($scope.messageEvaluation);
	//    }).$promise.then(function() {
          });
			
          SettingNameGetterAndSetter.save({settingName : "smartyStreetsAuthID"}, $scope.smartyStreetsAuthID, function (data) {
            if (data != null) {
              $scope.messageEvaluation = data;
              $scope.error = false;
              console.log($scope.messageEvaluation);
            } else {
              $scope.errorMessage = "An unknown error occurred.";
              $scope.error = true;
            }
          }, function (error) {
            $scope.error = true;
            $scope.errorMessage = error.statusText + " - " + error.data;
          });
        };
      }]);
