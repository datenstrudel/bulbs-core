package net.datenstrudel.bulbs.core.application.facade.converters.preset;

import net.datenstrudel.bulbs.core.application.facade.DtoConverter;
import net.datenstrudel.bulbs.core.application.facade.DtoConverterRegistry;
import net.datenstrudel.bulbs.core.domain.model.preset.PresetActuatorCommand;
import net.datenstrudel.bulbs.core.domain.model.preset.PresetId;
import net.datenstrudel.bulbs.shared.domain.model.client.preset.DtoPresetActuatorCmd;
import net.datenstrudel.bulbs.shared.domain.model.identity.AppId;
import org.springframework.stereotype.Component;

/**
 *
 * @author Thomas Wendzinski
 */
@Component
public class  ConverterPresetActuatorCmd
        implements DtoConverter<PresetActuatorCommand, DtoPresetActuatorCmd>{

    //~ Member(s) //////////////////////////////////////////////////////////////
    //~ Construction ///////////////////////////////////////////////////////////
    //~ Method(s) //////////////////////////////////////////////////////////////
    @Override
    public DtoPresetActuatorCmd convert(PresetActuatorCommand src) {
        DtoPresetActuatorCmd res = new DtoPresetActuatorCmd();
        res.setAppId(src.getAppId().getUniqueAppName());
        res.setPresetId((String) DtoConverterRegistry.instance().converterForDomain(
                PresetId.class).convert(src.getPresetId()));
        res.setPriority(src.getPriority());
        res.setStates(src.getStates());
        res.setLoop(src.isLoop());
        return res;
    }
    @Override
    public PresetActuatorCommand reverseConvert(DtoPresetActuatorCmd in) {
        PresetActuatorCommand res = new PresetActuatorCommand(
                new AppId(in.getAppId()),
                null,
                in.getPriority(), 
                DtoConverterRegistry.instance().converterForDomain(PresetId.class).reverseConvert(in.getPresetId()),
                in.isLoop()
        );
        return res;
    }

    @Override
    public Class<DtoPresetActuatorCmd> supportedDtoClass() {
        return DtoPresetActuatorCmd.class;
    }
    @Override
    public Class<PresetActuatorCommand> supportedDomainClass() {
        return PresetActuatorCommand.class;
    }
    

}
