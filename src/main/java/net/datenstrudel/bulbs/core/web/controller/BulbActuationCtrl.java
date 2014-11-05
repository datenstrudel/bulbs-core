package net.datenstrudel.bulbs.core.web.controller;

import net.datenstrudel.bulbs.core.application.services.ActuatorService;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 *
 * @author Thomas Wendzinski
 */
@Controller
@Deprecated
public class BulbActuationCtrl {
    
    //~ Member(s) //////////////////////////////////////////////////////////////
    private static final Logger log = LoggerFactory.getLogger(BulbActuationCtrl.class);
    @Autowired
    private ActuatorService bulbActuatorService;
    
    //~ Construction ///////////////////////////////////////////////////////////
    //~ Method(s) //////////////////////////////////////////////////////////////
//    @RequestMapping (method=RequestMethod.POST, value = "/core/bulbs/actuation")
//    public void executeActuation(
//            @RequestBody DtoBulbActuatorCmd cmd,
//            Authentication authentication
//            ) throws BulbBridgeHwException{
//        String userApiKey =  ( (BulbsContextUser) authentication.getPrincipal()).getApiKey();
//        SecurityContextHolder.getContext();
//
//        if(cmd.getUserApiKey() == null) cmd.setUserApiKey(userApiKey);
//        if(log.isDebugEnabled()){
//            log.debug("Going to exec: " + cmd);
//        }
//        BulbActuatorCommand bCmd =
//                new ConverterBulbActuatorCmd().reverseConvertCollection(cmd);
//        cmd.setUserApiKey(userApiKey);
//        bulbActuatorService.execute(bCmd);
//    }
    
    //~ Private Artifact(s) ////////////////////////////////////////////////////

}
