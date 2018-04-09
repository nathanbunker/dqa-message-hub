angular.module('messageHubDemoApp')

	.controller('MessageDetailController', ['$sce', '$scope', 'messageDetail',
		function ($sce, $scope, messageDetail) {

			$scope.filterText = {$: ''};

			$scope.messageDetail = messageDetail;
			$scope.spannedMessageRequest = "";
			$scope.messageRequestParts = [];
			$scope.hIdx = -1;

			$scope.current = {};

			$scope.goBack = function () {
				window.history.back();
			};

			activate();

			function activate() {
				console.log("Activating MESSAGE DETAIL Controller!");
				//Prep the list by annotating it with the array index.
				$scope.messageDetail.messageData.$promise.then(function (data) {
					$scope.messageRequestParts = makeArrayWithSeparatorsIncluded(data.messageReceived, data.vxuParts);
					$scope.segmentList = getSegmentList(data.vxuParts);
				});
			}

			$scope.setHoverIndex = function (part) {
				$scope.hIdx = part.valueIndex;
			};

			/**
			 * Handles what happens when a part of the message is clicked (highlighting the part, etc.).
			 *
			 * @param index index of the message part that was clicked
			 */
			$scope.messageClicked = function (index) {
				$scope.filterText.$ = '';

				// if the clicked part is the same as the currently-selected part, clear the selection
				if ($scope.hIdx === index) {
					$scope.hIdx = -1;
					$scope.current.segment = undefined;
				} else {
					$scope.hIdx = index;
					$scope.current.segment = $scope.messageDetail.messageData.vxuParts[index].segmentIndex;
				}
			};

			/**
			 * Gets a list of segment names and IDs. Used to generate navigation buttons.
			 *
			 * @param valueList List of parts of the message
			 * @returns {Array} List of all segments (lines) in the message
			 */
			var getSegmentList = function (valueList) {
				var segmentList = [];

				for (var i = 0, len = valueList.length; i < len; i++) {
					var valueItem = valueList[i];
					var value = valueItem.value;

					if (valueItem.location.substring(4, 5) === '0') {
						segmentList.push({
							name: value,
							index: valueItem.segmentIndex
						})
					}
				}

				return segmentList;
			};

			/**
			 * Get all of the parts of the message for display.
			 *
			 * @param inputString message as a single string
			 * @param valueList   JSON list of values
			 * @returns {Array}   List of message parts
			 */
			var makeArrayWithSeparatorsIncluded = function (inputString, valueList) {
				var popOffString = inputString;
				var list = [];
				var separatorElement;

				//append spans around each value, and include some metadata...
				for (var i = 0, len = valueList.length; i < len; i++) {
					var valueItem = valueList[i];

					//find the next spot that value is in the string.
					var value = valueItem.value;
					var idxStart = popOffString.indexOf(value);
					var separatorValue = popOffString.substring(0, idxStart);

					if (separatorValue) {
						separatorElement = {
							value: separatorValue,
							valueIndex: -10
						};
						list.push(separatorElement);
					}

					list.push(valueItem);

					// then the value
					popOffString = popOffString.substring(idxStart + value.length);
				}

				separatorElement = {
					value: popOffString,
					valueIndex: -10,
					location: "",
					locationDescription: ""
				};

				list.push(separatorElement);
				return list;
			};

			/**
			 * Toggles the currently-selected segment tab/button. If the clicked button is the same as the
			 * currently-selected button, the currently-selected segment will be reset so none are selected.
			 *
			 * @param index Index of the clicked button.
			 */
			$scope.toggleTab = function (index) {
				if ($scope.current.segment === index) {
					$scope.current.segment = undefined;
				} else {
					$scope.current.segment = index;
				}

				$scope.filterText.$ = '';
				clearSelectedPart();
			};

			/**
			 * Clears out selections when the search text is changed. Clears the currently-selected segment button, as
			 * well as the highlighted message part/result.
			 */
			$scope.clearSearch = function () {
				$scope.current.segment = undefined;
				clearSelectedPart();
			};

			/**
			 * Clears the currently-selected message part/result.
			 */
			function clearSelectedPart() {
				$scope.hIdx = -1;
			}
		}]);