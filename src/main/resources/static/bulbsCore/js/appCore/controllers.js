'use strict';

/* ******************************
 * Controllers - Bulbs|Core - 
 * ******************************
 */

function NavbarCtrl($scope, $location, GlobalOptionsService, IdentityService){
    $scope.pageActive = function(route) {
        return route === $location.path();
    };
    $scope.navCategoryActive = function(route) {
//        return route === $location.path().substring(0, route.length);
        return route.replace("/", "") === $scope.categoryActive.active;
    };
    $scope.categoryActive = {
        active : ""
    };

    $scope.globalOptions = GlobalOptionsService.allOptions();
    GlobalOptionsService.putOption("searchQuery", "");
    $scope.currentUser = IdentityService.getPrincipal();
    $scope.navbarSettings = {
        searchQuery : $scope.globalOptions.searchQuery
    };
    $scope.init = function(){
        var splitPath = $location.path().split("/");
        $scope.categoryActive.active = splitPath[1];
    }
    $scope.init();
}

//~ BULBSs /////////////////////////////////////////////////////////////////////
function BulbsCtrl($scope, $rootScope, $http, $routeParams, $timeout,
        BridgeResourceService, BulbResourceService, BulbService, ActuatorServiceBulbs, GlobalOptionsService, ColorConverter,
        StompClientHolder) {

//    $scope.authenticationContext = authenticationContext;
    $scope.bulbs = null;
    $scope.bulbs_cache = {};
    $scope.bulbs_mod = [];
    $scope.bulbs_opts = {
        continuousTrigger : true
    };
    $scope.globalOptions = GlobalOptionsService.allOptions();
    
	$scope.sortPredicateBulb = 'name';
    $scope.reloadBulbs = function(){
//        $scope.bulbs = null;
//        loadAllBulbs();
        syncAllBridgesToHwState();
    };
    var loadAllBulbs = function(){
        $scope.bulbs = null;
        BulbService.bulbsByUser().then(
            function(bulbs){
                $scope.bulbs = bulbs;
                calculateBulbIndex();
//                initWebsocketHandler();
            },
            function(error){
                // ~ Failure
                $scope.serverError = "Error(" + error.status +")" + error.data;
            }
        );
    };
    var syncAllBridgesToHwState = function(){
        BridgeResourceService.syncToHardwareState(
            {}, // Request url parames
            {}, // Request Body
            function(resp){
//                $scope.bulbs = resp;
//                calculateBulbIndex();
//                initWebsocketHandler();
//                console.log(resp);
            },
            function(httpResponse){
                // ~ Failure
                $scope.serverError = "Error(" + httpResponse.status +")" + httpResponse.data;
            }
        );
    };
    function calculateBulbIndex(){
        angular.forEach($scope.bulbs, function (bulb) {
			bulb.statePending = false;
            if(typeof($scope.bulbs_cache[bulb.bulbId]) === 'undefined'){
                $scope.bulbs_cache[bulb.bulbId] = {};
            }
            $scope.bulbs_cache[bulb.bulbId] = bulb;
        });
    }
    function findBulbIdx(bulb2Find) {
        var idx = 0;
        var res = -1;
        angular.forEach($scope.bulbs, function (bulb) {
            if(bulb.bulbId === bulb2Find.bulbId ){
                res = idx;
                return res;
            }
            idx = idx + 1;
        });
        return res;
    }
    
    //~ Edit
    $scope.switchEdit = function(field, bulbId){
        if(typeof($scope.bulbs_mod[bulbId]) !== 'undefined'
                && typeof($scope.bulbs_mod[bulbId].edit[field]) !== 'undefined'){
           $scope.bulbs_mod[bulbId].edit[field] = ! $scope.bulbs_mod[bulbId].edit[field]; 
        }else $scope.switchEditOn(field, bulbId);
    };
    $scope.switchEditOn = function(field, bulbId){
//        $scope.bulbs_mod[bulbIdx] = angular.copy($scope.bulbs[bulbIdx]);
        if(typeof($scope.bulbs_mod[bulbId]) === 'undefined'){
            $scope.bulbs_mod[bulbId] = {};
            $scope.bulbs_mod[bulbId] = 
                    angular.copy($scope.bulbs_cache[bulbId]);
            $scope.bulbs_mod[bulbId].edit = {};
        }
//        $scope.bulbs_mod[bridgeId][bulbId]
        $scope.bulbs_mod[bulbId].edit[field] = true;
        $scope.bulbs_mod[bulbId].edit['serverError'] = null;
    };
    $scope.switchEditOff = function(field, bulbId){
        $scope.bulbs_mod[bulbId].edit[field] = false;
    };
    $scope.saveEdited = function(field, bulbId){
        var reqBody = {};
        reqBody[field] = $scope.bulbs_mod[bulbId][field];
        var cbSuccess = function(resp){
                var b = $scope.bulbs_cache[bulbId];
                var bIdx = findBulbIdx(b);
                $scope.bulbs[bIdx] = resp;
                $scope.switchEditOff(field, bulbId);
                $scope.bulbs_mod[bulbId].edit.serverError = null;
            };
        var cbError = function(httpResponse){
            // ~ Failure
            $scope.bulbs_mod[bulbId].edit.serverError = httpResponse.data;
        };
        switch(field){
            case 'name':
                BulbResourceService.modifyName(
                    {'bulbId' : bulbId }, // Request url parames
                    {value :  $scope.bulbs_mod[bulbId][field]}, cbSuccess, cbError

                );
                break;
        }
    };
    $scope.switchBulbOnOff = function(bulb){
        bulb.state.pending = true;
        $scope.modifyBulbState({
            enabled : !bulb.state.enabled,
            color : bulb.state.color
        }, bulb);
    };
    $scope.modifyBulbColor = function(newColor, bulb){
        $scope.modifyBulbState({
            enabled : true,
            color : newColor
        }, bulb);
    };
    $scope.modifyBulbState = function(newState, bulb){
        var cmd = {
            appId : 'APP_TYPE__BULBS_CORE',
            priority : { priority : 0},
            bulbId : bulb.bulbId,
            transitionDelay : 0,
            states : [
                newState
            ]
        };
        // Stomp WS request
        ActuatorServiceBulbs.execute(
            cmd // Request Body
        ); 
    };
    
    //~ Options
  
    //~ INIT --
    $scope.init = function(){
        console.log("INIT bulbs view..");

        loadAllBulbs();
		
		$scope.$on('Evt_BulbStateUpdated', function(e, bulbStateUpdateEvent){
            $timeout(function(){
                $scope.$apply();
            });
        });
    };
    $scope.init();
  
}

//~ BRIDGEs ////////////////////////////////////////////////////////////////////
function BridgesCtrl($scope, BridgeResourceService,  
        StompClientHolder, BridgeService, $log){
//    $scope.authenticationContext = authenticationContext;
    $scope.is_modalNewBridge_open = false;
    $scope.toggleModal_newBridge = function(){
        $scope.is_modalNewBridge_open = !$scope.is_modalNewBridge_open;
    };
    $scope.is_modalDeleteBridge_open = false;
    $scope.bridge2DelSelected = null;
    $scope.toggleModal_deleteBridge = function(bridge){
        $scope.is_modalDeleteBridge_open = !$scope.is_modalDeleteBridge_open;
        $scope.bridge2DelSelected = bridge;
        if(typeof($scope.bridge2DelSelected) === 'undefined')return;
        $scope.bridges_mod[bridge.bridgeId] = angular.copy(bridge);
        $scope.bridges_mod[bridge.bridgeId].serverError = null;
    };
    $scope.bulbSearchInProgressForBridges = {};
    $scope.bulbSearchFinishedForBridges = {};
    
    $scope.bridges = null;
    $scope.bridges_mod = [];
    
    $scope.reloadBridges = function(){
        $log.info("Reloading Bridges..");
        $scope.bridges = null;
        loadAllBridges(true);
    };
    
    function loadAllBridges(forceUpdate){
		BridgeService.bridgesByUser(forceUpdate).then(
			function(bridges){
				$scope.bridges = bridges;
                initWebsocketHandler();
			},
			function(error){
                $scope.serverError = "Error(" + httpResponse.status +")" + httpResponse.data;
			}
		);
    };
    
    function initWebsocketHandler(){
        var pushMsgHandler_BridgeSynced = function(message) {
            var bridgeSyncedEvent = JSON.parse(message.body);
            console.log("RECEIVED BridgeSyncedEvent: " +  bridgeSyncedEvent);
            $scope.bulbSearchInProgressForBridges[bridgeSyncedEvent] = false;
            $scope.bulbSearchFinishedForBridges[bridgeSyncedEvent] = true;
            
            loadAllBridges(false);
        };
        StompClientHolder.subscribe("BridgesCtrl", "/topic/bridgeSynced/", pushMsgHandler_BridgeSynced);
    }
    $scope.hideBulbSearchFinished = function(bridgeId){
        $scope.bulbSearchFinishedForBridges[bridgeId] = false;
    };
    
    //~ Edit
    $scope.switchEditOn = function(field, bridge){
        $scope.bridges_mod[bridge.bridgeId] = angular.copy(bridge);
        $scope.bridges_mod[bridge.bridgeId].edit = {};
        $scope.bridges_mod[bridge.bridgeId].edit[field] = true;
        $scope.bridges_mod[bridge.bridgeId].serverError = null;
    };
    $scope.switchEditOff = function(field, bridge){
        $scope.bridges_mod[bridge.bridgeId] = angular.copy(bridge);
        $scope.bridges_mod[bridge.bridgeId].edit = {};
        $scope.bridges_mod[bridge.bridgeId].edit[field] = false;
    };
    $scope.saveEdited = function(field, bridge){
        var reqBody = {};
        reqBody.value = bridge[field];
        var urlParams = {'bridgeId' : bridge.bridgeId}; // Request url parames
        var cbSuccess = function(resp){
            $scope.reloadBridges();
            $scope.switchEditOff(field, bridge);
        };
        var cbError = function(httpResponse){
            // ~ Failure
            $scope.bridges_mod[bridge.bridgeId].serverError = "Error(" + httpResponse.status +")" + httpResponse.data;
        };
        switch(field){
            case 'name':
                BridgeResourceService.modifyName(
                    urlParams, reqBody, cbSuccess, cbError
                );
                break;
            case 'localAddress':
                var localAddress = {
                    host : bridge.localAddress.host,
                    port : bridge.localAddress.port
                }
                BridgeResourceService.modifyLocalAddress(
                    urlParams, localAddress, cbSuccess, cbError
                );
                break;
        }

    };
    $scope.deleteBridge = function(bridge){
        BridgeResourceService.del(
            {'bridgeId' : bridge.bridgeId}, // Request url parames
//            {"macAddress": "macAddress"},
            {}, // Request Body
            function(resp){
                $scope.toggleModal_deleteBridge();
                loadAllBridges(true);
            },
            function(httpResponse){
                // ~ Failure
                $scope.bridges_mod[bridge.bridgeId].serverError = "Error(" + httpResponse.status +")" + httpResponse.data;
            }
        );
    };
    $scope.performBulbSearch = function(bridge){
        $scope.bulbSearchInProgressForBridges[bridge.bridgeId] = true;
        BridgeResourceService.search(
            {'bridgeId' : bridge.bridgeId}, // Request url parames
            {}, // Request Body
            function(resp){
            },
            function(httpResponse){
                // ~ Failure
                $scope.bridges_mod[bridge.bridgeId].serverError = "Error(" + httpResponse.status +")" + httpResponse.data;
            }
        );
    };
    
    //~ INIT --
    $scope.init = function(){
        loadAllBridges(false);
    };
    $scope.init();
}
function NewBridgeCtrl($scope, BridgeResourceService, BridgeService){
//    $scope.authenticationContext = authenticationContext;
    $scope.requestProcessing = false;
    $scope.bridge_new = BridgeService.newBridge();
    $scope.init = function(){
        $scope.bridge_new.platform = "PHILIPS_HUE";
        $scope.bridge_new.localAddress.port = "";
        $scope.bridge_new.localAddress.host = "";
        $scope.newBridgeResult = '';
        $scope.serverError = '';
        $scope.bridge_new_master = null;
    };
    $scope.modal_newBridge_close = function(){
        $scope.new_bridge_form.$setPristine();
        $scope.init();  
        $scope.toggleModal_newBridge();
    };
    //~ Communication
    $scope.createBridge = function(newBridge){
//        $scope.newBridgeResult = "RESULT";
        $scope.requestProcessing = true;
        $scope.bridge_new_master = angular.copy(newBridge);
//        $scope.bridge_new_master.apiKey = authenticationContext.apiKey;
        BridgeResourceService.create(
            {}, // Request url parames
            $scope.bridge_new_master, // Request Body
            function(resp){
                $scope.requestProcessing = false;
                $scope.serverError = "";
                $scope.newBridgeResult = resp;
                $scope.reloadBridges();
            },
            function(httpResponse){
                // ~ Failure
                $scope.requestProcessing = false;
                $scope.serverError = "Error(" + httpResponse.status +")" + httpResponse.data;
                $scope.newBridgeResult = "";
//                alert(httpResponse.data);
            }
        );
    };
    
    $scope.init();
    
}

//~ GROUPs /////////////////////////////////////////////////////////////////////
function GroupCtrl($scope, $rootScope, GlobalOptionsService, GroupService,
        GroupResourceService, BulbService, ActuatorServiceGroups, ColorConverter,
        $timeout ){

    $scope.groups = [];
    $scope.groupSelected = null;
    $scope.groupsUnsaved = {};
    $scope.usersBulbs = [];
    $scope.groupsAvailableBulbs = {};

    $scope.globalOptions = GlobalOptionsService.allOptions();
    $scope.group_opts = {
        continuousTrigger : true
    };
    $scope.viewmode = 'groupControls';
    
    $scope.is_modalDeleteGroup_open = false;
    $scope.group2DelSelected = null;
    $scope.toggleModal_deleteGroup = function(origGroup){
        $scope.is_modalDeleteGroup_open = !$scope.is_modalDeleteGroup_open;
        if($scope.is_modalDeleteGroup_open){
            $scope.switchEditOn('', origGroup);
            $scope.group2DelSelected = origGroup;
        }else{
            $scope.cancelEdit('', origGroup);
            $scope.group2DelSelected = null;
        }
    };
    
    $scope.newGroup = function(){
        $scope.viewmode = 'groupContents';
        var newGroup = GroupService.newGroup();
        $scope.groups.push(newGroup);
        $scope.groupsUnsaved[newGroup.groupId] = newGroup;
    };
    $scope.switchEditOn = function(fieldName, origGroup){
        var modGroup = angular.copy(origGroup);
        $scope.groupsUnsaved[origGroup.groupId] = modGroup;
        switch(fieldName){
            case 'name': 
                modGroup.isUnsaved = true;
                modGroup.isEditName = true;
                break;
            case 'bulbIds': 
                modGroup.isEditName = false;
                modGroup.isUnsaved = true;
                modGroup.isEditBulbIds = true;
                modGroup.currBulb = {};
                if(typeof(modGroup.bulbs) === 'undefined')modGroup.bulbs = [];
                modGroup.bulbIds = angular.copy(origGroup.bulbIds);
                $scope.calcGroupsAvailableBulbs(origGroup);
                break;
        }
    };
    $scope.cancelEdit = function(property, origGroup){
        var modGroup = $scope.groupsUnsaved[origGroup.groupId];
        if(typeof(modGroup) === 'undefined')return ; // make this method idempotent
        if(typeof(modGroup['isNew']) !== 'undefined' && modGroup['isNew']){
            removeGroupFromLocModel(origGroup);
        }else{
            delete $scope.groupsUnsaved[origGroup.groupId];
        }
    };
    
    $scope.save = function(fieldName, origGroup){
        var modGroup = $scope.groupsUnsaved[origGroup.groupId];
        if(typeof(modGroup['isNew']) !== 'undefined' && modGroup['isNew']){
            GroupService.saveNewGroup(modGroup).then(
                function(resp){
                    delete $scope.groupsUnsaved[origGroup.groupId];
                    var gIdx = groupIndexById($scope.groups, origGroup.groupId);
                    $scope.groups[gIdx] = resp;
                    resolveBulbIds4Group($scope.groups[gIdx]);
                    $scope.switchEditOn('bulbIds', resp);
                },
                function(httpResponse){
                    // ~ Failure
                    $scope.groupsUnsaved[modGroup.groupId].serverError =
                    httpResponse.data;
                    //                        "Error(" + httpResponse.status +")" + httpResponse.data;
                }
            );
        }else{
            switch(fieldName){
                case 'name':
                    GroupResourceService.modifyName(
                        { groupId: origGroup.groupId}, // Request url parames
                        modGroup, // Request Body
                        function(resp){
                            delete $scope.groupsUnsaved[origGroup.groupId];
                            var gIdx = groupIndexById($scope.groups, origGroup.groupId);
                            $scope.groups[gIdx] = resp;
                            resolveBulbIds4Group($scope.groups[gIdx]);
                        },
                        function(httpResponse){
                            // ~ Failure
                            $scope.groupsUnsaved[modGroup.groupId].serverError =
                                    httpResponse.data;
            //                        "Error(" + httpResponse.status +")" + httpResponse.data;
                        }
                    );
                    break;
                case 'bulbIds':
                    GroupResourceService.modifyAllGroupMembers(
                        { groupId: origGroup.groupId}, // Request url parames
                        modGroup, // Request Body
                        function(resp){
                            delete $scope.groupsUnsaved[origGroup.groupId];
                            var gIdx = groupIndexById($scope.groups, origGroup.groupId);
                            $scope.groups[gIdx] = resp;
                            resolveBulbIds4Group($scope.groups[gIdx]);
                        },
                        function(httpResponse){
                            // ~ Failure
                            $scope.groupsUnsaved[modGroup.groupId].serverError =
                                    httpResponse.data;
            //                        "Error(" + httpResponse.status +")" + httpResponse.data;
                        }
                    );
                    break;
            }
        }
    };
    $scope.deleteGroup = function(origGroup){
        GroupService.deleteGroup(origGroup).then(
            function(resp){
                $scope.toggleModal_deleteGroup(origGroup);
                GroupService.groupsByUser().then(function (res) {
                    $scope.groups.remove(origGroup);
//                    $scope.groups.addAll(angular.copy(res));
                });
            },
            function(httpResponse){
                // ~ Failure
                $scope.groupsUnsaved[origGroup.groupId].serverError = httpResponse.data;
//                        "Error(" + httpResponse.status +")" + httpResponse.data;
            }
        );
    };
    
    $scope.switchGroupOff = function(group){
        $scope.modifyGroupState({
            enabled : false,
            color : group.bulbs[0].state.color
        }, group);
    };
    $scope.switchEnabledState = function(group){
        var newEnabledState = false;
        angular.forEach(group.bulbs, function(bulb){
            newEnabledState = newEnabledState || bulb.state.enabled;
        });
        newEnabledState = !newEnabledState;
        $scope.modifyGroupState({
            enabled : newEnabledState,
            color : group.bulbs[0].state.color
        }, group);
    };
    $scope.modifyGroupColor = function(newColor, group){
        $scope.modifyGroupState({
            enabled : true,
            color : newColor
        }, group);
    };
    $scope.modifyGroupState = function(newState, group){
        var cmd = {
            appId : 'APP_TYPE__BULBS_CORE',
            priority : { priority : 0},
            groupId : group.groupId,
            transitionDelay : 0,
            states : [
                newState
            ]
        };
        // Stomp WS request
        ActuatorServiceGroups.execute(
            cmd // Request Body
        ); 
    };

    $scope.initColorFor = function(group){
        if( (typeof(group.bulbs) === 'undefined') || group.bulbs == null)return null;
        if(group.bulbs.length  < 1 )return null;
        if(typeof(group.bulbs[0].state) === 'undefined' || group.bulbs[0] == null )return null;
        return group.bulbs[0].state.color;
    };
    
    
    $scope.calcGroupsAvailableBulbs = function(group){
        if(typeof($scope.groupsAvailableBulbs[group.groupId]) === 'undefined'){
            $scope.groupsAvailableBulbs[group.groupId] = [];
        }else{
            $scope.groupsAvailableBulbs[group.groupId].length = 0;
        }
        if(typeof($scope.groupsUnsaved[group.groupId]) === 'undefined')
            return $scope.groupsAvailableBulbs[group.groupId];
        angular.forEach($scope.usersBulbs, function (bulb) {
            var el = bulb;
            var bulbId = bulb.bulbId;
            var alreadyUsed = false;
            angular.forEach(
                $scope.groupsUnsaved[group.groupId].bulbs, function(bulb){
                    var usedBulbId = bulb.bulbId;
                    if(usedBulbId === bulbId){
                        alreadyUsed = true;
                        return;
                    }
            });
            if(!alreadyUsed){
                $scope.groupsAvailableBulbs[group.groupId].push(el);
            }
        });
        return $scope.groupsAvailableBulbs[group.groupId];
    };
    $scope.handleBulbSelectionChange = function(origGroup){
        var unsavedGroup = $scope.groupsUnsaved[origGroup.groupId];
        console.log("Bulb Selection changed: " + unsavedGroup.currBulb);
        //TODO: Check input!
        unsavedGroup.bulbs.push(unsavedGroup.currBulb);
        unsavedGroup.bulbIds.push(unsavedGroup.currBulb.bulbId);
        unsavedGroup.currBulb = {};
        $scope.calcGroupsAvailableBulbs(origGroup);
    };
    $scope.removeBulbFromGroup = function(origGroup, bulbId){
        var unsavedGroup = $scope.groupsUnsaved[origGroup.groupId];
        unsavedGroup.bulbs.splice(indexByBulbId(unsavedGroup.bulbs, bulbId), 1);
        unsavedGroup.bulbIds.splice(bulbIdIndexById(unsavedGroup.bulbIds, bulbId), 1);
        $scope.calcGroupsAvailableBulbs(origGroup);
    };
    
    $scope.groupItemStyle = function(bulb){
        if(!bulb)return "";
        var bgColor = d3.hcl(ColorConverter.fromServerColor(bulb.state.color).colorHsl);
        
        var colorText = d3.hsl ( (bgColor.h + 90) % 360, 0.8, 0.5);
        var res = {
            'background-color' : bgColor.toString() ,
            'color' : colorText.toString()
        };
        return res;
    };
    
    //~ Local model functions
    function removeGroupFromLocModel(origGroup){
        delete $scope.groupsUnsaved[origGroup.groupId];
        $scope.groups.splice($scope.groups.indexOf(origGroup), 1);
    }
    
    function loadUsersBulbs(){
        BulbService.bulbsByUser().then(
            function(resp){
                $scope.usersBulbs = resp;
                resolveBulbIds4Groups($scope.groups);
//                initWebsocketHandler();
                angular.forEach($scope.usersBulbs, function(b){
//                    var b = $scope.usersBulbs[i];
                    $rootScope.$broadcast('Evt_BulbStateUpdated', {
                        bulbId : b.bulbId,
                        state : b.state
                    });
                });
//                for (var i in $scope.usersBulbs){
//                }
            },
            function(error){
                // ~ Failure
                $scope.serverError = error.data;
                console.log("Error loadong bulb groups: " + error.data);
            }
        );
    }
    function loadAllGroups(){
        GroupService.groupsByUser(true).then(
            function(resp){
                $scope.groups = angular.copy(resp);
                loadUsersBulbs();
            },
            function(httpResponse){
                // ~ Failure
                $scope.serverError = httpResponse.data;
            }
        );
    }
    function initWebsocketHandler(){
        var pushMsgHandler_BulbState = function(message) {
            var bulbStateUpdateEvent = JSON.parse(message.body);
            console.log("RECEIVED PUSH: " +  bulbStateUpdateEvent);
            var bulbId = {
                    localId : bulbStateUpdateEvent.bulbLocalId,
                    bridgeId : bulbStateUpdateEvent.bulbBridgeId
            };
            var bulb = bulbById($scope.usersBulbs, bulbId);
            bulb.state = bulbStateUpdateEvent.state;
//            $scope.bulbs_cache[bulbStateUpdateEvent.bulbBridgeId][bulbStateUpdateEvent.bulbLocalId].state =
//                    bulbStateUpdateEvent.state;
            $scope.$apply();
            $rootScope.$broadcast('Evt_BulbStateUpdated', bulbStateUpdateEvent);
        };
        StompClientHolder.subscribe("BulbsCtrl", "/topic/bulbupdates/", pushMsgHandler_BulbState);
    }
    
    function resolveBulbIds4Groups(groups){
        angular.forEach(groups, function(group){
            resolveBulbIds4Group(group);
        });
    }
    function resolveBulbIds4Group(group){
        if(typeof(group.bulbs) === 'undefined' || group.bulbs == null) group.bulbs = [];
        else group.bulbs.length = 0;
        angular.forEach(group.bulbIds, function(bulbId){
//                var bulb = bulbById($scope.usersBulbs, bulbId);
//                group.bulbs.push(bulb);
                group.bulbs.push(BulbService.bulbById(bulbId));
        });
    }
    
    //~ INIT --
    $scope.init = function(){
        loadAllGroups();
        $scope.$on('Evt_BulbStateUpdated', function(e, bulbStateUpdateEvent){
            $timeout(function(){
                resolveBulbIds4Groups($scope.groups);
            });
        });
    };
    $scope.init();
    
}

//~ PRESETs ////////////////////////////////////////////////////////////////////
function PresetCtrl($scope, $rootScope, $routeParams, PresetService, GlobalOptionsService, ColorConverter){

    $scope.presets = [];
    $scope.globalOptions = GlobalOptionsService.allOptions();
    $scope.presetsOptions = {
        orderByExp : 'name',
        orderReverse : false
    };
	$scope.toolsOptions = {
		applyStatesOnMod : false,
		continuousTrigger : true
	};
    
    $scope.newPreset = function(){
		$scope.presets.push(PresetService.newPreset());
	};

    $scope.init = function(){
        PresetService.presetsByUser().then(
            function(presets){
                $scope.presets = presets;
            },
            function(error){
                console.warn("Error loading presets: " + error);
            }
        );
    };
    $scope.init();
}

//~ IDENTITY ///////////////////////////////////////////////////////////////////
function IdentityCtrl($scope, $location, $routeParams, IdentityService) {
//    $scope.authenticationContext = authenticationContext;
//    $rootScope.authenticationContext.authenticated = false;
    $scope.user_master = {};
    $scope.user = {};

    $scope.reset = function(){
        $scope.user = $scope.user_master;
    };
//    $scope.reset();  

    $scope.isSignup = false;
    $scope.modeSignup = function(isSignup){
        $scope.isSignup = isSignup;
    };
    $scope.authenticationError = "";
    $scope.signUpError = "";
    //~ Communication
	$scope.signIn = function(user){
        $scope.user_master = angular.copy(user);
        $scope.user_master['remember-me'] = true;
		IdentityService.signIn($scope.user_master).then(
			function(user){
                $location.path("/core/bulbs");
			},function(error){
				$scope.authenticationError = error.data;
			}
		);
	};
    $scope.init = function(){
        $scope.apply();
    };
    $scope.signUp = function(user){
        $scope.user_master = angular.copy(user);
		IdentityService.signUp($scope.user_master).then(
			function(user){
                $location.path("/core/bridges");
			},function(error){
				$scope.signUpError = error.data;
			}
		);
    };
}

function newUUID(){
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
    var r = Math.random()*16|0, v = c == 'x' ? r : (r&0x3|0x8);
    return v.toString(16);
});
}

function indexByBulbId(bulbArray, bulbId){
    for(var i = 0; i< bulbArray.length; i++){
        var currBulb = bulbArray[i];
        if(currBulb.bulbId === bulbId){
            return i;
        }
    }
    return -1;
}
function bulbById(bulbArray, bulbId){
    for (var i = 0; i< bulbArray.length; i++){
        var bulb = bulbArray[i];
        if(bulb.bulbId === bulbId ){
            return bulb;
        }
    }
    return null;
}
function bulbIdIndexById(bulbIdArray, bulbId){
    for (var i = 0; i< bulbIdArray.length; i++){
        var currBulbId = bulbIdArray[i];
        if(currBulbId === bulbId ){
            return i;
        }
    }
    return -1;
}
function groupIndexById(groupArray, groupId){
    for (var i = 0; i< groupArray.length; i++){
        var currGroup = groupArray[i];
        if(currGroup.groupId === groupId ){
            return i;
        }
    }
    return -1;
}

