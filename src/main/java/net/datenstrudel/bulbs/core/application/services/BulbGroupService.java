package net.datenstrudel.bulbs.core.application.services;

import net.datenstrudel.bulbs.core.domain.model.bulb.BulbId;
import net.datenstrudel.bulbs.core.domain.model.group.BulbGroup;
import net.datenstrudel.bulbs.core.domain.model.group.BulbGroupId;
import net.datenstrudel.bulbs.core.domain.model.group.ValidatorBulbGroup;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUserId;

import java.util.Collection;
import java.util.Set;

/**
 *
 * @author Thomas Wendzinski
 */
public interface BulbGroupService {

    //~ Member(s) //////////////////////////////////////////////////////////////
    //~ Method(s) //////////////////////////////////////////////////////////////
    public BulbGroup loadById(BulbsContextUserId userId, BulbGroupId groupId);
    public void remove(BulbsContextUserId userId, BulbGroupId groupId);
    public BulbGroup loadByName(BulbsContextUserId userId, String groupName);
    public Set<BulbGroup> loadAllByUser(BulbsContextUserId userId);
    public BulbGroup createNew(
            BulbsContextUserId creatorId, 
            String uniqueGroupname, 
            ValidatorBulbGroup.NotificationHandlerBulbGroup validator);
    public BulbGroup modifyName(
            BulbsContextUserId userId, 
            BulbGroupId groupId, 
            String newUniqueGroupname, 
            ValidatorBulbGroup.NotificationHandlerBulbGroup validator );
    public BulbGroup modifyGroupMembers(
            BulbsContextUserId userId, 
            BulbGroupId groupId, 
            Collection<BulbId> allBulbIds );
    
}
