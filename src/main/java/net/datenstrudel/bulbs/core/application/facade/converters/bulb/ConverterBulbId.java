package net.datenstrudel.bulbs.core.application.facade.converters.bulb;

import net.datenstrudel.bulbs.core.application.facade.DtoConverter;
import net.datenstrudel.bulbs.core.domain.model.bulb.BulbId;
import org.springframework.stereotype.Component;

/**
 *
 * @author Thomas Wendzinski
 */
@Component
public class ConverterBulbId implements DtoConverter<BulbId, String>{

    //~ Member(s) //////////////////////////////////////////////////////////////
    //~ Construction ///////////////////////////////////////////////////////////
    //~ Method(s) //////////////////////////////////////////////////////////////
    @Override
    public String convert(BulbId src) {
        return src.serialize();
    }
    @Override
    public BulbId reverseConvert(String in){
        return BulbId.fromSerialized(in);
    }
    
    
    //~ Private Artifact(s) ////////////////////////////////////////////////////
    @Override
    public Class<String> supportedDtoClass() {
        return String.class;
    }
    @Override
    public Class<BulbId> supportedDomainClass() {
        return BulbId.class;
    }

}
