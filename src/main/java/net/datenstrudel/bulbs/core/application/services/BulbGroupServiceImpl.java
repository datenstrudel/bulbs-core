package net.datenstrudel.bulbs.core.application.services;

import net.datenstrudel.bulbs.core.application.facade.ModelFacadeOutPort;
import net.datenstrudel.bulbs.core.domain.model.bulb.BulbId;
import net.datenstrudel.bulbs.core.domain.model.group.*;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUserId;
import net.datenstrudel.bulbs.core.domain.model.messaging.DomainEventPublisher;
import net.datenstrudel.bulbs.core.domain.model.messaging.IllegalArgumentExceptionNotRepeatable;
import net.datenstrudel.bulbs.core.web.controller.util.NotAuthorizedException;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Set;

/**
 *
 * @author Thomas Wendzinski
 */
@Component
public class BulbGroupServiceImpl implements BulbGroupService{

    //~ Member(s) //////////////////////////////////////////////////////////////
    private static final Logger log = LoggerFactory.getLogger(BulbGroupService.class);

    private final BulbGroupRepository groupRepository;
    private final ModelFacadeOutPort outPort;

    //~ Construction ///////////////////////////////////////////////////////////
    @Autowired
    public BulbGroupServiceImpl(
            BulbGroupRepository groupRepository, 
            ModelFacadeOutPort outPort ) {
        this.groupRepository = groupRepository;
        this.outPort = outPort;
    }
    
    //~ Method(s) //////////////////////////////////////////////////////////////
    @Override
    public BulbGroup loadById(BulbsContextUserId userId, BulbGroupId groupId) {
        BulbGroup res = groupRepository.findOne(groupId);
        outPort.write(res);
        return res;
    }
    @Override
    public void remove(BulbsContextUserId userId, BulbGroupId groupId) {
        BulbGroup res = groupRepository.findOne(groupId);
        if(res == null)throw new IllegalArgumentExceptionNotRepeatable("Group["
                +groupId+"] for deletion doesn't exist!");
        groupRepository.delete(groupId);
        DomainEventPublisher.instance().publish(new BulbGroupDeleted(groupId));
    }
    @Override
    public Set<BulbGroup> loadAllByUser(BulbsContextUserId userId) {
        Set<BulbGroup> res = groupRepository.findById_Creator(userId);
        outPort.write(res);
        return res;
    }
    @Override
    public BulbGroup loadByName(BulbsContextUserId userId, String groupName) {
        BulbGroup res = groupRepository.findByNameAndId_Creator(groupName, userId);
        if(!res.getId().getCreator().sameValueAs(userId)){
            throw new NotAuthorizedException("Not allowed to access group by name!");
        }
        outPort.write(res);
        return res;
    }

    @Override
    public BulbGroup createNew(
            BulbsContextUserId creatorId,
            String uniqueGroupname, 
            ValidatorBulbGroup.NotificationHandlerBulbGroup validationNotifier) {
        log.info("Creating new BulbGroup '"+uniqueGroupname+"' by user " + creatorId);
        BulbGroup res = new BulbGroup(groupRepository.nextIdentity(creatorId), uniqueGroupname);
        res.validateNew(validationNotifier, groupRepository);
        groupRepository.save(res);
        outPort.write(res);
        DomainEventPublisher.instance().publish(new BulbGroupCreated(res.getId()));
        return res;
    }
    @Override
    public BulbGroup modifyName(
            BulbsContextUserId userId,
            BulbGroupId groupId,
            String newUniqueGroupname, 
            ValidatorBulbGroup.NotificationHandlerBulbGroup validationNotifier) {
        
        BulbGroup group = groupRepository.findOne(groupId);
        if(!group.getId().getCreator().sameValueAs(userId)){
            throw new NotAuthorizedException("Illegal attempt to modify group's name!");
        }
        group.modifyName(newUniqueGroupname, validationNotifier, groupRepository);
        groupRepository.save(group);
        outPort.write(group);
        return group;
    }
    @Override
    public BulbGroup modifyGroupMembers(
            BulbsContextUserId userId, 
            BulbGroupId groupId, 
            Collection<BulbId> allBulbIds) {
        
        BulbGroup group = groupRepository.findOne(groupId);
        if(!group.getId().getCreator().sameValueAs(userId)){
            throw new NotAuthorizedException("Illegal attempt to modify group's name!");
        }
        group.updateAllBulbs(allBulbIds);
        groupRepository.save(group);
        outPort.write(group);
        return group;
    }
}
