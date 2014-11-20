package net.datenstrudel.bulbs.core.infrastructure.persistence.repository;

import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUserId;

/**
 * Created by Thomas Wendzinski.
 */
public interface BulbsContextUserRepositoryExtension {

    public void initIndices();
    public BulbsContextUserId nextIdentity();
}
