'use strict';

/* 
 * Entry Point of Bulbs|Core web application
 */
var module__bulbs_core = angular
	.module('bulbs_core', [
        'ngRoute', 
        'ngAnimate',
        'bulbFilters', 
        'overridenJSCoreFunctions', 
        'commonHttpInterceptor', 
        'identityAuthServices', 
        'hardwareResources', 
        'colorServices', 
        'actuationServices', 
        'webSocketServices', 
        'entityServices',
        'bulbs_core_directives',
        'bulbs_core.globalState'
    ])
    .config([
            '$routeProvider', function ($routeProvider) {
                $routeProvider
                        .when('/login', {
                            templateUrl: 'bulbsCore/partials/identity/login.html', 
                            controller: IdentityCtrl} )
                        .when('/core/bulbs', {
                            templateUrl: 'bulbsCore/partials/core/bulb-list.html', 
                            controller: BulbsCtrl} )
                        .when('/core/bridges', {
                            templateUrl: 'bulbsCore/partials/core/bridges.html',
                            controller: BridgesCtrl} )
                        .when('/core/groups', {
                            templateUrl: 'bulbsCore/partials/core/groups.html',
                            controller: GroupCtrl} )
                        .when('/core/presets', {
                            templateUrl: 'bulbsCore/partials/core/presets.html',
                            controller: PresetCtrl} )
//                        .when('/speech/config/:bulbId', {
//                            templateUrl: 'bulbsCore/partials/core/bulb-detail.html',
//                            controller: BulbDetailCtrl} )
                        .when('/speech/config', {
                            templateUrl: 'bulbsCore/partials/core/bulb-list.html',
                            controller: BulbsCtrl} )

                        .otherwise({redirectTo: '/login'} );
//                        .otherwise({redirectTo: '/core/bulbs'} );
            }]
    )
	.config(function($locationProvider){
//        $locationProvider.html5Mode(true).hashPrefix('!');
    })
	.run(['$rootScope', '$location', 'IdentityService',  function($rootScope, $location, IdentityService) {
		IdentityService.checkSignedIn().then(
			function(user){
				console.log("Found user already Logged in: " + user);
                if($location.path() === "/login"){
				    $location.path('/core/bulbs');
                }
			}, function(error){
				$location.path('/login');
			});
	}]);
        
        
//~ SERVICES /////////////////////////////////////////////////////////
angular.module('overridenJSCoreFunctions', [], function () {
    Array.prototype.addAll = function() {
        for (var a = 0;  a < arguments.length;  a++) {
            var arr = arguments[a];
            for (var i = 0;  i < arr.length;  i++) {
                this.push(arr[i]);
            }
        }
    };
    Array.prototype.remove = function() {
		var idx;
        for (var a = 0;  a < arguments.length;  a++) {
            var arg = arguments[a];
			idx = this.indexOf(arg);
			this.splice(idx, 1);
        }
		return idx;
	};
    Array.prototype.removeAll = function() {
        this.splice(0, this.length );
        return this;
    };
});

/* 
 * Intercepts all http requests.
 * Sets global variable 'requestCount' in order to track the number of running requests (e.g. for showing load indicator).
 * Redirects to login page on 401/403 Response codes.
 * */
angular.module('commonHttpInterceptor', ['ngRoute'], function ($routeProvider, $httpProvider) {
	var reqInterceptor = ['$rootScope', '$q', function (scope, $q) {
		return {
			request: function (config) {
				//~ Request START --
				if(typeof scope.requestCount == 'undefined'){
					scope.requestCount = 0;
				}
                scope.requestCount += 1;
				return config || $q.when(config);
			}
		};
	}];
	var respInterceptor = ['$rootScope', '$q', '$timeout', function (scope, $q, $timeout) {
		function success(response) {
			//~ Request RETURN --
            $timeout(function(){
			    scope.requestCount -= 1;
            });
			return response;
		};
		function error(response) {
			//~ Request RETURN --
            $timeout(function(){
			    scope.requestCount -= 1;
            });
			var status = response.status;
			if ( ( status == 403 || status == 401 )
                    && window.location.hash !== "#/login/".substring(0, window.location.hash.length)) {
				window.location = "/#/login";
				return;
			}
			return $q.reject(response);

		};
		return function (promise) {
			return promise.then(success, error);
		};
	}];
	$httpProvider.interceptors.push(reqInterceptor);        
	$httpProvider.responseInterceptors.push(respInterceptor);        
});
        