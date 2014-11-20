package net.datenstrudel.bulbs.core.application.facade.converters.preset;

import net.datenstrudel.bulbs.core.application.facade.DtoConverter;
import net.datenstrudel.bulbs.core.application.facade.DtoConverterRegistry;
import net.datenstrudel.bulbs.core.domain.model.bulb.AbstractActuatorCmd;
import net.datenstrudel.bulbs.core.domain.model.preset.Preset;
import net.datenstrudel.bulbs.core.domain.model.preset.PresetId;
import net.datenstrudel.bulbs.shared.domain.model.client.preset.DtoPreset;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 *
 * @author Thomas Wendzinski
 */
@Component
public class ConverterPreset implements DtoConverter<Preset, DtoPreset>{

    //~ Member(s) //////////////////////////////////////////////////////////////
    //~ Method(s) //////////////////////////////////////////////////////////////
    @Override
    public DtoPreset convert(Preset src) {
        DtoPreset res = new DtoPreset();
        res.setPresetId((String) DtoConverterRegistry.instance()
                .converterForDomain(PresetId.class).convert(src.getId())
        );
        res.setName(src.getName());
        res.setStates(
                (List) DtoConverterRegistry.instance()
                .converterForDomain(AbstractActuatorCmd.class).convertCollection(src.getStates()));
        return res;
    }
    @Override
    public Preset reverseConvert(DtoPreset src) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Class<DtoPreset> supportedDtoClass() {
        return DtoPreset.class;
    }
    @Override
    public Class<Preset> supportedDomainClass() {
        return Preset.class;
    }

    //~ Private Artifact(s) ////////////////////////////////////////////////////
}
