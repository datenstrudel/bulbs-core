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
        extends AbstractActuatorCmd<PresetActuatorCommand> {

    //~ Member(s) //////////////////////////////////////////////////////////////
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
        super(appId, userApiKey, priority, loop);
        this.presetId = presetId;
    }
    
    //~ Method(s) //////////////////////////////////////////////////////////////
    public PresetId getPresetId() {
        return presetId;
    }
    
    //~ ////////////////////////////////////////////////////////////////////////
    @Override
    public int deferredExecutionHash() {
        int hash = userApiKey.hashCode();
        hash = 17 * hash + (presetId.hashCode());
        hash = 17 * hash + (appId.hashCode());
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
        if( !presetId.equals(other.presetId) )return false;
        return true;
    }
    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 97 * hash + (this.presetId != null ? this.presetId.hashCode() : 0);
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
    private void setPresetId(PresetId presetId) {
        this.presetId = presetId;
    }
    

}
