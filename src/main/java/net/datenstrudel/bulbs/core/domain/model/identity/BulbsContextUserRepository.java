package net.datenstrudel.bulbs.core.domain.model.identity;

import net.datenstrudel.bulbs.core.domain.model.infrastructure.BCoreBaseRepository;

/**
 * Created by Thomas Wendzinski.
 */
public interface BulbsContextUserRepository
        extends BCoreBaseRepository<BulbsContextUser, BulbsContextUserId>, BulbsContextUserRepositoryExt {

    public BulbsContextUser findByEmail(String email);
    public BulbsContextUser findByApiKey(String apiKey);

}
