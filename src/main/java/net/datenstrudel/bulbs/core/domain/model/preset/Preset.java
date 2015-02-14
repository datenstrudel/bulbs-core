package net.datenstrudel.bulbs.core.domain.model.preset;

import net.datenstrudel.bulbs.core.domain.model.bulb.AbstractActuatorCmd;
import net.datenstrudel.bulbs.shared.domain.model.Entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Wraps the whole state (transitions) of {@link net.datenstrudel.bulbs.core.domain.model.bulb.Bulb]s.
 * Can be used as to manage sort of presets.
 */
public class Preset extends Entity<Preset, PresetId> {

    //~ Member(s) //////////////////////////////////////////////////////////////
    private String name;
	private List<AbstractActuatorCmd> states = new ArrayList<>();

    //~ Construction ///////////////////////////////////////////////////////////
	private Preset(){}
    public Preset(PresetId id, String presetName ) {
        setId(id);
        setName(presetName);
    }
    //~ Method(s) //////////////////////////////////////////////////////////////
    public String getName() {
        return name;
    }
    public void modifyName(
            String newName,
            ValidatorPreset.NotificationHandlerPreset notificationHandler,
            PresetRepository presetRepository){
        setName(newName);
        validateExisting(notificationHandler, presetRepository);
//        DomainEventPublisher.instance().publish(new PresetUpdated(id));
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
        if(!this.id.sameValueAs(other.getId()))return false;
        return true;
    }
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.id);
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
    private void setStates(List<AbstractActuatorCmd> states) {
        this.states = states;
    }
    private void setName(String name) {
        this.name = name;
    }
    
}