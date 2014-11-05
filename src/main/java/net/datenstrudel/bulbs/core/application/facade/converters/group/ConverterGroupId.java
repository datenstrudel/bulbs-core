package net.datenstrudel.bulbs.core.application.facade.converters.group;

import net.datenstrudel.bulbs.core.application.facade.DtoConverter;
import net.datenstrudel.bulbs.core.domain.model.group.BulbGroupId;
import org.springframework.stereotype.Component;

/**
 *
 * @author Thomas Wendzinski
 */
@Component
public class ConverterGroupId implements DtoConverter<BulbGroupId, String> {

    //~ Member(s) //////////////////////////////////////////////////////////////
    //~ Construction ///////////////////////////////////////////////////////////
    public ConverterGroupId() {
    }
    
    //~ Method(s) //////////////////////////////////////////////////////////////
    @Override
    public String convert(BulbGroupId src) {
       return src.serialize();
    }
    @Override
    public BulbGroupId reverseConvert(String src) {
        return BulbGroupId.fromSerialized(src);
    }

    @Override
    public Class<String> supportedDtoClass() {
        return String.class;
    }
    @Override
    public Class<BulbGroupId> supportedDomainClass() {
        return BulbGroupId.class;
    }

    //~ Private Artifact(s) ////////////////////////////////////////////////////
}
