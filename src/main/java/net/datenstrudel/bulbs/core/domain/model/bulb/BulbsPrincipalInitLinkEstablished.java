package net.datenstrudel.bulbs.core.domain.model.bulb;

import net.datenstrudel.bulbs.core.domain.model.identity.BulbsPrincipal;
import net.datenstrudel.bulbs.core.domain.model.messaging.DomainEvent;

import java.util.Date;

/**
 *
 * @author Thomas Wendzinski
 */
public class BulbsPrincipalInitLinkEstablished implements DomainEvent{

    //~ Member(s) //////////////////////////////////////////////////////////////
    private BulbsPrincipal principal;
    private String contextUserId;
    private Date occuredOn = new Date();
    
    //~ Construction ///////////////////////////////////////////////////////////
    public BulbsPrincipalInitLinkEstablished(String contextUserId, BulbsPrincipal principal) {
        this.contextUserId = contextUserId;
        this.principal = principal;
    }
    
    //~ Method(s) //////////////////////////////////////////////////////////////
    public BulbsPrincipal getPrincipal() {
        return principal;
    }
    @Override
    public String getDomainId() {
        return null;
    }
    @Override
    public Date getOccurredOn() {
        return occuredOn;
    }
    public String getContextUserId() {
        return contextUserId;
    }

    @Override
    public String toString() {
        return "BulbsPrincipalInitLinkEstablished{" + "principal=" + principal + ", occuredOn=" + occuredOn + '}';
    }
    //~ Private Artifact(s) ////////////////////////////////////////////////////
}
