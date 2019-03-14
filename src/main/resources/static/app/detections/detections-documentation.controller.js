angular.module('messageHubDemoApp')

    .controller('DetectionsDocumentationController', ['$scope', '$http', 'ngTreetableParams','$resource', '$q',
        function ($scope, $http, ngTreetableParams, $resource, $q) {
    	$scope.exportType = 'Document';
    	
    	var dataRemote = new $resource('documentation/table');
    	dataRemote.query().$promise.then(function(result){
    		console.log(result);
    		$scope.rows = result;
    	});
    	
    	$scope.rows = [];
    	

            
}]);