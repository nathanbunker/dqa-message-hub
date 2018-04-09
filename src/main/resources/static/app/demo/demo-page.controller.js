angular.module('messageHubDemoApp')

.controller('DemoPageController',
    ['$scope', 'Hl7JsonPoster', 'Hl7JsonExample', 'ReportMessage',
      function ($scope, $poster, $example, $messageReporter) {

        $scope.messageIn = {
//    				user:""
//    				,profileCode:""
//    				,message:""
        };

        $scope.messageEvaluation = {};

        activate();

        function activate() {
          console.log("DemoPageController ACTIVATE");
        }

        // submit an address to be cleansed (or find already-mapped address)
        $scope.ok = function () {
          console.log("Submitted");
          $poster.save($scope.messageIn, function (data) {
            if (data != null) {
              $scope.messageEvaluation = data;
              $scope.error = false;
            } else {
              $scope.errorMessage = "An unknown error occurred.";
              $scope.error = true;
            }
          }, function (error) {
            $scope.error = true;
            $scope.errorMessage = error.statusText + " - " + error.data;
          });
        };

        // submit an address to be cleansed (or find already-mapped address)
        $scope.getExample = function () {
          console.log("getting example");
          $example.get(function (data) {
            if (data != null) {
              $scope.messageIn = data;
              $scope.error = false;
            } else {
              $scope.errorMessage = "An unknown error occurred getting an example.";
              $scope.error = true;
            }
          }, function (error) {
            $scope.error = true;
            $scope.errorMessage = error.statusText + " - " + error.data;
          });
        };

      }]);
