package net.datenstrudel.bulbs.core.application.services;

import net.datenstrudel.bulbs.core.application.facade.ModelFacadeOutPort;
import net.datenstrudel.bulbs.core.domain.model.bulb.AbstractActuatorCmd;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUserId;
import net.datenstrudel.bulbs.core.domain.model.messaging.IllegalArgumentExceptionNotRepeatable;
import net.datenstrudel.bulbs.core.domain.model.preset.Preset;
import net.datenstrudel.bulbs.core.domain.model.preset.PresetId;
import net.datenstrudel.bulbs.core.domain.model.preset.PresetRepository;
import net.datenstrudel.bulbs.core.domain.model.preset.ValidatorPreset;
import net.datenstrudel.bulbs.core.web.controller.util.NotAuthorizedException;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;

/**
 *
 * @author Thomas Wendzinski
 */
@Service
public class PresetServiceImpl implements PresetService {

    //~ Member(s) //////////////////////////////////////////////////////////////
    private static final Logger log = LoggerFactory.getLogger(PresetServiceImpl.class);
    private final PresetRepository presetRepository;
    private final ModelFacadeOutPort outPort;

    //~ Construction ///////////////////////////////////////////////////////////
    @Autowired
    public PresetServiceImpl(
            PresetRepository presetRepository, 
            ModelFacadeOutPort outPort) {
        this.presetRepository = presetRepository;
        this.outPort = outPort;
    }
    
    //~ Method(s) //////////////////////////////////////////////////////////////
    @Override
    public Preset loadById(BulbsContextUserId userId, PresetId presetId) {
        Preset res = presetRepository.findOne(presetId);
        outPort.write(res);
        return res;
    }
    @Override
    public void remove(BulbsContextUserId userId, PresetId presetId) {
        Preset res = presetRepository.findOne(presetId);
        if(res == null)throw new IllegalArgumentExceptionNotRepeatable("Preset["
                +presetId+"] for deletion doesn't exist!");
        log.info("Deleting preset by ID: " + presetId);
        presetRepository.delete(presetId);
//        DomainEventPublisher.instance().publish(new PresetDeleted(presetId));
    }
    @Override
    public Set<Preset> loadAllByUser(BulbsContextUserId userId) {
        Set<Preset> res = presetRepository.findById_Creator(userId);
        outPort.write(res);
        return res;
    }
    @Override
    public Preset loadByName(BulbsContextUserId userId, String presetName) {
        Preset res = presetRepository.findByNameAndId_Creator(presetName, userId);
        if(!res.getId().getCreator().sameValueAs(userId)){
            throw new NotAuthorizedException("Illegal access to not posessing preset!");
        }
        outPort.write(res);
        return res;
    }
    
    @Override
    public Preset createEmpty(
            BulbsContextUserId creatorId,
            String uniquePresetName, 
            ValidatorPreset.NotificationHandlerPreset validationNotifier) {
        log.info("Creating empty new Preset '"+uniquePresetName+"' by user " + creatorId);
        Preset res = new Preset(presetRepository.nextIdentity(creatorId), uniquePresetName);
        res.validateNew(validationNotifier, presetRepository);
        presetRepository.save(res);
        outPort.write(res);
//        DomainEventPublisher.instance().publish(new PresetCreated(res.getId()));
        return res;
    }
    @Override
    public Preset createNew(
            BulbsContextUserId creatorId,
            String uniquePresetName, 
            Collection<AbstractActuatorCmd> states,
            ValidatorPreset.NotificationHandlerPreset validationNotifier) {
        log.info("Creating new Preset '"+uniquePresetName+"' by user " + creatorId);
        Preset res = new Preset(presetRepository.nextIdentity(creatorId), uniquePresetName);
        res.validateNew(validationNotifier, presetRepository);
        res.addStates(states);
        presetRepository.save(res);
        outPort.write(res);
//        DomainEventPublisher.instance().publish(new PresetCreated(res.getId()));
        return res;
    }
    @Override
    public Preset modifyName(
            BulbsContextUserId userId,
            PresetId presetId,
            String newUniquePresetName, 
            ValidatorPreset.NotificationHandlerPreset validationNotifier) {
        
        Preset preset = presetRepository.findOne(presetId);
        if(!preset.getId().getCreator().sameValueAs(userId)){
            throw new NotAuthorizedException("Illegal attempt to modify preset's name!");
        }
        preset.modifyName(newUniquePresetName, validationNotifier, presetRepository);
        presetRepository.save(preset);
        outPort.write(preset);
        return preset;
    }
    @Override
    public Preset modifyPresetStates(
            BulbsContextUserId userId, 
            PresetId presetId, 
            Collection<AbstractActuatorCmd> newStates) {
        Preset preset = presetRepository.findOne(presetId);
        if(!preset.getId().getCreator().sameValueAs(userId)){
            throw new NotAuthorizedException("Illegal attempt to modify preset!");
        }
        log.info("Replacing states of Preset[" + presetId + "]: " + newStates);
        preset.replaceStates(newStates);
        presetRepository.save(preset);
        outPort.write(preset);
        return preset;
    }
    //~ Private Artifact(s) ////////////////////////////////////////////////////

}
