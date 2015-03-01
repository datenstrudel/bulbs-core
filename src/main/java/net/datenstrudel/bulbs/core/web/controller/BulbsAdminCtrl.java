package net.datenstrudel.bulbs.core.web.controller;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.AuthorizationScope;
import net.datenstrudel.bulbs.core.application.facade.ModelFacadeOutPort;
import net.datenstrudel.bulbs.core.application.services.BulbBridgeAdminService;
import net.datenstrudel.bulbs.core.domain.model.bulb.BulbBridge;
import net.datenstrudel.bulbs.core.domain.model.bulb.BulbBridgeId;
import net.datenstrudel.bulbs.core.domain.model.bulb.BulbId;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUser;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbBridgeAddress;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbBridgeHwException;
import net.datenstrudel.bulbs.shared.domain.model.client.bulb.DtoBulb;
import net.datenstrudel.bulbs.shared.domain.model.client.bulb.DtoBulbBridge;
import net.datenstrudel.bulbs.shared.domain.model.client.common.DtoSingleValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 *
 * @author Thomas Wendzinski
 */
@RestController
@RequestMapping(value = "/core/")
@Api(value = "bridges", description = "Hardware oriented device management", position = 20)
public class BulbsAdminCtrl {

    //~ Member(s) //////////////////////////////////////////////////////////////
    private static final Logger log = LoggerFactory.getLogger(BulbsAdminCtrl.class);

    @Autowired
    private BulbBridgeAdminService bulbBridgeAdminService;
    @Autowired 
    private ModelFacadeOutPort modelPort;
    
    //~ Construction ///////////////////////////////////////////////////////////
    
    //~ Method(s) //////////////////////////////////////////////////////////////
    @AuthorizationScope(scope = "global", description = "Desc")
    @RequestMapping (method=RequestMethod.POST, value = "bridges")
    public DtoBulbBridge findAndCreateBulbBridge(
            @Valid @ApiParam(required = true) @RequestBody DtoBulbBridge newBridge,
            Authentication authentication
            )throws BulbBridgeHwException{
        String userApiKey =  ((BulbsContextUser)authentication.getPrincipal()).getApiKey();
        
        log.info("Searching for new bulb bridge.. ");
        
        modelPort.registerConverterFor(DtoBulbBridge.class);
        BulbBridge res = bulbBridgeAdminService.findAndCreateBulbBridge(
                newBridge.getPlatform(),
                newBridge.getLocalAddress(), userApiKey);
        log.info(" -- New Bridge found: " + res);
        return modelPort.outputAs(DtoBulbBridge.class);
    }
    
    @RequestMapping (method=RequestMethod.GET, value = "bridges")
    public Set<DtoBulbBridge> allBridges(Authentication authentication){
        String userApiKey =  ((BulbsContextUser)authentication.getPrincipal()).getApiKey();
        
        log.info("Loading all Bridges.. ");
        modelPort.registerConverterFor(DtoBulbBridge.class);
        bulbBridgeAdminService.bridgesByContextUser(userApiKey);
        return modelPort.outputAsSet(DtoBulbBridge.class);
    }

    @RequestMapping (method=RequestMethod.PUT, value = "bridges/{bridgeId}/name")
    public DtoBulbBridge modifyBridgeName(
            @PathVariable(value = "bridgeId") String bridgeUuid,
            @ApiParam(required = true) @Valid @RequestBody DtoSingleValue input,
            Authentication authentication
            )throws BulbBridgeHwException{
        BulbBridgeId bridgeId = new BulbBridgeId(bridgeUuid);
        String userApiKey =  ((BulbsContextUser)authentication.getPrincipal()).getApiKey();
        
        bulbBridgeAdminService.modifyBridgeName(userApiKey, bridgeId, input.getValue());

        //~ Return updated bridge
        modelPort.registerConverterFor(DtoBulbBridge.class);
        bulbBridgeAdminService.loadBridge(bridgeId, userApiKey);
        return modelPort.outputAs(DtoBulbBridge.class);
    }
    @RequestMapping (method=RequestMethod.PUT, value = "bridges/{bridgeId}/localAddress")
    public DtoBulbBridge modifyBridgeAddress(
            @PathVariable(value = "bridgeId") String bridgeUuid,
            @Valid @RequestBody BulbBridgeAddress newAddress,
            Authentication authentication
    )throws BulbBridgeHwException{
        BulbBridgeId bridgeId = new BulbBridgeId(bridgeUuid);
        String userApiKey =  ((BulbsContextUser)authentication.getPrincipal()).getApiKey();

        bulbBridgeAdminService.modifyLocalAddress(userApiKey, bridgeId, newAddress);

        //~ Return updated bridge
        modelPort.registerConverterFor(DtoBulbBridge.class);
        bulbBridgeAdminService.loadBridge(bridgeId, userApiKey);
        return modelPort.outputAs(DtoBulbBridge.class);
    }
    
    @RequestMapping (method=RequestMethod.DELETE, value = "bridges/{bridgeUUId}")
    public String deleteBridge(
            @PathVariable(value = "bridgeUUId") String bridgeUUId,
            Authentication authentication
            ){
        BulbBridgeId bridgeId = new BulbBridgeId(bridgeUUId);
        String userApiKey =  ((BulbsContextUser)authentication.getPrincipal()).getApiKey();
        
        log.info("Going to DELETE bridge with ID '"+bridgeUUId+"'.. ");
        bulbBridgeAdminService.removeBulbBridge(userApiKey, bridgeId);
        return "";
    }
    
    @RequestMapping (method=RequestMethod.POST, value = "bridges/{bridgeUUId}/searchBulbs")
    public String performBulbSearch(
            @PathVariable(value = "bridgeUUId") String bridgeUUId,
            Authentication authentication
            ) throws BulbBridgeHwException{
        
        String userApiKey =  ((BulbsContextUser)authentication.getPrincipal()).getApiKey();
        
        log.info("Triggering bulb search.. ");
        bulbBridgeAdminService.performBulbSearch(new BulbBridgeId(bridgeUUId), userApiKey);
        return "Search started.";
    }
    @RequestMapping (method=RequestMethod.POST, value = "bridges/doSyncToHardwareState")
    public String allBridgesSyncToHardwareState(
            Authentication authentication
            ) throws BulbBridgeHwException{
        
        String userApiKey =  ((BulbsContextUser)authentication.getPrincipal()).getApiKey();
        
        bulbBridgeAdminService.syncAllBridges(userApiKey);
        return "{msg:'OK. Changes are going to be populated via messaging..'}";
    }
    
    //~ Bulbs ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    @RequestMapping (method=RequestMethod.GET, value = "bulbs")
    public Set<DtoBulb> bulbsForUser(Authentication authentication){
        String userApiKey =  ((BulbsContextUser)authentication.getPrincipal()).getApiKey();
        modelPort.registerConverterFor(DtoBulb.class);
        bulbBridgeAdminService.bulbsByContextUser(userApiKey);
        return modelPort.outputAsSet(DtoBulb.class);
    }
    @RequestMapping (method=RequestMethod.PUT, value = "bulbs/{bulbId}/name")
    public DtoBulb modifyBulbName(
            @NotNull @PathVariable (value = "bulbId") String _bulbId,
            @Valid @RequestBody() DtoSingleValue input,
            Authentication authentication
            )throws BulbBridgeHwException{
        
        String userApiKey =  ((BulbsContextUser)authentication.getPrincipal()).getApiKey();
        BulbId bulbId = BulbId.fromSerialized(_bulbId);

        bulbBridgeAdminService.modifyBulbName(userApiKey, bulbId, input.getValue());

        //~ Return updated bulb
        modelPort.registerConverterFor(DtoBulb.class);
        bulbBridgeAdminService.loadBulb(bulbId, userApiKey);
        return modelPort.outputAs(DtoBulb.class);
    }
    //~ Private Artifact(s) ////////////////////////////////////////////////////

}
