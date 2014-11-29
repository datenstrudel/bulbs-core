//mongeez formatted javascript

//changeset datenstrudel:bulbsContextUser
db.runCommand({dropIndexes: "bulbsContextUser", index: "*"});
db.bulbsContextUser.find().forEach(function (input) {
    var bulbsPrincipals = [];
    var tmpPtincipal;
    for (var i = 0; i< input.bulbsPrincipals.length; i++){
        tmpPtincipal = input.bulbsPrincipals[i];
        bulbsPrincipals.push({
            "username": tmpPtincipal.username,
            "appId": {
                "uniqueAppName": tmpPtincipal.appType
            },
            "bulbBridgeId": tmpPtincipal.bulbBridgeId,
            "state": tmpPtincipal.state
        });
    }
    db.bulbsContextUser.remove({_id: input._id});
    db.bulbsContextUser.insert(
        {
            "_id": {
                "uuid": input._id.str
            },
            "_class": "net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUser",
            "email": input.email,
            "credentials": input.credentials,
            "nickname": input.nickname,
            "apiKey": input.apiKey,
            "bulbsPrincipals" : bulbsPrincipals
        }
    );
});

//changeset datenstrudel:bulbBridge
db.runCommand({dropIndexes: "bulbBridge", index: "*"});
db.bulbBridge.find().forEach(function (input) {
    var convertedBulbs = [];
    var tmpBulb;
    for (var i = 0; i < input.bulbs.length; i++){
        tmpBulb = input.bulbs[i];
        var tmpColor = JSON.parse(tmpBulb.state.color);
        //var tmpColor = tmpBulb.state.color;
        convertedBulbs.push(
            {
                "_id": {
                    "localId": tmpBulb.bulbId,
                    "bridgeId": {
                        "uuId": input.bulbBridgeId
                    }
                },
                "platform": input.platform,
                "name": tmpBulb.name,
                "bulbAttributes": tmpBulb.bulbAttributes,
                "online": tmpBulb.online,
                "priorityCoordinator": {},
                "state": {
                    "color": {
                        "_class": "net.datenstrudel.bulbs.shared.domain.model.color.ColorHSB",
                        "brightness": tmpColor.objectContent.brightness,
                        "hue": tmpColor.objectContent.hue,
                        "saturation": tmpColor.objectContent.saturation,
                        "COLOR_SCHEME": "HSB"
                    },
                    "enabled": true,
                    "transitionDelay": 0
                }
            }
        );
    }

    db.bulbBridge.remove({_id: input._id});
    var hostPort = input.localAddress.split(":");
    db.bulbBridge.insert(
        {
            "_id": {
                "uuId": input.bulbBridgeId
            },
            "_class": "net.datenstrudel.bulbs.core.domain.model.bulb.BulbBridge",
            "platform": input.platform,
            "bridgeAttributes": input.bridgeAttributes,
            "macAddress": input.macAddress,
            "localAddress": {
                "host": hostPort[0],
                "port": hostPort[1]
            },
            "name": input.name,
            "online": input.online,
            "owner": {
                "uuid": input.owner
            },
            "bulbs": convertedBulbs
        }
    );
});

//changeset datenstrudel:bulbGroup
db.runCommand({dropIndexes: "bulbGroup", index: "*"});
db.bulbGroup.find().forEach(function (input) {
    var convertedBulbs = [];
    var tmpBulb;
    for (var i = 0; i < input.bulbs.length; i++){
        tmpBulb = input.bulbs[i];
        convertedBulbs.push(
            {
                "localId": tmpBulb.bulbId,
                "bridgeId": {
                    "uuId": tmpBulb.bridgeUUID
                }
            }
        );
    }

    db.bulbGroup.remove({_id: input._id});
    db.bulbGroup.insert(
        {
            "_id": {
                "creator": {
                    "uuid": input.owner
                },
                "groupUuid": input.bulbGroupUuid
            },
            "_class": "net.datenstrudel.bulbs.core.domain.model.group.BulbGroup",
            "name": input.name,
            "bulbs": convertedBulbs
        }
    );
});

//changeset datenstrudel:preset
db.runCommand({dropIndexes: "preset", index: "*"});
db.preset.find().forEach(function (input) {
    var convertedStates = [];

    var convertBulbState = function (stateIn){
        return {
            "color": {
                "_class": "net.datenstrudel.bulbs.shared.domain.model.color.ColorHSB",
                "brightness": stateIn.color.objectContent.brightness,
                "hue": stateIn.color.objectContent.hue,
                "saturation": stateIn.color.objectContent.saturation,
                "COLOR_SCHEME": "HSB"
            },
            "enabled": stateIn.enabled,
            "transitionDelay": stateIn.transitionDelay
        };
    };
    var convertBulbCmd = function (cmdIn){
        var convStates = [];
        for(var i = 0; i< cmdIn.objectContent.states.length; i++){
            convStates.push(convertBulbState(cmdIn.objectContent.states[i]));
        }
        var res = {
            "bulbId": cmdIn.objectContent.bulbId,
            "appId":cmdIn.objectContent.appId,
            "priority": cmdIn.objectContent.priority,
            "states": convStates,
            "created": "Sat, 29 Nov 2014 01:48:26 GMT +01:00",
            "loop": false,
            "_class": "net.datenstrudel.bulbs.core.domain.model.bulb.BulbActuatorCommand"
        };
        return res;
    };
    var convertGroupCmd = function convertGroupActuationCmd(cmdIn){
        var convStates = [];
        for(var i = 0; i< cmdIn.objectContent.states.length; i++){
            convStates.push(convertBulbState(cmdIn.objectContent.states[i]));
        }
        var res = {
            "groupId": {
                "creator": {
                    "uuid": cmdIn.objectContent.groupId.userId.uuid
                },
                "groupUuid": cmdIn.objectContent.groupId.groupUuid
            },
            "appId":cmdIn.objectContent.appId,
            "priority": cmdIn.objectContent.priority,
            "states": convStates,
            "created": "Sat, 29 Nov 2014 01:48:26 GMT +01:00",
            "loop": false,
            "_class": "net.datenstrudel.bulbs.core.domain.model.group.GroupActuatorCommand"
        };
        return res;
    };

    var srcStates = JSON.parse(input.states);
    //var srcStates = input.states;
    var tmpState;
    for (var i = 0; i < srcStates.length; i++){
        tmpState = srcStates[i];
        if(tmpState.objectType === "de.bulbs.core.domain.model.bulb.BulbActuatorCommand"){
            convertedStates.push(convertBulbCmd(tmpState));
        }else if (tmpState.objectType === "de.bulbs.core.domain.model.bulb.GroupActuatorCommand"){
            convertedStates.push(convertGroupCmd(tmpState));
        }
    }

    db.preset.remove({_id: input._id});
    db.preset.insert(
        {
            "_id": {
                "presetUuid": input.presetUuid,
                "creator": {
                    "uuid": input.userId
                }
            },
            "_class": "net.datenstrudel.bulbs.core.domain.model.preset.Preset",
            "name": input.name,
            "states": convertedStates
        }
    );
});

//changeset datenstrudel:publishedMessageTracker
db.runCommand({dropIndexes: "publishedMessageTracker", index: "*"});
db.publishedMessageTracker.find().forEach(function (input) {
    db.publishedMessageTracker.remove({_id: input._id});
    db.publishedMessageTracker.insert({
        "_id": input._id,
        "_class": "net.datenstrudel.bulbs.core.application.messaging.eventStore.PublishedMessageTracker",
        "type": "notificationQueue__bulbs_core",
        "mostRecentPublishedStoredEventId": input.mostRecentPublishedStoredEventId
    });
});


