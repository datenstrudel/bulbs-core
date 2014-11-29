package net.datenstrudel.bulbs.core.domain.model.bulb;

import net.datenstrudel.bulbs.shared.domain.model.ValueObject;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbBridgeHwException;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbState;
import net.datenstrudel.bulbs.shared.domain.model.bulb.CommandPriority;
import net.datenstrudel.bulbs.shared.domain.model.identity.AppId;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.Date;
import java.util.List;

/**
 *
 * @author Thomas Wendzinski
 * @param <T>
 */
public abstract class AbstractActuatorCmd<T extends AbstractActuatorCmd>
        implements ValueObject<T>{

    //~ Member(s) //////////////////////////////////////////////////////////////
    protected AppId appId;
    protected String userApiKey;
    protected CommandPriority priority = CommandPriority.standard();
    /**
	 * Contains a bunch of states that are applied sequentially. The time between two
	 * changes is defined in <code>transitionDelay</code>.
	 * The list must contain at least one element.
	 */
    @NotEmpty
    protected List<BulbState> states;
    private boolean loop = false;
    
    //~ Construction ///////////////////////////////////////////////////////////
    protected AbstractActuatorCmd(){}
    /**
     * At a minimum required for sub classes.
     * @param appId
     * @param userApiKey
     * @param priority 
     * @param loop 
     */
    protected AbstractActuatorCmd(
            AppId appId,
            String userApiKey,
            CommandPriority priority,
            boolean loop){
        this.appId = appId;
        this.userApiKey = userApiKey;
        this.priority = priority;
        this.loop = loop;
    }
    public AbstractActuatorCmd(
            AppId appId,
            String userApiKey,
            CommandPriority priority,
            List<BulbState> states
            ) {
        this.appId = appId;
        this.userApiKey = userApiKey;
        this.priority = priority;
        this.states = states;
    }
    public AbstractActuatorCmd(
            AppId appId,
            String userApiKey,
            CommandPriority priority,
            List<BulbState> states, 
            boolean loop) {
        this.appId = appId;
        this.userApiKey = userApiKey;
        this.priority = priority;
        this.states = states;
        this.loop = loop;
    }
    /**
     * Retrieve a new actuation command with state transition. The number of states
     * is determined by the <code>transitionDelay</code>
     * @param appId 
     * @param userApiKey 
     * @param transitionStart
     * @param transitionEnd
     * @param transitionDelay in <code>ms</code>
     * @param wholeTransitionTime in <code>ms</code>
     * @return 
     * @deprecated
     */
    public static AbstractActuatorCmd withStateTransition(
            AppId appId,
            String userApiKey,
            BulbState transitionStart, BulbState transitionEnd,
            int transitionDelay, int wholeTransitionTime
            ){
        throw new UnsupportedOperationException("Implement me!");
    }
    
    //~ Method(s) //////////////////////////////////////////////////////////////
    public abstract int deferredExecutionHash();
    public abstract void execute(ActuatorDomainService actuatorService)throws BulbBridgeHwException;
    
    public AppId getAppId() {
        return appId;
    }
    public String getUserApiKey() {
        return userApiKey;
    }
    public CommandPriority getPriority() {
        return priority;
    }
    public List<BulbState> getStates() {
        return states;
    }
    public boolean isLoop() {
        return loop;
    }
    
    @Override
    public boolean sameValueAs(T other) {
        if(other == null)return false;
        if(!appId.equals(other.appId))return false;
        if(!userApiKey.equals(other.userApiKey))return false;
        if(!priority.equals(other.priority))return false;
        if(!states.equals(other.states))return false;

        return true;
    }
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + (this.appId != null ? this.appId.hashCode() : 0);
        hash = 97 * hash + (this.userApiKey != null ? this.userApiKey.hashCode() : 0);
        hash = 97 * hash + (this.priority != null ? this.priority.hashCode() : 0);
        hash = 97 * hash + (this.states != null ? this.states.hashCode() : 0);
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
        final T other = (T) obj;
        return this.sameValueAs(other);
    }
    
    //~ Private Artifact(s) ////////////////////////////////////////////////////
    protected void setAppId(AppId appId) {
        this.appId = appId;
    }
    public void setUserApiKey(String userApiKey) {
        this.userApiKey = userApiKey;
    }
    protected void setPriority(CommandPriority priority) {
        this.priority = priority;
    }
    protected void setStates(List<BulbState> states) {
        this.states = states;
    }
    protected void setLoop(boolean loop) {
        this.loop = loop;
    }
    
}
