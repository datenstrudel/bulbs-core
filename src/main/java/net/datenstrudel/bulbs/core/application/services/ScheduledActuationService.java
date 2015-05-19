package net.datenstrudel.bulbs.core.application.services;

import net.datenstrudel.bulbs.core.domain.model.bulb.AbstractActuatorCmd;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUserId;
import net.datenstrudel.bulbs.core.domain.model.scheduling.ScheduledActuation;
import net.datenstrudel.bulbs.core.domain.model.scheduling.ScheduledActuationId;
import net.datenstrudel.bulbs.shared.domain.model.scheduling.Trigger;

import java.util.Collection;
import java.util.Set;

/**
 *
 * @author Thomas Wendzinski
 */
public interface ScheduledActuationService {
    
    public Set<ScheduledActuation> loadAllByUser(BulbsContextUserId userId);
    public ScheduledActuation loadById(BulbsContextUserId userId, ScheduledActuationId id);
    public ScheduledActuation create(
            BulbsContextUserId userId, 
            String name, boolean removeAfterExecution);
    public ScheduledActuation create(
            BulbsContextUserId userId, 
            String name, Trigger trigger, boolean removeAfterExecution);
    public ScheduledActuation createAndActivate(
            BulbsContextUserId userId, 
            String name, Trigger trigger, boolean removeAfterExecution,
            Collection<AbstractActuatorCmd> states);
    public void remove(BulbsContextUserId userId, ScheduledActuationId actId);
    
    
    public void activate(BulbsContextUserId userId, ScheduledActuationId actId);
    public void deactivate(BulbsContextUserId userId, ScheduledActuationId actId);
    
    public void modifyName(BulbsContextUserId userId, ScheduledActuationId actId, 
            String newName); //TODO: Validator
    public void modifyStates(BulbsContextUserId userId, ScheduledActuationId actId, 
            Collection<AbstractActuatorCmd> newStates);
    public void modifyTrigger(BulbsContextUserId userId, ScheduledActuationId actId, Trigger newTrigger);
    
    /**
     * For internal use only. This method actually applies the actuation commands
     * associated with the {@link ScheduledActuation}, that is given by its <code>actId</code>
     * @param userId
     * @param actId 
     */
    public void execute(BulbsContextUserId userId, ScheduledActuationId actId);
    
}
