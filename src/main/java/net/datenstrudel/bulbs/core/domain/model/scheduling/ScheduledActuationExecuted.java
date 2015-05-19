package net.datenstrudel.bulbs.core.domain.model.scheduling;

import net.datenstrudel.bulbs.core.domain.model.messaging.DomainEvent;

import java.util.Date;

/**
 *
 * @author Thomas Wendzinski
 */
public class ScheduledActuationExecuted implements DomainEvent {

    //~ Member(s) //////////////////////////////////////////////////////////////
    private final ScheduledActuationId entityId;
    private final Date occurredOn = new Date();

    //~ Construction ///////////////////////////////////////////////////////////
    public ScheduledActuationExecuted(ScheduledActuationId entityId) {
        this.entityId = entityId;
    }
    
    //~ Method(s) //////////////////////////////////////////////////////////////
    @Override
    public String getDomainId() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    @Override
    public Date getOccurredOn() {
        return occurredOn;
    }
    
    public ScheduledActuationId getEntityId() {
        return entityId;
    }
    
    //~ Private Artifact(s) ////////////////////////////////////////////////////


}
