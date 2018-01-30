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

            $scope.cancelRemove = function(fileId) {
                console.log("UNDO Removing file: " + fileId);
                var timeout = $scope.fileUploads[fileId].deleteTimeout;
                clearTimeout(timeout);
                $scope.fileUploads[fileId].deleteRequested = false;
                $scope.fileUploads[fileId].countdown = 0;

                fileUpload.unpauseFileProcess(fileId);
                $scope.fileUploads[fileId].status = 'started';
                watchFileProcess(fileId);
                //
                //if ($scope.fileUploads[fileId].percentage < 100) {
                //        fileUpload.reportFileProcess(fileId)
                //            .success(function (data) {
                //                console.log("file is finished? : " + !(data.percentage < 100));
                //                console.log("file percent? : " + data.percentage);
                //                if (data.percentage < 100) {
                //                    //Once the data is returned, save it, and take a look.
                //
                //                } else {
                //                    $scope.fileUploads[fileId] = data;
                //                    $scope.fileUploads[fileId].status = "finished";
                //                }
                //            });
                //} else {
                //    $scope.fileUploads[fileId].status = "finished";
                //}

            };

            $scope.removeFile = function(fileId) {

                if ($scope.fileUploads[fileId] && $scope.fileUploads[fileId].deleteRequested) {
                    console.log("Remove already requested for: " + fileId);
                    //it's already marked for being deleted.
                    return;
                }

                $scope.fileUploads[fileId].deleteRequested = true;
                $scope.fileUploads[fileId].status = 'deleting';
                $scope.fileUploads[fileId].countdown = 100;
                fileUpload.pauseFileProcess(fileId);
                countdown(fileId);

                $scope.fileUploads[fileId].deleteTimeout = setTimeout(function () {
                    if ($scope.fileUploads[fileId].deleteRequested === true) {
                        console.log("Removing file: " + fileId);
                        delete $scope.fileUploads[fileId];
                        fileUpload.removeFile(fileId);
                    }
                }, 2200);
            };

            function countdown(fileId) {
                setTimeout(function() {
                    if ($scope.fileUploads[fileId]) {
                        $scope.fileUploads[fileId].countdown = Math.round($scope.fileUploads[fileId].countdown - 3.8);
                        if ($scope.fileUploads[fileId].countdown > 0) {
                            $scope.$apply();
                            countdown(fileId);
                        }
                    }
                }, 100);
            }

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
                    var upload = $scope.fileUploads[fileId];
                    if (!upload || upload.status !== 'deleting') {
                        //console.log("watching...");
                        //console.log(upload);
                        fileUpload.reportFileProcess(fileId)
                        //Once the data is returned, save it, and take a look.
                            .success(function (data) {
                                if (data) {
                                    //replace the data in the map with the new information about the process.
                                    $scope.fileUploads[fileId] = data;
                                    //And if it's not done, trigger another update.
                                    if (data.status === 'started') {
                                        //Call again to get an update.  this is recursion. w00t!
                                        watchFileProcess(fileId);
                                    }
                                }
                            });
                    }
                    //repeat the lookup at the time interval below.
                }, 500);
        }

            $scope.downloadAcks = function(fileId) {
                fileUpload.getAcks(fileId)
                    .success(function (data) {
                        var fileName = $scope.fileUploads[fileId].fileName;
                        var text = data.join("\r");
                        download(fileName, text);
                    });
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