package net.datenstrudel.bulbs.core.domain.model.preset;

import net.datenstrudel.bulbs.core.domain.model.preset.ValidatorPreset.NotificationHandlerPreset;
import net.datenstrudel.bulbs.shared.domain.validation.ValidationException;
import net.datenstrudel.bulbs.shared.domain.validation.Validator;
public class ValidatorPreset extends Validator<NotificationHandlerPreset> {

    //~ Member(s) //////////////////////////////////////////////////////////////
    private final Preset preset2Validate;
    private final PresetRepository presetRepository;
    
    //~ Construction ///////////////////////////////////////////////////////////
    public ValidatorPreset(NotificationHandlerPreset notificationHandler,
            Preset preset2Validate,
            PresetRepository presetRepository
            ) {
        super(notificationHandler);
        this.preset2Validate = preset2Validate;
        this.presetRepository = presetRepository;
    }
    
    //~ Method(s) //////////////////////////////////////////////////////////////
    @Override
    public void validateNew() {
        Preset preset = presetRepository.findByNameAndId_Creator(
                preset2Validate.getName(), preset2Validate.getId().getCreator());
        if(preset != null){
            notificationHandler().handleDuplicatePresetName();
            throw new ValidationException("Preset with given name already exists!");
        }
    }
    @Override
    public void validateExisting() {
        Preset grp = presetRepository.findByNameAndId_Creator(
                preset2Validate.getName(), preset2Validate.getId().getCreator());
        if(grp != null && !grp.sameIdentityAs(preset2Validate)){
            notificationHandler().handleDuplicatePresetName();
            throw new ValidationException("Preset with given name already exists!");
        }
    }
    
    //~ Private Artifact(s) ////////////////////////////////////////////////////
    //~ Additional Artifact(s) ////////////////////////////////////////////////////
    public static interface NotificationHandlerPreset extends Validator.ValidationNotificationHandler{
        void handleDuplicatePresetName();
    }

}
