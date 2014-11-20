package net.datenstrudel.bulbs.core.domain.model.preset;

import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUserId;

/**
 * Created by Thomas Wendzinski.
 */
public interface PresetRepositoryExt {

    public PresetId nextIdentity(BulbsContextUserId userId);
}
