package net.datenstrudel.bulbs.core.domain.model.bulb.events;

import net.datenstrudel.bulbs.core.domain.model.bulb.BulbBridgeId;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUserId;
import net.datenstrudel.bulbs.core.domain.model.messaging.DomainEvent;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbBridgeAddress;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbsPlatform;

import java.util.Date;

/**
 *
 * @author Thomas Wendzinski
 */
public class BulbBridgeDeleted implements DomainEvent{
    
    //~ Member(s) //////////////////////////////////////////////////////////////
    private Date occurredOn = new Date();
    private BulbBridgeId bridgeId;
    private BulbsContextUserId userId;
    private BulbsPlatform platform;
    private BulbBridgeAddress bridgeAddress;
    
    //~ Construction ///////////////////////////////////////////////////////////
    private BulbBridgeDeleted() {
    }
    public BulbBridgeDeleted(BulbsContextUserId userId, BulbBridgeId bridgeId, BulbsPlatform platform, BulbBridgeAddress bridgeAddress) {
        this.userId = userId;
        this.bridgeId = bridgeId;
        this.platform = platform;
        this.bridgeAddress = bridgeAddress;
    }

    //~ Method(s) //////////////////////////////////////////////////////////////
    public BulbBridgeId getBridgeId() {
        return bridgeId;
    }
    public BulbsContextUserId getUserId() {
        return userId;
    }
    public BulbsPlatform getPlatform() {
        return platform;
    }
    public BulbBridgeAddress getBridgeAddress() {
        return bridgeAddress;
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
    private void setBridgeId(BulbBridgeId bridgeId) {
        this.bridgeId = bridgeId;
    }
    private void setOccurredOn(Date occurredOn) {
        this.occurredOn = occurredOn;
    }
    private void setUserId(BulbsContextUserId userId) {
        this.userId = userId;
    }
    
}
