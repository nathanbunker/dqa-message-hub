angular.module('messageHubDemoApp')

    .controller('DetectionsDocumentationController', ['$scope', '$http', 'ngTreetableParams','$resource', '$q',
        function ($scope, $http, ngTreetableParams, $resource, $q) {

    	
    	var dataRemote = new $resource('documentation/json');
    	
    	

        $scope.expanded_params = new ngTreetableParams({
            getNodes: function(parent) {
                return parent ? parent.children : $q(function(resolve, reject){
                	dataRemote.get().$promise.then(function(result){
                		var data = [];
                		var documentation = result.detections;
                		for(var object in documentation){
                			if(documentation.hasOwnProperty(object)){
                				var objectData = {
                    					object,
                    					children : []
                    			};
                    			data.push(objectData);
                    			for(var field in documentation[object]){
                    				if(documentation[object].hasOwnProperty(field)){
                    					var fieldData = {
                        						field,
                        						children : []
                        				}
                        				objectData.children.push(fieldData);
                        				for(var severity in documentation[object][field]){
                        					if(documentation[object][field].hasOwnProperty(severity) && Array.isArray(documentation[object][field][severity])){

                        						var severityData = {
                                						severity,
                                						children : []
                                				}
                                				fieldData.children.push(severityData);
                                				for (var j = 0; j < documentation[object][field][severity].length; j++){
                                					var payload = documentation[object][field][severity][j];
                                					var detectionData = {
                                    						code : payload.code,
                                    						description : payload.text,
                                    						status : payload.active
                                    				}
                                					severityData.children.push(detectionData);
                                				}
                        					}
                            			}
                    				}
                    			}
                			}
                		}
                		resolve(data);
                	});
                });
            },
            getTemplate: function(node) {
                return 'tree_node';
            },
            options: {
            	initialState: 'expanded'
            }
        });
            
}]);