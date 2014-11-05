package net.datenstrudel.bulbs.core.application.services;

import net.datenstrudel.bulbs.core.domain.model.bulb.AbstractActuatorCmd;
import net.datenstrudel.bulbs.core.domain.model.bulb.ActuatorDomainService;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbBridgeHwException;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 *
 * @author Thomas Wendzinski
 */
@Service(value="bulbActuatorServiceImpl")
public class ActuatorServiceImpl implements ActuatorService{
    //~ Member(s) //////////////////////////////////////////////////////////////
    private static final Logger log = LoggerFactory.getLogger(BulbsContextUserServiceImpl.class);
    @Autowired
    private ActuatorDomainService actuatorDomainService;
    
    //~ Construction ///////////////////////////////////////////////////////////
    //~ Method(s) //////////////////////////////////////////////////////////////
    @Override
    public void executeDeferred(
            AbstractActuatorCmd command)throws BulbBridgeHwException {
        actuatorDomainService.executeDeferred(command);
    }
    @Override
    public void execute(
            AbstractActuatorCmd command)throws BulbBridgeHwException {
        actuatorDomainService.execute(command);
    }
    @Override
    public void execute(
            Collection<? extends AbstractActuatorCmd> commands)throws BulbBridgeHwException {
        actuatorDomainService.execute(commands);
    }
    
    //~ Private Artifact(s) ////////////////////////////////////////////////////

}
