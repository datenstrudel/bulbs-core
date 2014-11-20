package net.datenstrudel.bulbs.core.domain.model.group;

import net.datenstrudel.bulbs.core.domain.model.bulb.AbstractActuatorCmd;
import net.datenstrudel.bulbs.core.domain.model.bulb.ActuatorDomainService;
import net.datenstrudel.bulbs.core.domain.model.bulb.BulbActuatorCommand;
import net.datenstrudel.bulbs.core.domain.model.bulb.BulbId;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbBridgeHwException;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbState;
import net.datenstrudel.bulbs.shared.domain.model.bulb.CommandPriority;
import net.datenstrudel.bulbs.shared.domain.model.identity.AppId;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Thomas Wendzinski
 */
public class GroupActuatorCommand  extends AbstractActuatorCmd<GroupActuatorCommand>
        implements Serializable{

    //~ Member(s) //////////////////////////////////////////////////////////////
    private BulbGroupId groupId;
    //~ Construction ///////////////////////////////////////////////////////////
    private GroupActuatorCommand(){}
    public GroupActuatorCommand(
            BulbGroupId groupId, 
            AppId appId,
            String userApiKey,
            CommandPriority priority,
            List<BulbState> states
            ) {
        super(appId, userApiKey, priority, states);
        this.groupId = groupId;
    }
    public GroupActuatorCommand(
            BulbGroupId groupId, 
            AppId appId,
            String userApiKey,
            CommandPriority priority,
            List<BulbState> states, 
            boolean loop) {
        super(appId, userApiKey, priority, states, loop);
        this.groupId = groupId;
    }
    /**
     * Retrieve a new actuation command with state transition. The number of states
     * is determined by the <code>transitionDelay</code>
     * @param appId 
     * @param userApiKey 
     * @param groupId
     * @param transitionStart
     * @param transitionEnd
     * @param transitionDelay in <code>ms</code>
     * @param wholeTransitionTime in <code>ms</code>
     * @return 
     */
    public static GroupActuatorCommand withStateTransition(
            BulbGroupId groupId, 
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
        hash = 17 * hash + (groupId.hashCode());
        hash = 17 * hash + (appId.hashCode());
        return hash;
    }
    @Override
    public void execute(ActuatorDomainService actuatorService) throws BulbBridgeHwException {
        actuatorService.execute(this);
    }
    
    
    public BulbGroupId getGroupId() {
        return groupId;
    }
    @Override
    public boolean sameValueAs(GroupActuatorCommand other) {
        if(other == null)return false;
        if( !super.sameValueAs(other) ) return false;
        if( !groupId.equals( other.getGroupId() ) ) return false;
        return true;
    }
    
    public List<BulbActuatorCommand> toBSingleBulbCommands(BulbGroup group){
        Assert.isTrue(this.groupId.equals(group.getId()));
        List<BulbActuatorCommand> res = new ArrayList<>(group.getBulbs().size());

        for (BulbId bulbId : group.getBulbs()) {
            res.add(
                    new BulbActuatorCommand(bulbId, appId, userApiKey, priority, states, super.isLoop())
            );
        }
        return res;
    }
    
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 97 * hash + (this.groupId != null ? this.groupId.hashCode() : 0);
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
        final GroupActuatorCommand other = (GroupActuatorCommand) obj;
        return this.sameValueAs(other);
    }

    @Override
    public String toString() {
        return "GroupActuatorCmd{" 
                + "appId=" + appId 
                + ", groupId=" + groupId 
                + ", priority=" + priority 
                + ", states=" + states 
            + '}';
    }
    

    //~ Private Artifact(s) ////////////////////////////////////////////////////
    private void setGroupId(BulbGroupId groupId) {
        this.groupId = groupId;
    }

}
