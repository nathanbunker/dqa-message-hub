angular.module('messageHubDemoApp')

.controller('MessageListModalController', ['$scope', '$state', '$uibModalInstance', 'FacilityList', "searchOptionsModal",
                                     function($scope, $state, $uibModalInstance, FacilityList, searchOptionsModal){
	$scope.searchOptionsForm = angular.copy(searchOptionsModal);
	
	$scope.statusCheckModel = {
		    AA: false,
		    AE: false,
		    AR: false
		  };
	
	$scope.statusFilter = [];
	
	  $scope.ok = function () {
	    $uibModalInstance.close($scope.searchOptionsForm);
	  };

	  $scope.cancel = function() {
		$uibModalInstance.dismiss('cancel');
	  };
	  
	  activate();
	  
	  function activate() {
		  console.log('building modal!');
		  console.log($scope.searchOptionsForm.filters.ackStatus);
		  var statusFilter = $scope.searchOptionsForm.filters.ackStatus;
		  
		  if (statusFilter) {
			  var splitted = [];
			  if (typeof statusFilter != Object) {
				  splitted = statusFilter.split(',');
				  console.log(splitted);  
			  } else {
				  
			  }
			  
			  angular.forEach(splitted, function (value) {
				  console.log("value: " + value);
				  if (value=='AA') {
					$scope.statusCheckModel.AA = true;
			      }
				  
				  if (value=='AE') {
						$scope.statusCheckModel.AE = true;
				      }
				  
				  if (value=='AR') {
						$scope.statusCheckModel.AR = true;
				      }
			    });
		  } else {
			  $scope.statusCheckModel.AA = true;
			  $scope.statusCheckModel.AE = true;
			  $scope.statusCheckModel.AR = true;
			  
		  }
		  
	  }
	  
	  
	  $scope.$watchCollection('statusCheckModel', function (newval, oldval) {
		    $scope.statusFilter = [];
		    console.log(newval);
		    if (!(newval.AA == newval.AE && newval.AE == newval.AR)) {
		    	angular.forEach($scope.statusCheckModel, function (value, key) {
				      if (value) {
				        $scope.statusFilter.push(key);
				      }
				    });
		    }
		    $scope.searchOptionsForm.filters.ackStatus = $scope.statusFilter.join(',');//turn it into string
		    console.log($scope.statusFilter);
	  });
}])
