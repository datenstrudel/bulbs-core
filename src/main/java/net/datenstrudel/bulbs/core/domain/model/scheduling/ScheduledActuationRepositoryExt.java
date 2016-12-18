package net.datenstrudel.bulbs.core.domain.model.scheduling;

import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUserId;

/**
 * Created by Thomas Wendzinski.
 */
public interface ScheduledActuationRepositoryExt {

    ScheduledActuationId nextIdentity(BulbsContextUserId creator);

}
