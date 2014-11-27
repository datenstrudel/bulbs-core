package net.datenstrudel.bulbs.core.domain.model.group;

import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUserId;

/**
 * Created by Thomas Wendzinski.
 */
public interface BulbGroupRepositoryExt {

    public BulbGroupId nextIdentity(BulbsContextUserId creatorId);
}
