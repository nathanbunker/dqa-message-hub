angular.module('messageHubDemoApp')

    .controller('FilePageController', ['$scope', 'fileUpload', 
        function ($scope, fileUpload) {
            
    	 $scope.uploadFile = function(){
    	        var file = $scope.myFile;
    	        console.log('file is ' );
    	        console.dir(file);
				var uploadUrl = "messages/form-file";
              fileUpload.uploadFileToUrl(file, uploadUrl);
    	    };
            
        }]);