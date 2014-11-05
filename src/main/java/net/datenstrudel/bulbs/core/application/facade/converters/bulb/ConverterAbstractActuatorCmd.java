package net.datenstrudel.bulbs.core.application.facade.converters.bulb;

import net.datenstrudel.bulbs.core.application.facade.DtoConverter;
import net.datenstrudel.bulbs.core.application.facade.DtoConverterRegistry;
import net.datenstrudel.bulbs.core.domain.model.bulb.AbstractActuatorCmd;
import net.datenstrudel.bulbs.shared.domain.model.client.bulb.DtoAbstractActuatorCmd;
import org.springframework.stereotype.Component;

/**
 * 
 * @author Thomas Wendzinski
 */
@Component
public class ConverterAbstractActuatorCmd 
        implements DtoConverter<AbstractActuatorCmd, DtoAbstractActuatorCmd>{
    
    //~ Member(s) //////////////////////////////////////////////////////////////

    //~ Construction ///////////////////////////////////////////////////////////
    public ConverterAbstractActuatorCmd() {
    }

    //~ Method(s) //////////////////////////////////////////////////////////////
    @Override
    public DtoAbstractActuatorCmd convert(AbstractActuatorCmd src) {
        DtoConverter<AbstractActuatorCmd, DtoAbstractActuatorCmd> conv = 
                (DtoConverter) DtoConverterRegistry.instance()
                        .converterForDomain(src.getClass());
        return conv.convert(src);
    }
    @Override
    public AbstractActuatorCmd reverseConvert(DtoAbstractActuatorCmd src) {
        DtoConverter<AbstractActuatorCmd, DtoAbstractActuatorCmd> conv = 
                (DtoConverter) DtoConverterRegistry.instance()
                        .converterFor(src.getClass());
        return conv.reverseConvert(src);
    }

    @Override
    public Class<DtoAbstractActuatorCmd> supportedDtoClass() {
        return DtoAbstractActuatorCmd.class;
    }
    @Override
    public Class<AbstractActuatorCmd> supportedDomainClass() {
        return AbstractActuatorCmd.class;
    }

}
