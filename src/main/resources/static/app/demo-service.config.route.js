angular.module('messageHubDemoApp')

    .config(function ($stateProvider, $urlRouterProvider) {

        $urlRouterProvider.otherwise("demo-page");

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
        ;
    });
