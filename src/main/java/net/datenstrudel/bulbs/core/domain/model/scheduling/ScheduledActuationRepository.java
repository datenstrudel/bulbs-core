package net.datenstrudel.bulbs.core.domain.model.scheduling;

import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUserId;

import java.util.Set;

/**
 *
 * @author Thomas Wendzinski
 */
public interface ScheduledActuationRepository {
    
    public ScheduledActuationId nextIdentity(BulbsContextUserId creator);
    
    public ScheduledActuation loadById(ScheduledActuationId id);
    public ScheduledActuation loadByName(BulbsContextUserId userId, String schedulerName);
    public Set<ScheduledActuation> loadByOwner(BulbsContextUserId userId) ;
    
    public void store(ScheduledActuation scheduler);
    public void remove(ScheduledActuationId id);
    
    
    
    
    
}
