package net.datenstrudel.bulbs.core.domain.model.group;

import net.datenstrudel.bulbs.core.domain.model.messaging.DomainEvent;

import java.util.Date;

/**
 *
 * @author Thomas Wendzinski
 */
public class BulbGroupUpdated implements DomainEvent{

    //~ Member(s) //////////////////////////////////////////////////////////////
    private BulbGroupId groupId;
    @Deprecated
    private String newName;
    private Date occurredOn = new Date();

    //~ Construction ///////////////////////////////////////////////////////////
    private BulbGroupUpdated() {
    }
    @Deprecated
    public BulbGroupUpdated(BulbGroupId groupId, String newName) {
        this.groupId = groupId;
        this.newName = newName;
    }
    public BulbGroupUpdated(BulbGroupId groupId) {
        this.groupId = groupId;
    }
    
    
    //~ Method(s) //////////////////////////////////////////////////////////////
    public BulbGroupId getGroupId() {
        return groupId;
    }
    public String getNewName() {
        return newName;
    }
    
    @Override
    public String getDomainId() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Date getOccurredOn() {
        return occurredOn;
    }

    //~ Private Artifact(s) ////////////////////////////////////////////////////
    private void setGroupId(BulbGroupId groupId) {
        this.groupId = groupId;
    }
    private void setNewName(String newName) {
        this.newName = newName;
    }
    private void setOccurredOn(Date occurredOn) {
        this.occurredOn = occurredOn;
    }
    
    
}
