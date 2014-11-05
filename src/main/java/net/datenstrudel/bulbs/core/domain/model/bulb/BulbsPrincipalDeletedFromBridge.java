/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.datenstrudel.bulbs.core.domain.model.bulb;

import net.datenstrudel.bulbs.core.domain.model.identity.BulbsPrincipal;
import net.datenstrudel.bulbs.core.domain.model.messaging.DomainEvent;

import java.util.Date;

/**
 *
 * @author Thomas Wendzinski
 */
public class BulbsPrincipalDeletedFromBridge implements DomainEvent {

    //~ Member(s) //////////////////////////////////////////////////////////////
    private BulbsPrincipal principalDeleted;
    private String contextUserId;
    private Date occuredOn = new Date();

    //~ Construction ///////////////////////////////////////////////////////////
    private BulbsPrincipalDeletedFromBridge() {
    }
    public BulbsPrincipalDeletedFromBridge(BulbsPrincipal principalDeleted, String contextUserId) {
        this.principalDeleted = principalDeleted;
        this.contextUserId = contextUserId;
    }
    
    //~ Method(s) //////////////////////////////////////////////////////////////
    @Override
    public String getDomainId() {
        return null;
    }
    @Override
    public Date getOccurredOn() {
        return occuredOn;
    }

    public BulbsPrincipal getPrincipalDeleted() {
        return principalDeleted;
    }
    public String getContextUserId() {
        return contextUserId;
    }

    //~ Private Artifact(s) ////////////////////////////////////////////////////

}
