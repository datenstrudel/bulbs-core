package net.datenstrudel.bulbs.core.domain.model.preset;

import net.datenstrudel.bulbs.core.domain.model.bulb.AbstractActuatorCmd;
import net.datenstrudel.bulbs.core.domain.model.bulb.ActuatorDomainService;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbBridgeHwException;
import net.datenstrudel.bulbs.shared.domain.model.bulb.CommandPriority;
import net.datenstrudel.bulbs.shared.domain.model.identity.AppId;

/**
 * Triggers a preset to be executed.
 */
public class PresetActuatorCommand
        extends AbstractActuatorCmd<PresetActuatorCommand, PresetId> {

    //~ Member(s) //////////////////////////////////////////////////////////////
    @Deprecated
    private PresetId presetId;
    
    
    //~ Construction ///////////////////////////////////////////////////////////
    private PresetActuatorCommand() {
    }
    public PresetActuatorCommand(
            AppId appId, 
            String userApiKey, 
            CommandPriority priority, 
            PresetId presetId,
            boolean loop) {
        super(presetId, appId, userApiKey, priority, loop);
        this.presetId = presetId;
    }
    
    //~ Method(s) //////////////////////////////////////////////////////////////

    //~ ////////////////////////////////////////////////////////////////////////
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
    
    @Override
    public boolean sameValueAs(PresetActuatorCommand other) {
        if(other == null)return false;
        if(this == other) return true;
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
        final PresetActuatorCommand other = (PresetActuatorCommand) obj;
        return this.sameValueAs(other);
    }

    //~ Private Artifact(s) ////////////////////////////////////////////////////

}
