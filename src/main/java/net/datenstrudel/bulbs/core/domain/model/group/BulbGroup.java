package net.datenstrudel.bulbs.core.domain.model.group;

import net.datenstrudel.bulbs.core.domain.model.bulb.BulbId;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUserId;
import net.datenstrudel.bulbs.core.domain.model.messaging.DomainEventPublisher;
import net.datenstrudel.bulbs.shared.domain.model.Entity;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Within one <code>Bridge</code> each <code>Bulb</code> can belong to several
 * <code>BulbGroup</code>s.
 * @author derTom
 * @version 1.0
 * @updated 08-Jun-2013 22:54:59
 */
public class BulbGroup extends Entity<BulbGroup, String> {

    //~ Member(s) //////////////////////////////////////////////////////////////
    private BulbGroupId groupId;
    private String name;
	private Set<BulbId> bulbs = new HashSet<>();

    //~ Construction ///////////////////////////////////////////////////////////
	private BulbGroup(){}
    public BulbGroup(BulbGroupId groupId, String name) {
        setGroupId(groupId);
        setName(name);
    }
    
    //~ Method(s) //////////////////////////////////////////////////////////////
    public BulbGroupId getGroupId() {
        return groupId;
    }
	public String getName(){
		return this.name;
	}
    public BulbsContextUserId userId(){
        return groupId.getUserId();
    }
    public Set<BulbId> getBulbs() {
        return bulbs;
    }
    
    //~ ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public void addBulb(BulbId bulbId){
        if(this.bulbs.contains(bulbId)){
            throw new IllegalArgumentException("Bulb[" + bulbId
                    +"] already assigned to group[" + groupId + "]");
        }
        this.bulbs.add(bulbId);
    }
    public void removeBulb(BulbId bulbId){
        this.bulbs.remove(bulbId);
    }
    public void modifyName(
            String newName,
            ValidatorBulbGroup.NotificationHandlerBulbGroup notificationHandler,
            BulbGroupRepository groupRepository){
        setName(newName);
        validateExisting(notificationHandler, groupRepository);
        
        DomainEventPublisher.instance().publish(new BulbGroupUpdated(groupId));
    }
    public void updateAllBulbs(Collection<BulbId> allMembers){
        this.bulbs.clear();
        this.bulbs.addAll(allMembers);
        DomainEventPublisher.instance().publish(
                new BulbGroupUpdated(groupId)
        );
    }
    //~ ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public void validateNew(
            ValidatorBulbGroup.NotificationHandlerBulbGroup notificationHandler,
            BulbGroupRepository groupRepository){
        new ValidatorBulbGroup(notificationHandler, this, groupRepository)
                .validateNew();
    }
    public void validateExisting(
            ValidatorBulbGroup.NotificationHandlerBulbGroup notificationHandler,
            BulbGroupRepository groupRepository){
        new ValidatorBulbGroup(notificationHandler, this, groupRepository)
                .validateExisting();
    }

    //~ ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + Objects.hashCode(this.groupId);
        return hash;
    }
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final BulbGroup other = (BulbGroup) obj;
        if (!Objects.equals(this.groupId, other.groupId)) {
            return false;
        }
        return true;
    }
    @Override
    public boolean sameIdentityAs(BulbGroup other) {
        if(other == null) return false;
        return this.groupId.sameValueAs(other.getGroupId());
    }
    @Override
    public String toString() {
        return "BulbGroup{" 
                + "groupId=" + groupId 
                + ", bulbs=" + bulbs + '}';
    }

    //~ Private Artifact(s) ////////////////////////////////////////////////////
    private void setGroupId(BulbGroupId groupId) {
        this.groupId = groupId;
    }
    private void setName(String name) {
        if(StringUtils.isEmpty(name))throw new IllegalArgumentException(
                "Member 'name' must not be empty");
        this.name = name;
    }
    private void setBulbs(Set<BulbId> bulbs) {
        this.bulbs = bulbs;
    }

    
    

}