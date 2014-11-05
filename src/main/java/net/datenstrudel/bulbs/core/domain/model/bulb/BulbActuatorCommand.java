package net.datenstrudel.bulbs.core.domain.model.bulb;

import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbBridgeHwException;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbState;
import net.datenstrudel.bulbs.shared.domain.model.bulb.CommandPriority;
import net.datenstrudel.bulbs.shared.domain.model.identity.AppId;

import java.io.Serializable;
import java.util.List;

/**
 * Wraps all information necessary in order to invoke the change of a Bulb's state.
 * 
 * @author Thomas Wendzinski
 * @version 1.0
 * @created 08-Jun-2013 23:02:11
 */
 public class BulbActuatorCommand 
        extends AbstractActuatorCmd<BulbActuatorCommand>
        implements Serializable{

    //~ Member(s) //////////////////////////////////////////////////////////////
	private BulbId bulbId;
	
    //~ Construction ///////////////////////////////////////////////////////////
	private BulbActuatorCommand(){}
    public BulbActuatorCommand(BulbActuatorCommand cmd){
        this(cmd.bulbId, cmd.appId, cmd.userApiKey, cmd.priority, cmd.states);
    }
    public BulbActuatorCommand(
            BulbId bulbId, 
            AppId appId,
            String userApiKey,
            CommandPriority priority,
            List<BulbState> states
            ) {
        super(appId, userApiKey, priority, states);
        this.bulbId = bulbId;
    }
    public BulbActuatorCommand(
            BulbId bulbId, 
            AppId appId,
            String userApiKey,
            CommandPriority priority,
            List<BulbState> states, 
            boolean loop) {
        super(appId, userApiKey, priority, states, loop);
        this.bulbId = bulbId;
    }
    /**
     * Retrieve a new actuation command with state transition. The number of states
     * is determined by the <code>transitionDelay</code>
     * @param appId 
     * @param userApiKey 
     * @param bulbId
     * @param transitionStart
     * @param transitionEnd
     * @param transitionDelay in <code>ms</code>
     * @param wholeTransitionTime in <code>ms</code>
     * @return 
     */
    public static BulbActuatorCommand withStateTransition(
            BulbId bulbId, 
            AppId appId,
            String userApiKey,
            BulbState transitionStart, BulbState transitionEnd,
            int transitionDelay, int wholeTransitionTime
            ){
        throw new UnsupportedOperationException("Implement me!");
    }
    
    //~ Method(s) //////////////////////////////////////////////////////////////
    @Override
    public int deferredExecutionHash() {
        int hash = userApiKey.hashCode();
        hash = 17 * hash + (getBulbId().hashCode());
        hash = 17 * hash + (getAppId().hashCode());
        return hash;
    }
    @Override
    public void execute(ActuatorDomainService actuatorService) throws BulbBridgeHwException{
        actuatorService.execute(this);
    }
    
    
    public BulbId getBulbId() {
        return bulbId;
    }

    @Override
    public boolean sameValueAs(BulbActuatorCommand other) {
        if(other == null)return false;
        if( !super.sameValueAs(other) ) return false;
        if(!bulbId.equals(other.bulbId))return false;
        return true;
    }
    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 97 * hash + (this.bulbId != null ? this.bulbId.hashCode() : 0);
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
        final BulbActuatorCommand other = (BulbActuatorCommand) obj;
        return this.sameValueAs(other);
    }

    @Override
    public String toString() {
        return "BulbActuatorCommand{" 
                + "appId=" + appId 
                + ", bulbId=" + bulbId 
                + ", priority=" + priority 
                + ", states=" + states 
            + '}';
    }
    
    //~ Private Artifact(s) ////////////////////////////////////////////////////
    private void setBulbId(BulbId bulbId) {
        this.bulbId = bulbId;
    }
    
}