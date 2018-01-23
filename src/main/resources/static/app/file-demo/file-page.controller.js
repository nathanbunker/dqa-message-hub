angular.module('messageHubDemoApp')
    .controller('FilePageController', ['$scope', 'fileUpload',
        function ($scope, fileUpload) {

            $scope.fileUploads = {};
            $scope.fileIds = [];
            $scope.myFile = {};
            $scope.cycle = false;

            $scope.uploadFile = function () {

                var file = $scope.myFile;
                console.log('file is ');
                console.dir(file);

                fileUpload.uploadFileToUrl(file)
                    .success(function(data){
                        console.log("file upload info: ");
                        console.log(data);
                        console.log(data.fileId);
                        fileUpload.initiateFileProcess(data.fileId);
                        $scope.fileUploads[data.fileId] = data;
                        console.log("Initiated process");
                        //$scope.cycle = true;
                        //$scope.cycle = false;
                        watchFileProcess(data.fileId);
                        angular.element("input[type='file']").val(null);
                    })
                    .error(function(){
                        console.log("ERROR SUBMITTING FILE.");
                });
            };

            function watchFileProcess(fileId) {
                console.log("about to loop for file id: " + fileId);
                setTimeout(function(){
                    console.log("Looking up current status for " + fileId);
                            fileUpload.reportFileProcess(fileId)
                                .success(function (data) {
                                    $scope.fileUploads[fileId] = data;
                                    console.log("fileId:"+fileId+"::fileUploads::"+ $scope.fileUploads[fileId]);
                                    console.log("Current status: " + $scope.fileUpload);
                                    if (data.status === 'started') {
                                        watchFileProcess(fileId);
                                    }
                                });
                    }, 500);

            }

        }]);