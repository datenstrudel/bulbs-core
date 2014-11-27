package net.datenstrudel.bulbs.core.domain.model.group;

import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUserId;
import net.datenstrudel.bulbs.core.domain.model.infrastructure.BCoreBaseRepository;

import java.util.Set;

/**
 * @author Thomas Wendzinski
 * @version 1.0
 * @created 08-Jun-2013 22:51:42
 */
public interface BulbGroupRepository extends BCoreBaseRepository<BulbGroup, BulbGroupId>, BulbGroupRepositoryExt{
    
    public BulbGroup findByNameAndId_Creator(String name, BulbsContextUserId creator);
    public Set<BulbGroup> findById_Creator(BulbsContextUserId creator);

}