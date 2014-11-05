package net.datenstrudel.bulbs.core.application.facade.converters.preset;

import net.datenstrudel.bulbs.core.application.facade.DtoConverter;
import net.datenstrudel.bulbs.core.domain.model.preset.PresetId;
import org.springframework.stereotype.Component;

/**
 *
 * @author Thomas Wendzinski
 */
@Component
public class ConverterPresetId implements DtoConverter<PresetId, String>{

    //~ Member(s) //////////////////////////////////////////////////////////////
    //~ Construction ///////////////////////////////////////////////////////////
    public ConverterPresetId() {
    }

    //~ Method(s) //////////////////////////////////////////////////////////////
    @Override
    public String convert(PresetId src) {
        return src.serialize();
    }
    @Override
    public PresetId reverseConvert(String src) {
        return PresetId.fromSerialized(src);
    }

    @Override
    public Class<String> supportedDtoClass() {
        return String.class;
    }
    @Override
    public Class<PresetId> supportedDomainClass() {
        return PresetId.class;
    }

    //~ Private Artifact(s) ////////////////////////////////////////////////////
}
