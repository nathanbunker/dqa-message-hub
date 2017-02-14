angular.module('messageHubDemoApp')

    .factory('Hl7JsonPoster', function ($resource) {
        return $resource('messages/json');//posts a json object
    })

    .factory('Hl7JsonExample', function ($resource) {
    	return $resource('messages/json/example');//gets a json object with an example message
    })

   ;