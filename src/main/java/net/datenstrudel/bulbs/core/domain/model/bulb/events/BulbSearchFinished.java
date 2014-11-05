package net.datenstrudel.bulbs.core.domain.model.bulb.events;

import net.datenstrudel.bulbs.core.domain.model.identity.BulbsPrincipal;
import net.datenstrudel.bulbs.core.domain.model.messaging.DomainEvent;

import java.util.Date;

/**
 *
 * @author Thomas Wendzinski
 */
public class BulbSearchFinished implements DomainEvent {

    //~ Member(s) //////////////////////////////////////////////////////////////
    private String bulbBridgeId;
    private BulbsPrincipal principal;
    private Date occurredOn = new Date();

    //~ Construction ///////////////////////////////////////////////////////////
    public BulbSearchFinished() {
    }
    public BulbSearchFinished(String BulbBridgeId, BulbsPrincipal principal) {
        this.bulbBridgeId = BulbBridgeId;
        this.principal = principal;
    }
    
    //~ Method(s) //////////////////////////////////////////////////////////////
    public String getBulbBridgeId() {
        return bulbBridgeId;
    }
    public BulbsPrincipal getPrincipal() {
        return principal;
    }
    
    @Override
    public Date getOccurredOn() {
        return occurredOn;
    }
    @Override
    public String getDomainId() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    //~ Private Artifact(s) ////////////////////////////////////////////////////
    private void setBulbBridgeId(String BulbBridgeId) {
            this.bulbBridgeId = BulbBridgeId;
        }
    private void setPrincipal(BulbsPrincipal principal) {
            this.principal = principal;
        }
    private void setOccurredOn(Date occurredOn) {
        this.occurredOn = occurredOn;
    }
    
}
