angular.module('messageHubDemoApp')

    .factory('Hl7JsonPoster', function ($resource) {
        return $resource('messages/json');//posts a json object
    })

    .factory('Hl7JsonExample', function ($resource) {
    	return $resource('messages/json/example');//gets a json object with an example message
    })
    
    .factory('MessageResult', function ($resource) {
    	return $resource('messages/msg-result');//gets a string with ack info
    })
    
    .factory('MessageDownload', function ($resource) {
    	return $resource('messages/msg-download');//gets a string with ack info
    })

	.directive('fileModel', ['$parse', function ($parse) {
	    return {
	        restrict: 'A',
	        link: function(scope, element, attrs) {
	            var model = $parse(attrs.fileModel);
	            var modelSetter = model.assign;
	            
	            element.bind('change', function(){
	                scope.$apply(function(){
	                    modelSetter(scope, element[0].files[0]);
	                });
	            });
	        }
	    };
	}])
	
	.filter('newline', function ($sce) {
	    return function (text) {
	        return text ? $sce.trustAsHtml(text.replace(/\r/g, '<br/>')) : '';
	    };
	})

	

;