package net.datenstrudel.bulbs.core.domain.model.bulb;

import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUserId;

import java.util.Set;

/**
 * Created by Thomas Wendzinski.
 */
public interface BulbBridgeRepositoryExt {

    public BulbBridgeId nextIdentity();

}
