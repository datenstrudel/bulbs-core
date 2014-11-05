package net.datenstrudel.bulbs.core.domain.model.bulb.events;

import net.datenstrudel.bulbs.core.domain.model.messaging.DeferrableDomainEvent;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbState;

import java.util.Date;

/**
 * @author Thomas Wendzinski
 * @version 1.0
 * @created 08-Jun-2013 22:51:43
 */
public class BulbStateChanged implements DeferrableDomainEvent {

    //~ Member(s) //////////////////////////////////////////////////////////////
    private String bulbId;
    private BulbState state;
    private Date occuredOn = new Date();

    //~ Construction ///////////////////////////////////////////////////////////
	private BulbStateChanged(){}
    public BulbStateChanged(String bulbId, BulbState state) {
        this.bulbId = bulbId;
        this.state = state;
    }

    //~ Method(s) //////////////////////////////////////////////////////////////
    public String getBulbId() {
        return bulbId;
    }
    public BulbState getState() {
        return state;
    }
    
    @Override
    public Date getOccurredOn() {
        return occuredOn;
    }
    @Override
    public String getDomainId() {
        return null;
    }

    //~ Private Artifact(s) ////////////////////////////////////////////////////
    private void setBulbId(String bulbId) {
        this.bulbId = bulbId;
    }
    private void setState(BulbState state) {
        this.state = state;
    }
    private void setOccuredOn(Date occuredOn) {
        this.occuredOn = occuredOn;
    }

    @Override
    public String eventTypeDiscriminator() {
        return "" + bulbId.hashCode();
    }

}