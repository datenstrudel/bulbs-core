package net.datenstrudel.bulbs.core.application.facade.converters.scheduling;

import net.datenstrudel.bulbs.core.application.facade.DtoConverter;
import net.datenstrudel.bulbs.core.domain.model.scheduling.ScheduledActuationId;
import org.springframework.stereotype.Component;

/**
 *
 * @author Thomas Wendzinski
 */
@Component
public class ConverterScheduledActuationId implements DtoConverter<ScheduledActuationId, String>{

    //~ Member(s) //////////////////////////////////////////////////////////////
    //~ Construction ///////////////////////////////////////////////////////////
    //~ Method(s) //////////////////////////////////////////////////////////////
    @Override
    public String convert(ScheduledActuationId src) {
        return src.serialize();
    }
    @Override
    public ScheduledActuationId reverseConvert(String src) {
        return ScheduledActuationId.fromSerialized(src);
    }

    @Override
    public Class<String> supportedDtoClass() {
        return String.class;
    }
    @Override
    public Class<ScheduledActuationId> supportedDomainClass() {
        return ScheduledActuationId.class;
    }

    //~ Private Artifact(s) ////////////////////////////////////////////////////
}
