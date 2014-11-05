package net.datenstrudel.bulbs.core.domain.model.group;

import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUserId;

import java.util.Set;

/**
 * @author Thomas Wendzinski
 * @version 1.0
 * @created 08-Jun-2013 22:51:42
 */
public interface BulbGroupRepository {
    
    public BulbGroupId nextIdentity(BulbsContextUserId creatorId);
    
    public BulbGroup loadById(BulbGroupId groupId);
    public BulbGroup loadByName(BulbsContextUserId userId, String groupname);
    public Set<BulbGroup> loadByOwner(BulbsContextUserId userId);
    
    public void store(BulbGroup group);
    public void remove(BulbGroupId groupId);

}