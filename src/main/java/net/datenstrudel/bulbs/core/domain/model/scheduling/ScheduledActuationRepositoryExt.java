package net.datenstrudel.bulbs.core.domain.model.scheduling;

import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUserId;
import net.datenstrudel.bulbs.core.domain.model.preset.PresetId;

import java.util.Set;

/**
 * Created by Thomas Wendzinski.
 */
public interface ScheduledActuationRepositoryExt {
    public ScheduledActuationId nextIdentity(BulbsContextUserId creator);

    public Set<ScheduledActuation> findByStatesContainsTargetId(PresetId presetId) ;
}
