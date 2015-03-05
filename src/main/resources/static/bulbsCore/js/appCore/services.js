'use strict';

/*
 * Bulbs|Core Resource- and Entity Services
 */
angular.module('identityAuthServices', ['ngResource'])
    .factory('IdentityResourceService', function($resource){
        return $resource('core/identity', {}, {
            signIn : {
				url : '/login',
				method : 'POST', params:{}, 
				isArray : false , 
				headers : {
                    'Content-Type': 'application/x-www-form-urlencoded',
//                    'Content-Type': 'application/json',
                    'Accept' : ''}
            },
            currentPrincipal : {
				url : '',
				method : 'GET', params:{}, 
				isArray : false 
//				headers : {'Content-Type': 'application/x-www-form-urlencoded'} 
            },
            signUp: {
                url : 'core/identity/signUp', 
                method:'POST', 
                params:{}, 
                isArray:false}
        });
    })
	.factory('IdentityService', function($resource, $q, $rootScope, IdentityResourceService){
		var principal = {
			nickname: '',
			apiKey: '',
			email: '',
			credentials : ''
		};
		var authenticated = false;
		
		var checkSignedIn = function(){
			var res = $q.defer();
			IdentityResourceService.currentPrincipal(
				{},{},
				function(user){
					authenticated = true;
					principal = user;
					res.resolve(user);
				}, function(error){
					res.reject(error);
			});
			return res.promise;
		};
		return {
			signIn : function(user){
				var res = $q.defer();
				IdentityResourceService.signIn(
					{},
					$.param(user),
					function(resp){
						principal = resp;
						authenticated = true;
						res.resolve(user);
					},
					function(error){
						authenticated = false;
						res.reject(error);
					});
				return res.promise;
			},
			signUp : function(user){
				var res = $q.defer();
				IdentityResourceService.signUp(
					{},
					user,
					function(resp){
						principal = resp;
						authenticated = true;
						res.resolve(user);
					},
					function(error){
						authenticated = false;
						res.reject(error);
					});
				return res.promise;
			},
			apiKey : function(){
				return principal.apiKey;
			},
			checkSignedIn : checkSignedIn,
			getPrincipal : function(){ return principal; },
			authenticated: function(){ return authenticated; }
		};
	});
angular.module('hardwareResources', ['ngResource', 'identityAuthServices'])
    .factory('BridgeResourceService', function($resource, IdentityService){
        return $resource('core/bridges', 
                {}, 
                {
                    loadByUser: {method:'GET', params:{}, isArray:true, 
                        headers : {'Auth' : window.escape(IdentityService.apiKey())} },
                    create: {method : 'POST',
                        params:{}, isArray:false, 
                        headers : {'Auth' : window.escape(IdentityService.apiKey())} },
                    modifyName: {url : 'core/bridges/:bridgeId/name', method:'PUT',
                        params:{}, isArray:false, 
                        headers : {'Auth' : window.escape(IdentityService.apiKey())} },
                    modifyLocalAddress: {url : 'core/bridges/:bridgeId/localAddress', method:'PUT',
                        params:{}, isArray:false,
                        headers : {'Auth' : window.escape(IdentityService.apiKey())} },
                    del: {url : 'core/bridges/:bridgeId', method:'DELETE', 
                        params:{}, isArray:false, 
                        headers : {'Auth' : window.escape(IdentityService.apiKey())} },
                    syncToHardwareState: {
                        url: 'core/bridges/doSyncToHardwareState', method:'POST', params:{}, 
                        isArray:false, 
                        headers : {'Auth' : window.escape(IdentityService.apiKey())} },
                    search: {url : 'core/bridges/:bridgeId/searchBulbs',
                        method:'POST', params:{}, isArray:false, 
                        headers : {'Auth' : window.escape(IdentityService.apiKey())} }
                }
        );
    })
    .factory('BulbResourceService', function($resource, IdentityService){
        return $resource('core/bulbs/', 
            {},
            {
                loadByUser: {method:'GET', params:{}, isArray:true,
                    headers : {'Auth' : window.escape(IdentityService.apiKey())} },
                modifyName: {url : 'core/bulbs/:bulbId/name',
                    method:'PUT', params:{}, isArray:false,
                    headers : {'Auth' : window.escape(IdentityService.apiKey())} }
            }
        );
    })
    .factory('GroupResourceService', function($resource, IdentityService){
        return $resource('core/groups', 
                {}, 
                {
                    groupById: {url: 'core/groups/:groupId', method:'GET', params:{}, isArray:false,
                            headers : {'Auth' : window.escape(IdentityService.apiKey())} },
                    groupsByUser: {method:'GET', params:{}, isArray:true, 
                            headers : {'Auth' : window.escape(IdentityService.apiKey())} },
                    createGroup: {method:'POST', params:{}, isArray:false, 
                            headers : {'Auth' : window.escape(IdentityService.apiKey())} }, 
                    deleteGroup: {url: 'core/groups/:groupId', method:'DELETE',
                            params:{}, isArray:false, 
                            headers : {'Auth' : window.escape(IdentityService.apiKey())} 
                    },
                    modifyName: {url: 'core/groups/:groupId', method:'PUT',
                            params:{}, isArray:false, 
                            headers : {'Auth' : window.escape(IdentityService.apiKey())} 
                    },
                    modifyAllGroupMembers: {url: 'core/groups/:groupId/bulbIds', method:'PUT',
                            params:{}, isArray:false, 
                            headers : {'Auth' : window.escape(IdentityService.apiKey())} 
                    }
                }
                
        );
    })
    .factory('PresetResourceService', function($resource, IdentityService){
        return $resource('core/presets', 
                {}, 
                {
                    presetById: {url: 'core/presets/:presetId', method:'GET', params:{}, isArray:false,
                            headers : {'Auth' : window.escape(IdentityService.apiKey())} },
                    presetsByUser: {method:'GET', params:{}, isArray:true, 
                            headers : {'Auth' : window.escape(IdentityService.apiKey())} },
                    createPreset: {method:'POST', params:{}, isArray:false, 
                            headers : {'Auth' : window.escape(IdentityService.apiKey())} }, 
                    deletePreset: {url: 'core/presets/:presetId', method:'DELETE',
                            params:{}, isArray:false, 
                            headers : {'Auth' : window.escape(IdentityService.apiKey())} 
                    },
                    modifyName: {url: 'core/presets/:presetId/name', method:'PUT',
                            params:{}, isArray:false, 
                            headers : {
								'Auth' : window.escape(IdentityService.apiKey())
							} 
                    },
                    modifyPresetStates: {url: 'core/presets/:presetId/states', method:'PUT',
                            params:{}, isArray : true, 
                            headers : {'Auth' : window.escape(IdentityService.apiKey())},
							transformResponse : function(data){
								var res = angular.fromJson(data);
								return [res];
							}
                    }
                }
        );
    })
;
angular.module('entityServices', ['ngResource', 'identityAuthServices'])
    .factory('BridgeService', function($resource, $q, $rootScope, BridgeResourceService, StompClientHolder, ActuatorServiceBulbs){
		var allBridges = [];
        var emptyBridge = {
            bridgeId : "",
            name : "",
            platform : "",
            localAddress : {
                host : "",
                port : -1
            }
        };
		var initialized = false;

		var bridgeById = function bridgeById(bridgeId){
			if(!initialized)throw new Error("Illegal state of BridgeService; Call 'loadAllBridges(..)' before invoking 'bridgeById(..)'");
            for (var i = 0; i< allBridges.length; i++){
                var bridge = allBridges[i];
				if(idsEqual(bridge.bridgeId, bridgeId)){
					return bridge;
				}
            }
            return null;
        };
		var loadAllBridges = function(forceUpdate){
            forceUpdate = typeof(forceUpdate) !== 'undefined' ? forceUpdate : false;
            var deferredRes = $q.defer();
            if(initialized && !forceUpdate){
                deferredRes.resolve(allBridges);
                return deferredRes.promise;
            }
            initialized = true;
            BridgeResourceService.loadByUser(
                    {},
                    {},
                    function(resp){
                        allBridges.length = 0;
                        allBridges.addAll(resp);
                        deferredRes.resolve(allBridges);
                    },
                    function(resp){
                        deferredRes.reject(resp);
                    }
            );
            return deferredRes.promise;
        };
		return {
			bridgesByUser : loadAllBridges,
			bridgeById : bridgeById,
			isInitialized : function(){
				return initialized;
			},
            newBridge : function(){
                return angular.copy(emptyBridge);
            }

		};
		
	})
    .factory('BulbService', function($resource, $q, $rootScope, BulbResourceService, StompClientHolder, ActuatorServiceBulbs){
        var allBulbs = [];
        var initialized = false;
		var emptyBulbActCmd = {
			type: 'BULB',
            appId : 'APP_TYPE__BULBS_CORE',
            priority : { priority : 0},
            bulbId : "",
            transitionDelay : 0, // @deprecated
            states : [ /* Contains list of BulbStates */ ]
        };

        //TODO: remove this fn and its depss
		var idsEqual = function (id1, id2){
			if(id1 == id2){
				return true;
			}
			return false;
		};
        var bulbById = function bulbById(bulbId){
			if(!initialized)throw new Error("Illegal state of BulbService; Call 'loadAllBulbs(..)' before invoking 'bulbById(..)'");
            for (var i = 0; i< allBulbs.length; i++){
                var bulb = allBulbs[i];
				if( bulb.bulbId === bulbId ){
					return bulb;
				}
            }
            return null;
        };
        var loadAllBulbs = function(forceUpdate){
            forceUpdate = typeof(forceUpdate) !== 'undefined' ? forceUpdate : false;
            var deferredRes = $q.defer();
            if(initialized && !forceUpdate){
                console.log("****");
                deferredRes.resolve(allBulbs);
                return deferredRes.promise;
            }
            initialized = true;
            BulbResourceService.loadByUser(
                    {},
                    {},
                    function(resp){
                        allBulbs.length = 0;
                        allBulbs.addAll(resp);
                        deferredRes.resolve(allBulbs);
                    },
                    function(resp){
                        deferredRes.reject(resp);
                    }
            );
            return deferredRes.promise;
        };
        
        var pushMsgHandler_BulbState = function(message){
            console.log("BulbService received Push Message.");
            var bulbStateUpdateEvent = JSON.parse(message.body);
            var bulbId =  bulbStateUpdateEvent.bulbId;
			var bulb = bulbById(bulbId);
//            var bState = bulb.state;
//			bulb.statePending = false;
			bulb.state = bulbStateUpdateEvent.state;
            $rootScope.$broadcast('Evt_BulbStateUpdated', bulbStateUpdateEvent);
        };
        var pushMsgHandler_BridgeSynced = function(message) {
            var bridgeSyncedEvent = JSON.parse(message.body);
            console.log("RECEIVED BridgeSyncedEvent: " +  bridgeSyncedEvent);
            loadAllBulbs(true);
        };
        StompClientHolder.subscribe("BulbService", "/topic/bulbupdates/", pushMsgHandler_BulbState);
        StompClientHolder.subscribe("BulbService", "/topic/bridgeSynced/", pushMsgHandler_BridgeSynced);
        
        return {
            bulbsByUser : loadAllBulbs,
            bulbById : bulbById,
			idsEqual : idsEqual,
			newActCmd : function(bulb){
				var res = angular.copy(emptyBulbActCmd);
				res.bulbId = bulb.bulbId;
				return res;
			},
			newCancelCmd : function(bulbs){
				var res = angular.copy(ActuatorServiceBulbs.newCancelCmd());
				angular.forEach(bulbs, function(bulb){
					res.entityIds.push(bulb.bulbId);
				});
				return res;
			},
			executeActCmd : function(cmd){
				ActuatorServiceBulbs.execute(cmd);
			},
			executeCancelCmd : function(cmd){
				ActuatorServiceBulbs.executeActuationCancelCmd(cmd);
			},
			isInitialized : function(){
				return initialized;
			}
        };
    })
	.factory('GroupService', function($resource, $q, $rootScope, GroupResourceService, StompClientHolder, ActuatorServiceGroups, BulbService){
        var allGroups = [];
        var initialized = false;
        var emptyGroup = {
            isNew : true,
            isUnsaved : true,
            isEditName : true,
            groupId :  newUUID(),
            name: "New Group",
            bulbIds : []
        };
		var emptyGroupActCmd = {
			type: 'GROUP',
            appId : 'APP_TYPE__BULBS_CORE',
            priority : { priority : 0},
            groupId : '',
            transitionDelay : 0,
            states : [ /* List of BulbStates */ ]
        };
		var idsEqual = function (id1, id2){
			if( id1 === id2 ){
				return true;
			}
			return false;
		};
        
        var groupById = function groupById(groupId){
			if(!initialized)throw new Error("Illegal state of GroupService; Call 'loadAllGroups(..)' before invoking 'groupById(..)'");
            for (var i = 0; i< allGroups.length; i++){
                var group = allGroups[i];
                if(group.groupId === groupId ){
                    return group;
                }
            }
            return null;
        };
        var loaddAllGroups = function(forceUpdate){
            forceUpdate = typeof(forceUpdate) !== 'undefined' ? forceUpdate : false;
            var deferredRes = $q.defer();
            if(initialized && !forceUpdate){
                deferredRes.resolve(allGroups);
                return deferredRes.promise;
            }
            initialized = true;
            GroupResourceService.groupsByUser(
                    {},
                    {},
                    function(resp){
                        allGroups.length = 0;
                        allGroups.addAll(resp);
                        deferredRes.resolve(allGroups);
                    },
                    function(resp){
                        deferredRes.reject(resp);
                    }
            );
            return deferredRes.promise;
        };
        var saveNewGroup = function(unsavedGroup){
            var deferredRes = $q.defer();
            GroupResourceService.createGroup(
                {},
                unsavedGroup,
                function(resp){
                    allGroups.push(resp);
                    deferredRes.resolve(resp);
                }, function(error) {
                    deferredRes.reject(error);
                }
            );
            return deferredRes.promise;
        };
        var deleteGroup = function(groupToDelete){
            var deferredRes = $q.defer();
            GroupResourceService.deleteGroup(
                { groupId: groupToDelete.groupId}, // Request url parames
                {}, // Request Body
                function(resp){
                    allGroups.remove(groupToDelete);
                    deferredRes.resolve(resp);
                }, function(error){
                    deferredRes.reject(error);
                }
            );
            return deferredRes.promise;
        };
        
        return {
            groupsByUser : loaddAllGroups,
            isInitialized : function(){
                return initialized;
            },
            groupById : groupById,
			idsEqual : idsEqual,
			newActCmd : function(group){
				var res = angular.copy(emptyGroupActCmd);
				res.groupId = group.groupId;
				group.bulbs = [];
				angular.forEach(group.bulbIds, function(bulbId){
					group.bulbs.push(BulbService.bulbById(bulbId));
				});
				return res;
			},
			executeActCmd : function(cmd){
				ActuatorServiceGroups.execute(cmd);
			},
            saveNewGroup  : saveNewGroup,
            deleteGroup : deleteGroup,
            newGroup : function(){
                return angular.copy(emptyGroup);
            }
        };
    })
    .factory('PresetService', function($resource, $q, PresetResourceService, BulbService, GroupService, EntityUtils, ActuatorServicePresets){
        var presets = [];
        var initialized = false;
        var emptyPreset = {
            presetId : '',
            name : '',
            states : [], // List of DtoAbstractActuatorCmds
			//~ Client-Only memebers
			isUnsaved : true
        };
        var emptyPresetCmd = {
            type : 'PRESET',
            presetId : '',
            appId : 'APP_TYPE__BULBS_CORE',
            priority : { priority : 1},
            transitionDelay : 0
        };
        
        var presetById = function(presetId){
            return angular.forEach(presets, function(preset){
                if(preset.presetId === presetId){
                    return preset;
                }
            });
        };
        
        return {
            presetsByUser : function(forceUpdate){
                forceUpdate = typeof(forceUpdate) !== 'undefined' ? forceUpdate : false;
                var deferredRes = $q.defer();
                if(initialized && !forceUpdate){
                    deferredRes.resolve(presets);
                    return deferredRes.promise;
                }
                initialized = true;
                PresetResourceService.presetsByUser(
                        {},
                        {},
                        function(resp){
                            console.log("Presets loaded.");
                            presets = resp;
                            deferredRes.resolve(presets);
                        },
                        function(resp){
                            deferredRes.reject(resp);
                        }
                );
                return deferredRes.promise;
            },
            presetById : presetById,
			applyPreset : function(presetId, asLoop){
				asLoop = (typeof(asLoop) !== 'undefined' ) ? asLoop : false;
				var cmd = angular.copy(emptyPresetCmd);
				cmd.presetId = presetId;
				cmd.loop = asLoop;
				ActuatorServicePresets.execute(cmd);
				
			},
            newPreset : function(){
                var res = angular.copy(emptyPreset);
                res.presetId = EntityUtils.newUUID();
                return res;
            },
            newPresetCommand : function(preset){
                var res = angular.copy(emptyPresetCmd);
                res.presetId = preset.presetId;
                return res;
            },
			newBulbActCmd : function(bulb){
				return BulbService.newActCmd(bulb);
			},
			newGroupActCmd : function(group){
				return GroupService.newActCmd(group);
			},
			createPreset : function(unsavedPreset){
				var promiseNewPreset = $q.defer();
				PresetResourceService.createPreset(
					{}, unsavedPreset,
                    function(resp){
						presets.remove(unsavedPreset);
						presets.push(resp);
						promiseNewPreset.resolve(resp);
						console.log("Preset successfully created: " + resp.name);
					},
					function(error){
						promiseNewPreset.reject(error);
					}
				);
				return promiseNewPreset.promise;
			},
			modifyName: function(preset){
				var promiseChangedPreset = $q.defer();
				PresetResourceService.modifyName(
						{presetId : preset.presetId},
						{value : preset.name},
						function(resp){
							presets.remove(preset);
							presets.push(resp);
							promiseChangedPreset.resolve(resp);
							console.log("Preset name successfully modified: " + resp.name);
						},
						function(error){
							console.log("Error modifying preset: " + error);
							promiseChangedPreset.reject(error);
						}
				);
				return promiseChangedPreset.promise;
			},
			modifyStates : function(preset){
				var promiseChangedPreset = $q.defer();
				console.log("Sending States: " + preset.states.length);
				PresetResourceService.modifyPresetStates(
					{presetId : preset.presetId},
					preset.states,
                    function(resp){
						var idx = presets.indexOf(preset);
						presets.remove(preset);
						presets.splice(idx, 0, resp[0]);
//						presets.push(resp[0]);
						promiseChangedPreset.resolve(resp[0]);
						console.log("Preset states successfully modified: " + resp[0].name);
					},
					function(error){
						promiseChangedPreset.reject(error);
					}
				);
				return promiseChangedPreset.promise;
			},
			
			removePreset : function(preset){
				if(!preset.isUnsaved){
					var promiseDelPreset = $q.defer();
					PresetResourceService.deletePreset(
							{presetId : preset.presetId},
							{},
							function(resp){
								console.log("Deleteing preset: " + presets.indexOf(preset) );
								presets.remove(preset);
								console.log("Preset sucessfully deleted: " + resp);
								promiseDelPreset.resolve(resp);
							},
							function(error){
								console.log("Todo: handle following error globally!");
								console.log("Error deleting preset: " + error);
								promiseDelPreset.reject(error);
							}
					);
					return promiseDelPreset.promise;
				}else{
					presets.remove(preset);
				}
			},
			replacePreset : function(presetCurrent, presetBackup){
				var idx = presets.indexOf(presetCurrent);
				presets.remove(presetCurrent);
				presets.splice(idx, 0, presetBackup);
			},
            presetsInEditMode : function(){
                var res = false;
                angular.forEach(presets, function (p) {
                    if(p.isUnsaved){
                        res = true;
                        return;
                    }
                });
                return res;
            }

			
        };
    })
    .factory('EntityUtils', function(){
        return {
            newUUID : function(){
                return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
                    var r = Math.random()*16|0, v = c == 'x' ? r : (r&0x3|0x8);
                    return v.toString(16);
                });
            }
        };
    })
;
   
angular.module('actuationServices', ['ngResource', 'identityAuthServices'])
     //~ Std HTTP Send
//    .factory('ActuatorServiceBulbs', function($resource, authenticationContext){
//        return $resource('core/actuation', 
//                {}, 
//                {execute: {method:'POST', params:{}, isArray:false, headers : {'Auth' : window.escape(authenticationContext.apiKey())} }}
//        );
//    });
    //~ Websockets Send
    .factory('ActuatorServiceBulbs', function($rootScope, StompClientHolder){
		var initialBulbActCmd = {
			type: 'BULB',
			appId : 'APP_TYPE__BULBS_CORE',
            priority : { priority : 1},
            bulbId : {},
            states : []
		};
		var initialActuationCancelCmd = {
			type : 'CANCEL',
			appId : 'APP_TYPE__BULBS_CORE',
			priority : { priority : 0},
			entityIds : []
		};
        return {
            execute : function(cmd){
                $rootScope._StompClientHolder.stompClient.send("/core/bulbs/actuation", {}, JSON.stringify(cmd));
            },
            executeActuationCancelCmd : function(cmd){
                $rootScope._StompClientHolder.stompClient.send("/core/bulbs/actuation/cancel", {}, JSON.stringify(cmd));
            },
			newCommand : function(){
				return angular.copy(initialBulbActCmd);
			},
			newCancelCmd : function(){
				return angular.copy(initialActuationCancelCmd);
			}
        };
    })
    .factory('ActuatorServiceGroups', function($rootScope, StompClientHolder){
		var initialGroupActCmd = {
			type: 'GROUP',
            appId : 'APP_TYPE__BULBS_CORE',
            priority : { priority : 1},
            groupId : '',
            transitionDelay : 0,
            states : []
        };
        return {
            execute : function(cmd){
                $rootScope._StompClientHolder.stompClient
                        .send("/core/groups/actuation", {}, JSON.stringify(cmd));
            },
			newCommand : function(){
				return angular.copy(initialGroupActCmd);
			}
        };
    })
    .factory('ActuatorServicePresets', function($rootScope, StompClientHolder){
        return {
            execute : function(cmd){
                $rootScope._StompClientHolder.stompClient
                        .send("/core/presets/actuation", {}, JSON.stringify(cmd));
            }
        };
    });
angular.module('webSocketServices', ['ngResource', 'identityAuthServices'])
    .factory('StompClientHolder', function($resource, IdentityService, $rootScope, $timeout){
        if($rootScope._StompClientHolder == null){
            var res = new function(){
                console.log("Creating new Core WebSocketService..");
                var self = this;
                self.stompClient = null;
                self.connected = false;
                self.queueSuffix;
                self.subscriptions = {};
                self.reconnectAttempt = 1;
                self.reconnTimer = null;
                self.subObjs = [];
                
                $rootScope.reconnectCounter = 5;;
                $rootScope.onReconnectTimerTrigger = function(){
                    $rootScope.reconnectCounter--;
                    if(!IdentityService.authenticated()){
                        $rootScope.reconnectMessage = "Reconnect WS in "+$rootScope.reconnectCounter+"s";
                        console.log("Reconnect in.. " + $rootScope.reconnectCounter);
                    }else{
                        $rootScope.reconnectMessage = '';
                    }
                    
                    if($rootScope.reconnectCounter == 0){
                        self.reconnectAttempt++;
                        if(IdentityService.authenticated()){
                            $rootScope.reconnectCounter = 2 * self.reconnectAttempt - 1 ;
                            console.log("Going to reconnect websocket..");
                            self.connect();
                            return;
                        }else{
                            self.reconnectAttempt = 1;
                            $rootScope.reconnectCounter = 1 ;
                        }
                    }
                    self.reconnTimer = $timeout($rootScope.onReconnectTimerTrigger, 1000);
                };
                self.connect = function(){
                    console.log("Connecting Websocket..");
                    var error_callback = function(error) {
                        // Trigger reconnect timer
                        console.log("Error on connect: ", error);
                        self.reconnTimer = $timeout($rootScope.onReconnectTimerTrigger, 1000);
                    };
                    self.socket = new SockJS('/core/websockets');
                    self.stompClient = Stomp.over(self.socket);
                    self.stompClient.connect( '', '', function(frame) {
                        //~ On Connection Success
                        if(self.reconnTimer != null ) $timeout.cancel(self.reconnTimer);
                        self.reconnectAttempt = 1;
                        $rootScope.reconnectCounter = 1;
                        self.connected = true;
                        $rootScope.reconnectMessage = '';
                        console.log('WebSocket frame connected: ' + frame);
                        //var username = frame.headers['user-name'];
                        //console.log('WebSocket username: ' + username);
                        //self.queueSuffix = frame.headers['queue-suffix'];
                        //console.log('Queue Suffix: ' + self.queueSuffix);
                        $rootScope.$broadcast('Evt_StompClientConnected', true);
                        console.log("Authenticated: " + IdentityService.authenticated());
                        for (var i in self.subObjs){
                            var sub = self.subObjs[i];
                            doSubscribe(sub.subscriberId, sub.topic, sub.callback);
                        }
                      }, error_callback);
                };
                
                self.subscribe = function(subscriberId, topic, callback){
                    console.log("WS Subscriber '"+subscriberId+"' subscribes to topic '"+topic+"' ");
                    self.subObjs.push({
                        subscriberId : subscriberId,
                        topic : topic,
                        callback : callback
                    });
                    if(self.connected){
                        doSubscribe(subscriberId, topic, callback);
                    }else{
//                        $rootScope.$on('Evt_StompClientConnected', function(e,arg){
//                            doSubscribe(subscriberId, topic, callback);
//                        });
                    }
                };
                function doSubscribe(subscriberId, topic, callback){
                    if( typeof(self.subscriptions[subscriberId]) !== 'undefined'){
                        if( typeof(self.subscriptions[subscriberId][topic]) !== 'undefined'){
                            self.stompClient.unsubscribe(
                                    self.subscriptions[subscriberId][topic] 
                            );
                        }
                    }
//                    topic = topic + self.queueSuffix;
//                    console.log("sending to topic: " + topic);
                    var subscr = self.stompClient.subscribe(topic, callback);
                    if(typeof(self.subscriptions[subscriberId]) === 'undefined'){
                        self.subscriptions[subscriberId] = {};
                    }
                    self.subscriptions[subscriberId][topic] = subscr;
                    return subscr;
                }
            };
            $rootScope._StompClientHolder = res;
            res.connect();
        }
        return $rootScope._StompClientHolder;
    });
;

angular.module('colorServices', [])
    .factory('ColorConverter', function(){
        var res = new ColorConverter();
        return res;
    })
;

angular.module('bulbs_core.globalState', [])
    .factory('GlobalOptionsService', function(){
        var globalOptions = {};
        return{
            allOptions : function(){
                return globalOptions;
            },
            putOption : function(key, value){
                globalOptions[key] = value;
            },
            getOption : function(key){
                globalOptions.getOption(key);
            }
        };
    })

;