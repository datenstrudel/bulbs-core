package net.datenstrudel.bulbs.core.web.controller;

import com.google.common.collect.Lists;
import com.wordnik.swagger.annotations.Api;
import net.datenstrudel.bulbs.core.application.facade.DtoConverterRegistry;
import net.datenstrudel.bulbs.core.application.facade.ModelFacadeOutPort;
import net.datenstrudel.bulbs.core.application.services.PresetService;
import net.datenstrudel.bulbs.core.domain.model.bulb.AbstractActuatorCmd;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUser;
import net.datenstrudel.bulbs.core.domain.model.preset.PresetId;
import net.datenstrudel.bulbs.core.domain.model.preset.ValidatorPreset;
import net.datenstrudel.bulbs.shared.domain.model.client.bulb.DtoAbstractActuatorCmd;
import net.datenstrudel.bulbs.shared.domain.model.client.common.DtoSingleValue;
import net.datenstrudel.bulbs.shared.domain.model.client.preset.DtoPreset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Thomas Wendzinski
 */
@RestController
@RequestMapping(value = "/core/",
        produces = MediaType.APPLICATION_JSON_VALUE)
@Api(value = "presets", description = "contain bulbs' and/or groups' states, ~transitions", position = 40)
public class PresetCtrl {

    //~ Member(s) //////////////////////////////////////////////////////////////
    @Autowired 
    private ModelFacadeOutPort modelPort;
    @Autowired
    private DtoConverterRegistry converterFactory;
    @Autowired
    private PresetService presetService;
    
    //~ Construction ///////////////////////////////////////////////////////////
    //~ Method(s) //////////////////////////////////////////////////////////////
    @RequestMapping(method = RequestMethod.GET, value="presets/{presetId}")
    public DtoPreset loadById(
            @PathVariable("presetId") String _presetId,
            Authentication authentication
    ){
        BulbsContextUser principal =  ((BulbsContextUser)authentication.getPrincipal());
        PresetId presetId = PresetId.fromSerialized(_presetId);
        
        modelPort.registerConverterFor(DtoPreset.class);
        presetService.loadById(principal.getBulbsContextUserlId(), presetId);
        return modelPort.outputAs(DtoPreset.class);
    }

    /**
     * Retrieve all presets of the principal authenticated.
     * @param authentication
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value="presets")
    public Set<DtoPreset> presetsByUser(
            Authentication authentication
    ){
        
        BulbsContextUser principal =  ((BulbsContextUser)authentication.getPrincipal());
        modelPort.registerConverterFor(DtoPreset.class);
        presetService.loadAllByUser(principal.getBulbsContextUserlId());
        return modelPort.outputAsSet(DtoPreset.class);
    }

    @RequestMapping(method = RequestMethod.POST, value="presets")
    public DtoPreset create(
            final @Valid @RequestBody DtoPreset dtoNewPreset,
            Authentication authentication
    ){
        BulbsContextUser principal =  ((BulbsContextUser)authentication.getPrincipal());
        
        modelPort.registerConverterFor(DtoPreset.class);
        presetService.createNew(
                principal.getBulbsContextUserlId(), 
                dtoNewPreset.getName(), 
                (converterFactory.converterForDomain(AbstractActuatorCmd.class)
                        .reverseConvertCollection( (Collection)dtoNewPreset.getStates() )),
                new ValidatorPreset.NotificationHandlerPreset() {
            @Override
            public void handleDuplicatePresetName() {
                throw new IllegalArgumentException("Preset name "+dtoNewPreset.getName()+" already in use!");
            }
        });
        return modelPort.outputAs(DtoPreset.class);
    }
    
    @RequestMapping(method = RequestMethod.PUT, value="presets/{presetId}/states")
    public DtoPreset modifyPresetStates(
            @PathVariable("presetId") String _presetId,
            final @RequestBody DtoAbstractActuatorCmd[] _states,
            Authentication authentication
    ){
        BulbsContextUser principal =  ((BulbsContextUser)authentication.getPrincipal());
        PresetId presetId = PresetId.fromSerialized(_presetId);
        List<DtoAbstractActuatorCmd> states = Lists.newArrayList(_states);
        
        modelPort.registerConverterFor(DtoPreset.class);
        presetService.modifyPresetStates(
                principal.getBulbsContextUserlId(), presetId, 
                converterFactory.converterForDomain(AbstractActuatorCmd.class).reverseConvertCollection((Collection)states)
                        );
        return modelPort.outputAs(DtoPreset.class);
    }
    
    @RequestMapping(method = RequestMethod.PUT, value="presets/{presetId}/name")
    public DtoPreset modifyPresetName(
            @PathVariable("presetId") String _presetId,
            final @RequestBody DtoSingleValue input,
            Authentication authentication
    ){
        String newName = input.getValue();

        BulbsContextUser principal =  ((BulbsContextUser)authentication.getPrincipal());
        PresetId presetId = PresetId.fromSerialized(_presetId);
        
        modelPort.registerConverterFor(DtoPreset.class);
        presetService.modifyName(
                principal.getBulbsContextUserlId(), 
                presetId, 
                newName,
                new ValidatorPreset.NotificationHandlerPreset() {
                    @Override
                    public void handleDuplicatePresetName() {
                        throw new IllegalArgumentException("Preset name " 
                                + newName + " already in use!");
                    } 
                }
        );
        return modelPort.outputAs(DtoPreset.class);
    }

    @RequestMapping(method = RequestMethod.DELETE, value="presets/{presetId}")
    public void deletePreset(
            @PathVariable("presetId") String _presetId,
            Authentication authentication
    ){
        BulbsContextUser principal =  ((BulbsContextUser)authentication.getPrincipal());
        PresetId presetId = PresetId.fromSerialized(_presetId);
        
        presetService.remove(principal.getBulbsContextUserlId(), presetId);
    }

    //~ Private Artifact(s) ////////////////////////////////////////////////////
}
