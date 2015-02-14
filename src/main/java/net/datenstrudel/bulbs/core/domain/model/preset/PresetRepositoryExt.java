package net.datenstrudel.bulbs.core.domain.model.preset;

import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUserId;

public interface PresetRepositoryExt {

    public PresetId nextIdentity(BulbsContextUserId userId);
}
