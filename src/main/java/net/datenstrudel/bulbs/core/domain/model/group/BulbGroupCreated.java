/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.datenstrudel.bulbs.core.domain.model.group;

import net.datenstrudel.bulbs.core.domain.model.messaging.DomainEvent;

import java.util.Date;

/**
 *
 * @author Thomas Wendzinski
 */
public class BulbGroupCreated implements DomainEvent{
    
     //~ Member(s) //////////////////////////////////////////////////////////////
    private BulbGroupId groupId;
    private Date occurredOn = new Date();

    //~ Construction ///////////////////////////////////////////////////////////
    public BulbGroupCreated() {
    }
    public BulbGroupCreated(BulbGroupId groupId) {
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
