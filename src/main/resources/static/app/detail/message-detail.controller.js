angular.module('messageHubDemoApp')

    .controller('MessageDetailController', ['$sce','$scope', 'messageDetail',
        function ($sce, $scope, messageDetail) {

            $scope.filterText = {$: ''};

            $scope.messageDetail = messageDetail;
            $scope.spannedMessageRequest = "";
            $scope.messageRequestParts = [];
            $scope.hIdx = -1;
            
            $scope.current = {
            		page :1
            }
            
            $scope.goBack = function () {
                window.history.back();
            };
            
            activate();
            
            function activate() {
            	console.log("Activating MESSAGE DETAIL Controller!");
            	//Prep the list by annotating it with the array index.
            	$scope.messageDetail.messageData.$promise.then(function(data) {
            		$scope.messageRequestParts = makeArrayWithSeparatorsIncluded(data.messageReceived, data.vxuParts);
            	});
            }
            
            $scope.pageChanged = function() {
//          	  var startPos = ($scope.current.page - 1) * 3;
          	  //$scope.displayItems = $scope.totalItems.slice(startPos, startPos + 3);
//          	  console.log($scope.current.page);
          	};
            
            
            $scope.setHoverIndex = function(part) {
            	$scope.hIdx = part.valueIndex;
            };
            
            $scope.messageClicked = function(locationText, index) {
            	if (locationText > "" && index >= 0) {
            		$scope.hIdx = index;
            		$scope.filterText.$ = '';
            		for (var x = 0 ; x < $scope.messageDetail.messageData.vxuParts.length ; x++) {
            			if ($scope.messageDetail.messageData.vxuParts[x].valueIndex == index) {
            				var valuePosition = (x / 10);
            				var calucatedPage = Math.floor(valuePosition) + 1;
            				$scope.current.page = calucatedPage;
            			}
            		}
            	}
            }
            
//            $scope.$watch('current.page', function () {
//            	console.log("current page: " + $scope.current.page);
//            }, true);
            
           
           makeArrayWithSeparatorsIncluded = function(inputString, valueList)  {
//        	   console.log("popping and locking!");
        	   
				var popOffString = inputString;
				//Put MSH into the new String.
				var list = [];
				var firstElement = {
						value: popOffString.substring(0,3),
						valueIndex : -100,
					}
				
				list.push(firstElement);
				
				popOffString = popOffString.substring(3);
				//append spans around each value, and include some metadata...
				for (var i = 0, len = valueList.length; i < len; i++) {
//					console.log("adding data for: ");
					var valueItem = valueList[i];
//					console.log(valueItem);
					//find the next spot that value is in the string.
					var value = valueItem.value;
					var idxStart = popOffString.indexOf(value);
					var separatorValue = popOffString.substring(0,idxStart);
					if (separatorValue) {
						var separatorElement = {
								value: separatorValue,
									valueIndex : -10
						};
						list.push(separatorElement);
					}
					list.push(valueItem);
					/* then the value */
					popOffString = popOffString.substring(idxStart + value.length);
				}
				var separatorElement = {
						value: popOffString,
						valueIndex : -10, 
						location: "", 
						locationDescription: ""
				};
				list.push(separatorElement);
			return list;
          };
}]);