angular.module('messageHubDemoApp')

.controller('SettingsController',
    ['$scope', 'SettingNameGetterAndSetter', 'ResetProperties', 'SettingsService', 'NistExceptionGetter',
      function ($scope, SettingNameGetterAndSetter, ResetProperties, SettingsService, NistExceptionGetter) {

        $scope.settings = {
          smartyStreetsAuthID: "",
          smartyStreetsAPIKey: "",
          smartyStreetsActivation: "",
          nistURL: "",
          nistActivation: ""
        };

        $scope.nistStatus = {
          nistException: ""
        };

        $scope.messageEvaluation = {
        			authid:"",
        			api:"",
        			activation:""
        };

        $scope.refresh = function() {
        	activate();
        };

        activate();

        function activate() {
          console.log("SettingsController ACTIVATE");

          SettingsService.get({}, function(data) {
            var settingArray = data.settings;
            for (var i = 0; i < settingArray.length; i++) {
              var setting = settingArray[i];
              var name = setting.name;
              var value = setting.value;
              switch (name) {
                case 'smartyStreetsAuthID':
                  $scope.settings.smartyStreetsAuthID = value;
                  break;
                case 'smartyStreetsAPIKey':
                  $scope.settings.smartyStreetsAPIKey = value;
                  break;
                case 'smartyStreetsActivation':
                  $scope.settings.smartyStreetsActivation = value;
                  break;
                case 'nistURL':
                  $scope.settings.nistURL = value;
                  break;
                case 'nistActivation':
                  if (value == null || value === "DISABLED") {
                    $scope.settings.nistActivation = "DISABLED";
                  } else {
                    $scope.settings.nistActivation = "ENABLED";
                  }
                  break;
              }
            }
          });

          NistExceptionGetter.get(function (data) {
              $scope.nistStatus.nistException = data.exception;
          });

        }

        function saveSetting(name, value) {
          if (value <= "") {
            return;
          }
          SettingNameGetterAndSetter.save(
              {settingName: name},
              value,
              function (data) {
                if (data != null) {
                  //$scope.messageEvaluation = data;
                  $scope.error = false;
                  ResetProperties.get();
                } else {
                  $scope.errorMessage = "An unknown error occurred saving "
                      + name + " with value " + value;
                  $scope.error = true;
                }
              }, function (error) {
                $scope.error = true;
                $scope.errorMessage = error.statusText + " - " + error.data;
              });
        }
        // submit an address to be cleansed (or find already-mapped address)
        $scope.saveSettings = function () {
          console.log("Submitting");
          saveSetting("smartyStreetsAPIKey", $scope.settings.smartyStreetsAPIKey);
          saveSetting("smartyStreetsAuthID", $scope.settings.smartyStreetsAuthID);
          saveSetting("smartyStreetsActivation", $scope.settings.smartyStreetsActivation);
          saveSetting("nistURL", $scope.settings.nistURL);
          saveSetting("nistActivation", $scope.settings.nistActivation);
          $scope.alerts.push({msg: 'Saved!'});
          window.setTimeout($scope.removeFirst, 3000);
        };
        $scope.removeFirst = function() {
          $scope.closeAlert(0);
          $scope.$apply();
        };

        $scope.alerts = [
        ];

        $scope.closeAlert = function(index) {
          console.log("closing alert " + index);
          $scope.alerts.splice(index, 1);
        };
      }]);
