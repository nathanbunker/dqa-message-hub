angular.module('messageHubDemoApp')

.controller('CodeListController',
    ['$scope', '$state', 'CodeFactory', 'CodeTypeFactory',
      function ($scope, $state, CodeFactory, CodeTypeFactory) {
        $scope.codeSet = [];
        $scope.codeOptions = [];
        $scope.codeTypeOption = {};

        $scope.formats = function() {
          console.log('formats');
          // $scope.codeSet = $scope.ndcUse.concat($scope.ndcSale);
        };

        function activate() {
          console.log("Activating Code List Controller!");
          $scope.codeOptions = CodeTypeFactory.query();

        }
        activate();

        $scope.loadCode = function() {
          getCode($scope.codeTypeOption.name);
        };

        function getCode(codeType) {
          CodeFactory.get({codesetType: codeType},
              function (data) {
                $scope.codeSet = data.code;
              });
        }


      }]);
