package net.datenstrudel.bulbs.core.application.services;

import net.datenstrudel.bulbs.core.domain.model.bulb.AbstractActuatorCmd;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUserId;
import net.datenstrudel.bulbs.core.domain.model.preset.Preset;
import net.datenstrudel.bulbs.core.domain.model.preset.PresetId;
import net.datenstrudel.bulbs.core.domain.model.preset.ValidatorPreset;

import java.util.Collection;
import java.util.Set;

/**
 *
 * @author Thomas Wendzinski
 */
public interface PresetService {

    //~ Member(s) //////////////////////////////////////////////////////////////
    //~ Method(s) //////////////////////////////////////////////////////////////
    public Preset loadById(BulbsContextUserId userId, PresetId presetId);
    public void remove(BulbsContextUserId userId, PresetId presetId);
    public Set<Preset> loadAllByUser(BulbsContextUserId userId);
    public Preset loadByName(BulbsContextUserId userId, String presetName);
    
    public Preset createNew(
            BulbsContextUserId creatorId,
            String uniquePresetName, 
            Collection<AbstractActuatorCmd> newStates,
            ValidatorPreset.NotificationHandlerPreset validationNotifier);
    public Preset createEmpty(
            BulbsContextUserId creatorId,
            String uniquePresetName, 
            ValidatorPreset.NotificationHandlerPreset validationNotifier);
    public Preset modifyName(
            BulbsContextUserId userId,
            PresetId presetId,
            String newUniquePresetname, 
            ValidatorPreset.NotificationHandlerPreset validationNotifier);
    public Preset modifyPresetStates(
            BulbsContextUserId userId, 
            PresetId presetId,
            Collection<AbstractActuatorCmd> newStates );
    

}
