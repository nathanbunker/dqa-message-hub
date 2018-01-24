angular.module('messageHubDemoApp')
    .controller('FilePageController', ['$scope', 'fileUpload',
        function ($scope, fileUpload) {
            //The container for watching the file uploads.
            $scope.fileUploads = {};

            //The value from the file input.
            $scope.myFile = {};

            initialize();
            function initialize() {
                fileUpload.getQueues().success(function(data){
                    if (data.uploads) {
                        for (var x = data.uploads.length - 1; x >= 0; x--) {
                            var upload = data.uploads[x];
                            $scope.fileUploads[upload.fileId] = upload;
                            watchFileProcess(upload.fileId);
                        }
                    }
                });
            }

            $scope.removeFile = function(fileId) {
                fileUpload.removeFile(fileId);
                $scope.fileUploads[fileId] = null;
            };

            //The function that sends the file and watches the process.
            $scope.uploadFile = function () {
                //Get the file from the file input control on the front end.
                var file = $scope.myFile;

                //Submit the file to be processed, initiate the process, and watch it.
                fileUpload.uploadFileToUrl(file)
                //once the file has been submitted to be in the queue, success will be triggered.
                    .success(function(data){
                        //Save the information about the file upload to the scope map.
                        $scope.fileUploads[data.fileId] = data;
                        //Then we must initiate the file process to get it started.
                        fileUpload.initiateFileProcess(data.fileId);
                        //Once its started, we need to initiate the process watcher.
                        watchFileProcess(data.fileId);
                        //And then clear the value from the file input to prepare to submit another file.
                        angular.element("#fileInput").val(null);
                    })
                    //If there's an error uploading, notify the user.
                    .error(function(){
                        console.log("ERROR SUBMITTING FILE.");
                        alert("ERROR Submitting file");
                });
            };

            function watchFileProcess(fileId) {
                setTimeout(function () {
                    fileUpload.reportFileProcess(fileId)
                        //Once the data is returned, save it, and take a look.
                        .success(function (data) {
                            //replace the data in the map with the new information about the process.
                            $scope.fileUploads[fileId] = data;
                            //And if it's not done, trigger another update.
                            if (data.status === 'started') {
                                //Call again to get an update.  this is recursion. w00t!
                                watchFileProcess(fileId);
                            }
                        });
                 //repeat the lookup at the time interval below.
                }, 1000);
            }

            $scope.downloadAcks = function(fileId) {
                var fileInfo = $scope.fileUploads[fileId];
                var fileName = fileInfo.fileName;
                var text = fileInfo.ackMessages.join("\r");
                download(fileName, text);
            };

            function download(filename, text) {
                console.log("Creating file named " + filename);
                var element = document.createElement('a');
                element.setAttribute('href', 'data:text/plain;charset=utf-8,' + encodeURIComponent(text));
                element.setAttribute('download', filename + "-acks.txt");
                element.style.display = 'none';
                document.body.appendChild(element);
                element.click();
                document.body.removeChild(element);
            }

        }]);