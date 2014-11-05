package net.datenstrudel.bulbs.core.domain.model.group;

import net.datenstrudel.bulbs.core.domain.model.messaging.DomainEvent;

import java.util.Date;

/**
 *
 * @author Thomas Wendzinski
 */
public class BulbGroupDeleted implements DomainEvent {
    
    //~ Member(s) //////////////////////////////////////////////////////////////
    private BulbGroupId groupId;
    private Date occurredOn = new Date();

    //~ Construction ///////////////////////////////////////////////////////////
    public BulbGroupDeleted() {
    }
    public BulbGroupDeleted(BulbGroupId groupId) {
        this.groupId = groupId;
    }
    
    //~ Method(s) //////////////////////////////////////////////////////////////
    public BulbGroupId getGroupId() {
        return groupId;
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
    private void setOccurredOn(Date occurredOn) {
        this.occurredOn = occurredOn;
    }
    

}
