angular.module('messageHubDemoApp')

    .controller('FilePageController', ['$scope', 'fileUpload', 
        function ($scope, fileUpload) {
            
    	 $scope.uploadFile = function(){
    	        var file = $scope.myFile;
    	        console.log('file is ' );
    	        console.dir(file);
    	        var uploadUrl = "http://localhost:8082/messages/form-file/";
    	        fileUpload.uploadFileToUrl(file, uploadUrl);
    	    };
            
        }]);