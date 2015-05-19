package net.datenstrudel.bulbs.core.domain.model.bulb;

import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbBridgeHwException;
import net.datenstrudel.bulbs.shared.domain.model.bulb.CommandPriority;
import net.datenstrudel.bulbs.shared.domain.model.identity.AppId;

import java.io.Serializable;
import java.util.Set;

/**
 * Cancels any multi state actuation of the given entities by id.
 * @author Thomas Wendzinski
 */
public class ActuationCancelCommand extends AbstractActuatorCmd<ActuationCancelCommand, Set<BulbId>>
        implements Serializable{

    //~ Member(s) //////////////////////////////////////////////////////////////
    private Set<BulbId> entityIds;
    
    //~ Method(s) //////////////////////////////////////////////////////////////
    //~ Private Artifact(s) ////////////////////////////////////////////////////

    //~ Construction ///////////////////////////////////////////////////////////
    public ActuationCancelCommand(Set<BulbId> entityIds, 
            AppId appId, 
            String userApiKey, 
            CommandPriority priority) {
        super(entityIds, appId, userApiKey, priority, false);
        this.entityIds = entityIds;
    }
    
    public Set<BulbId> getEntityIds() {
        return entityIds;
    }

    @Override
    public int deferredExecutionHash() {
        throw new UnsupportedOperationException("Not supported.");
    }
    @Override
    public void execute(ActuatorDomainService actuatorService) throws BulbBridgeHwException {
        actuatorService.execute(this);
    }

    private void setEntityIds(Set<BulbId> entityIds) {
        this.entityIds = entityIds;
    }
    
    

}
