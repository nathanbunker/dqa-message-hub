angular.module('messageHubDemoApp')

    .controller('ReportDemoController', ['$scope', 'Hl7JsonPoster', 'ReportMessage', 'Hl7JsonExample',
        function ($scope, $poster, $reportMessage, $exampleMessage) {
            
    		$scope.report = {};
    		$scope.messageIn = {};

            activate();

            function activate() {
                console.log("ReportDemoController ACTIVATE");
            }
            
            $scope.getReport = function () {
                console.log($scope.messageIn);
                $reportMessage.save($scope.messageIn, function (data) {
                    if (data != null) {
                        $scope.report = data;
                        $scope.error = false;
                        $scope.loaded = true;
                    } else {
                        $scope.errorMessage = "An unknown error occurred getting an example.";
                        $scope.error = true;
                    }
                }, function (error) {
                    $scope.error = true;
                    $scope.errorMessage = error.statusText + " - " + error.data;
                });
            };
            
            $scope.getExample = function () {
                console.log("getting example");
                $exampleMessage.get(function (data) {
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