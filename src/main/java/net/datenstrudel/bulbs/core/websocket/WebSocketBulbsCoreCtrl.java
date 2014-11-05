package net.datenstrudel.bulbs.core.websocket;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import net.datenstrudel.bulbs.core.application.facade.DtoConverterRegistry;
import net.datenstrudel.bulbs.core.application.facade.converters.bulb.ConverterBulbActuatorCmd;
import net.datenstrudel.bulbs.core.application.facade.converters.group.ConverterGroupActuatorCmd;
import net.datenstrudel.bulbs.core.application.services.ActuatorService;
import net.datenstrudel.bulbs.core.domain.model.bulb.ActuationCancelCommand;
import net.datenstrudel.bulbs.core.domain.model.bulb.BulbActuatorCommand;
import net.datenstrudel.bulbs.core.domain.model.group.GroupActuatorCommand;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUser;
import net.datenstrudel.bulbs.core.domain.model.preset.PresetActuatorCommand;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbBridgeHwException;
import net.datenstrudel.bulbs.shared.domain.model.client.bulb.DtoActuationCancelCmd;
import net.datenstrudel.bulbs.shared.domain.model.client.bulb.DtoBulbActuatorCmd;
import net.datenstrudel.bulbs.shared.domain.model.client.group.DtoGroupActuatorCmd;
import net.datenstrudel.bulbs.shared.domain.model.client.preset.DtoPresetActuatorCmd;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.security.Principal;

/**
 * This API is capable of handling requests via Websockets or Standard Http Requests.
 * @author Thomas Wendzinski
 */
@RestController
//authorizations = {@Authorization("auth")},
@RequestMapping("/core")
@Api( value = "actuation",
        description = "This API is capable of handling requests via Websockets or Standard Http Requests.",
        produces = MediaType.APPLICATION_JSON_VALUE,
        basePath="/core/",
        position = 10)
public class WebSocketBulbsCoreCtrl {

    //~ Member(s) //////////////////////////////////////////////////////////////
    private static final Logger log = LoggerFactory.getLogger(WebSocketBulbsCoreCtrl.class);
    @Autowired
    private ActuatorService actuatorService;
    @Autowired
    private DtoConverterRegistry converterFactory;
    
    //~ Construction ///////////////////////////////////////////////////////////
    
    //~ Method(s) //////////////////////////////////////////////////////////////
    /**
     * Client must call <code>/core/bulbs/actuation</code> due to app destination 
     * prefix is set to <code>core</code> - see WebSocketConfig for more details
     * 
     * @param cmd JSON encoded {@link DtoBulbActuatorCmd} expected here
     * @param principal 
     */
    @ApiOperation(value = "Control a single, specific bulb.", httpMethod = "POST")
    @RequestMapping (method= RequestMethod.POST, value = "/bulbs/actuation")
    @MessageMapping({"/bulbs/actuation"})
	public void executeBulbActuation(
            @Valid @Payload @RequestBody @ApiParam DtoBulbActuatorCmd cmd,
            Principal principal) {

        String userApiKey =  ((BulbsContextUser)((Authentication)principal).getPrincipal()).getApiKey();

        BulbActuatorCommand bCmd =
                new ConverterBulbActuatorCmd().reverseConvert(cmd);
        bCmd.setUserApiKey(userApiKey);

        try {
            actuatorService.executeDeferred(bCmd);
        } catch (BulbBridgeHwException ex) {
            log.error(ex.getMessage(), ex);
        }
	}

    @ApiOperation(value = "Control a group of bulbs.", httpMethod = "POST")
    @RequestMapping (method= RequestMethod.POST, value = "/groups/actuation")
    @MessageMapping({"/groups/actuation"})
	public void executeGroupActuation(
            @Valid @Payload @RequestBody DtoGroupActuatorCmd cmd,
            Principal principal) {

        String userApiKey =  ((BulbsContextUser)((Authentication)principal).getPrincipal()).getApiKey();
//        DtoGroupActuatorCmd cmd = gson.fromJson(
//                new String( (byte[]) body.getPayload()),
//                DtoGroupActuatorCmd.class);
        GroupActuatorCommand gCmd =
                new ConverterGroupActuatorCmd().reverseConvert(cmd);
        gCmd.setUserApiKey(userApiKey);

        try {
            actuatorService.executeDeferred(gCmd);
        } catch (BulbBridgeHwException ex) {
            log.error(ex.getMessage(), ex);
        }
	}

    @ApiOperation(value = "Start a preset.", httpMethod = "POST")
    @RequestMapping (method= RequestMethod.POST, value = "/presets/actuation")
    @MessageMapping({"/presets/actuation"})
	public void executePresetActuation(
            @Valid @Payload @RequestBody DtoPresetActuatorCmd cmd,
            Principal principal) {

        String userApiKey =  ((BulbsContextUser)((Authentication)principal).getPrincipal()).getApiKey();
        PresetActuatorCommand presetCmd =
                converterFactory.converterForDomain(PresetActuatorCommand.class).reverseConvert(cmd);
        presetCmd.setUserApiKey(userApiKey);

        try {
            actuatorService.execute(presetCmd);
        } catch (BulbBridgeHwException ex) {
            log.error(ex.getMessage(), ex);
        }
	}

    @ApiOperation(value = "Stop transitions of bulbs.", httpMethod = "POST")
    @RequestMapping (method= RequestMethod.POST, value = "/bulbs/actuation/cancel")
    @MessageMapping({"/bulbs/actuation/cancel"})
	public void executeCancelActuation(
            @Valid @Payload @RequestBody DtoActuationCancelCmd cmd,
            Principal principal) {

        String userApiKey =  ((BulbsContextUser)((Authentication)principal).getPrincipal()).getApiKey();
        ActuationCancelCommand cancelCmd =
                converterFactory.converterForDomain(ActuationCancelCommand.class).reverseConvert(cmd);
        cancelCmd.setUserApiKey(userApiKey);

        try {
            actuatorService.execute(cancelCmd);
        } catch (BulbBridgeHwException ex) {
            log.error(ex.getMessage(), ex);
        }
	}
    //~ Private Artifact(s) ////////////////////////////////////////////////////

}
