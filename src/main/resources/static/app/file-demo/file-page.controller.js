angular.module('messageHubDemoApp')
    .controller('FilePageController', ['$scope', 'fileUpload',
        function ($scope, fileUpload) {
            //The container for watching the file uploads.
            $scope.fileUploads = {};
            //The value from the file input.
            $scope.myFile      = {};

            initialize();

            //When the page first starts up, check for existing uploads
            //and add them to the list.
            function initialize() {
                fileUpload.getQueues()
                    .success(function (data) {
                    //if there are uploads, cycle through them and add them
                    if (data.uploads) {
                        for (var x = data.uploads.length - 1; x >= 0; x--) {
                            $scope.fileUploads[data.uploads[x].fileId] = data.uploads[x];
                            watchFileProcess(data.uploads[x].fileId);
                        }
                    }
                });
            }

            $scope.cancelRemove = function (fileId) {
                console.log("UNDO Removing file: " + fileId);
                clearTimeout($scope.fileUploads[fileId].deleteTimeout);
                $scope.fileUploads[fileId].deleteRequested = false;
                $scope.fileUploads[fileId].countdown       = 0;

                fileUpload.unpauseFileProcess(fileId);
                $scope.fileUploads[fileId].status = 'started';
                watchFileProcess(fileId);
            };

            $scope.removeFile = function (fileId) {
                if ($scope.fileUploads[fileId] && $scope.fileUploads[fileId].deleteRequested) {
                    console.log("Remove already requested for: " + fileId);
                    //it's already marked for being deleted.
                    return;
                }

                $scope.fileUploads[fileId].deleteRequested = true;
                $scope.fileUploads[fileId].status          = 'deleting';
                $scope.fileUploads[fileId].countdown       = 100;
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
                setTimeout(function () {
                    if ($scope.fileUploads[fileId]) {
                        $scope.fileUploads[fileId].countdown = Math.round($scope.fileUploads[fileId].countdown - 3.8);
                        $scope.$apply();
                        if ($scope.fileUploads[fileId].countdown > 0) {
                            countdown(fileId);
                        }
                    }
                }, 100);
            }

            //The function that sends the file and watches the process.
            $scope.uploadFile = function () {
                //Submit the file to be processed, initiate the process, and watch it.
                fileUpload.uploadFileToUrl($scope.myFile)
                //once the file has been submitted to be in the queue, success will be triggered.
                    .success(function (data) {
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
                    .error(function () {
                        console.log("ERROR SUBMITTING FILE.");
                        alert("ERROR Submitting file");
                    });
            };

            function watchFileProcess(fileId) {
                    if (!$scope.fileUploads[fileId] || $scope.fileUploads[fileId].status !== 'deleting') {
                        fileUpload.reportFileProcess(fileId)
                            //Once the data is returned, save it, and take a look. Repeat if necessary.
                            .success(function (data) {
                                if (data) {
                                    //replace the data in the map with the new information about the process.
                                    $scope.fileUploads[fileId] = data;
                                    //repeat the lookup at the time interval below.
                                    setTimeout(function () {
                                        //if the process is still going.
                                        if (data.status === 'started') {
                                            //Call again to get an update.  this is recursion. w00t!
                                            watchFileProcess(fileId);
                                        }
                                    }, 1000);
                                }
                            });
                    }
            }

            $scope.downloadAcks = function (fileId) {
                fileUpload.getAcks(fileId)
                    .success(function (data) {
                        var fileName = $scope.fileUploads[fileId].fileName;
                        var text     = data.join("\r");
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