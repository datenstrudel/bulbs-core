package net.datenstrudel.bulbs.core.domain.model.preset;

import net.datenstrudel.bulbs.core.domain.model.bulb.AbstractActuatorCmd;
import net.datenstrudel.bulbs.shared.domain.model.Entity;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Wraps the whole state of a {@link net.datenstrudel.bulbs.core.domain.model.bulb.Bulb]. Can be used as to manage sort of
 * presets.
 * @author Thomas Wendzinski
 * @version 1.0
 * @updated 08-Jun-2013 22:54:59
 */
public class Preset extends Entity<Preset, String> {

    //~ Member(s) //////////////////////////////////////////////////////////////
	private PresetId presetId;
    private String name;
	private List<AbstractActuatorCmd> states = new ArrayList<>();

    //~ Construction ///////////////////////////////////////////////////////////
	private Preset(){}
    public Preset(PresetId presetId, String presetName ) {
        setPresetId(presetId);
        setName(presetName);
    }
    //~ Method(s) //////////////////////////////////////////////////////////////
    public PresetId getPresetId() {
        return presetId;
    }
    
    public String getName() {
        return name;
    }
    public void modifyName(
            String newName,
            ValidatorPreset.NotificationHandlerPreset notificationHandler,
            PresetRepository presetRepository){
        setName(newName);
        validateExisting(notificationHandler, presetRepository);
//        DomainEventPublisher.instance().publish(new PresetUpdated(presetId));
    }

    public List<AbstractActuatorCmd> getStates() {
        return states;
    }
    public void addStates(Collection<AbstractActuatorCmd> newStates){
        this.states.addAll(newStates);
    }
    public void addState(AbstractActuatorCmd state){
        this.states.add(state);
    }
    public void replaceStates(Collection<AbstractActuatorCmd> newStates){
        this.states.clear();
        this.states.addAll(newStates);
    }
    
    // ~ ///////////////////////////////////////////////////////////////////////
    public void validateNew(
            ValidatorPreset.NotificationHandlerPreset notificationHandler,
            PresetRepository presetRepository){
        new ValidatorPreset(notificationHandler, this, presetRepository)
                .validateNew();
    }
    public void validateExisting(
            ValidatorPreset.NotificationHandlerPreset notificationHandler,
            PresetRepository presetRepository){
        new ValidatorPreset(notificationHandler, this, presetRepository)
                .validateExisting();
    }
    
    // ~ ///////////////////////////////////////////////////////////////////////
    @Override
    public boolean sameIdentityAs(Preset other) {
        if(other == null)return false;
        if(!this.presetId.sameValueAs(other.getPresetId()))return false;
        return true;
    }
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.presetId);
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
        final Preset other = (Preset) obj;
        return this.sameIdentityAs(other);
    }

    //~ Private Artifact(s) ////////////////////////////////////////////////////
    private void setPresetId(PresetId presetId) {
        if(!StringUtils.hasText(presetId.getPresetUuid()) ){
            throw new IllegalArgumentException("PresetId member 'presetUuid' must be set.");
        }
        if(presetId.getUserId() == null ){
            throw new IllegalArgumentException("PresetId member 'userId' must be set.");
        }
        this.presetId = presetId;
    }

    private void setStates(List<AbstractActuatorCmd> states) {
        this.states = states;
    }
    private void setName(String name) {
        this.name = name;
    }
    
}