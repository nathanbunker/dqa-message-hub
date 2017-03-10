angular.module('messageHubDemoApp')

    .controller('FileInputController', ['$scope', '$http', 'MessageResult', 'MessageDownload',
        function ($scope, $http, $msgres, $msgdownload) {
        
        $scope.response = {}
        
        $scope.stats = {}
            
    	$scope.uploadFile = function(){
	        var fd = new FormData();
	        fd.append('file', $scope.myFile);
	        $http.post('messages/form-file/', fd, {
	            transformRequest: angular.identity,
	            headers: {'Content-Type': undefined}
	        })
	        .success(function(data){
	        	$scope.response = data;
	        	console.log(data);
	        	
	        	$msgres.save(data, function (res) {
                    if (res != null) {
                        $scope.stats = res;
                    } else {
                        $scope.stats = "An unknown error occurred.";
                    }
                })
	        })
	        .error(function(){
	        });
		 }
		 
		 $scope.downloadFile = function () {
		 	$msgdownload.save($scope.stats.responseMessages);
    		console.log($scope.stats.responseMessages);
		}
            
	}]);

