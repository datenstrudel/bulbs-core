package net.datenstrudel.bulbs.core.domain.model.scheduling;

import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUserId;
import net.datenstrudel.bulbs.core.domain.model.infrastructure.BCoreBaseRepository;

import java.util.Set;

/**
 *
 * @author Thomas Wendzinski
 */
public interface ScheduledActuationRepository extends BCoreBaseRepository<ScheduledActuation, ScheduledActuationId>, ScheduledActuationRepositoryExt{
    
    public ScheduledActuation findByNameAndId_Creator(String name, BulbsContextUserId creator);
    public Set<ScheduledActuation> findById_Creator(BulbsContextUserId creator) ;
}
