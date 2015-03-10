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
public class GroupActuatorCommand  extends AbstractActuatorCmd<GroupActuatorCommand, BulbGroupId>
        implements Serializable{

    //~ Member(s) //////////////////////////////////////////////////////////////
    @Deprecated
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
        super(groupId, appId, userApiKey, priority, states);
        this.groupId = groupId;
    }
    public GroupActuatorCommand(
            BulbGroupId groupId, 
            AppId appId,
            String userApiKey,
            CommandPriority priority,
            List<BulbState> states, 
            boolean loop) {
        super(groupId, appId, userApiKey, priority, states, loop);
        this.groupId = groupId;
    }

    //~ Method(s) //////////////////////////////////////////////////////////////
    @Override
    public int deferredExecutionHash() {
        int hash = userApiKey.hashCode();
        hash = 17 * hash + (getTargetId().hashCode());
        hash = 17 * hash + (getAppId().hashCode());
        return hash;
    }
    @Override
    public void execute(ActuatorDomainService actuatorService) throws BulbBridgeHwException {
        actuatorService.execute(this);
    }

    /**
     * @return
     * @deprecated this overriden method is to be deleted when any persisted ActuatorCommand has been resaved.
     */
    @Override
    public BulbGroupId getTargetId() {
        BulbGroupId id = super.getTargetId();
        if(id != null) return id;
        return groupId;
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
    public boolean sameValueAs(GroupActuatorCommand other) {
        if(other == null)return false;
        if( !super.sameValueAs(other) ) return false;
        return true;
    }
    @Override
    public int hashCode() {
        int hash = super.hashCode();
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
                + ", targetId=" + targetId
                + ", priority=" + priority 
                + ", states=" + states 
            + '}';
    }
    

    //~ Private Artifact(s) ////////////////////////////////////////////////////

}
