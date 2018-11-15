angular.module('messageHubDemoApp')

.controller('CodeListController',
    ['$scope', '$state', 'CodeFactory', 'CodeTypeFactory',
      function ($scope, $state, CodeFactory, CodeTypeFactory) {
        $scope.codeSet = [];
        $scope.codeOptions = [];
        $scope.codeTypeOption = null;
        $scope.filterText = {$: ""};
        function activate() {
          console.log("Activating Code List Controller!");
          $scope.codeOptions = CodeTypeFactory.query();

        }
        activate();

        $scope.loadCode = function() {
          $scope.codeSet = [{value: "loading..."}];
          getCode($scope.codeTypeOption.name);
          $scope.filterText.$ = "";
        };

        function getCode(codeType) {
          CodeFactory.get({codesetType: codeType},
              function (data) {
                $scope.codeSet = data.code;
              });
        }


      }]);
