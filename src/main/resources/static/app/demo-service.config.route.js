angular.module('messageHubDemoApp')

.config(function ($stateProvider, $urlRouterProvider) {

  $urlRouterProvider.otherwise("messages");

  $stateProvider
  .state('demo', {
    url: "/demo-page",
    templateUrl: 'app/demo/demo-page.html',
    controller: "DemoPageController",
    resolve: {
      urlParams: ['$stateParams', function ($stateParams) {
        return {};
      }]
    }
  })

  .state('detections', {
    url: "/detections",
    templateUrl: 'app/detections/detections-documentation.html',
    controller: "DetectionsDocumentationController",
    resolve: {
      urlParams: ['$stateParams', function ($stateParams) {
        return {};
      }]
    }
  })

  .state('landing-page', {
    url: "/messages",
    templateUrl: 'app/home/landing-page.html',
    controller: "LandingPageController",
    resolve: {
      urlParams: ['$stateParams', function ($stateParams) {
        return {};
      }]
    }

  })
  .state('message-list', {
    url: '/messages/:providerKey/date/:date/?page&filters',
    templateUrl: 'app/list/message-list.html',
    controller: "MessageListController",
    params: {
      page: {
        value: '1',
        squash: true,
        dynamic: true
      }
      , filters: {
        value: '',
        squash: true,
        dynamic: true
      }
    },
    resolve: {
      pageData: ['$stateParams',
        function ($stateParams) {
          console.log("%%%%% MESSAGE-LIST ROUTE %%%%");
          var urlObj = {
            'providerKey': $stateParams.providerKey,
            'date': $stateParams.date,
            'page': $stateParams.page,
            'filters': $stateParams.filters
          };

          return urlObj;
        }]
    }
  })

  .state('message-detail', {
    url: "/messages/{id}",
    templateUrl: 'app/detail/message-detail.html',
    controller: 'MessageDetailController',
    resolve: {
      messageDetail: ['$stateParams', 'MessageDetailFactory',
        function ($stateParams, MessageDetailFactory) {
          return {
            messageData: MessageDetailFactory.get({id: $stateParams.id})
          };
        }]
    }
  })

  .state('file', {
    url: "/file-demo",
    templateUrl: 'app/file-demo/file-page.html',
    controller: "FilePageController",
    resolve: {
      urlParams: ['$stateParams', function ($stateParams) {
        return {};
      }]
    }
  })

  .state('reportDemo', {
    url: "/report-demo",
    templateUrl: 'app/report-demo/report-demo.html',
    controller: "ReportDemoController",
    resolve: {
      urlParams: ['$stateParams', function ($stateParams) {
        return {};
      }]
    }
  })

  .state('settings', {
    url: "/settings",
    templateUrl: 'app/settings/settings.html',
    controller: "SettingsController",
    resolve: {
      urlParams: ['$stateParams', function ($stateParams) {
        return {};
      }]
    }
  })
  ;
  $stateProvider
  .state('file-input', {
    url: "/file-input",
    templateUrl: 'app/file-input/file-input.html',
    controller: "FileInputController",
    resolve: {
      urlParams: ['$stateParams', function ($stateParams) {
        return {};
      }]
    }
  })
  ;
});
