package net.datenstrudel.bulbs.core.infrastructure;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.datenstrudel.bulbs.core.domain.model.bulb.AbstractActuatorCmd;
import net.datenstrudel.bulbs.core.infrastructure.persistence.converters_old.json.GenericTypeConverter;
import net.datenstrudel.bulbs.shared.commonTypeConverters.TypeConverterDate;
import net.datenstrudel.bulbs.shared.domain.model.color.Color;
import net.datenstrudel.bulbs.shared.domain.model.scheduling.Trigger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Date;

/**
 *
 * @author Thomas Wendzinski
 */
@Configuration
public class SerializerConfig {

    //~ Member(s) //////////////////////////////////////////////////////////////
    //~ Construction ///////////////////////////////////////////////////////////
    //~ Method(s) //////////////////////////////////////////////////////////////
    @Bean
    public Gson gsonBulbsCore(){
        Gson gson = new GsonBuilder()
            .registerTypeAdapter(Color.class, new GenericTypeConverter())
            .registerTypeAdapter(AbstractActuatorCmd.class, new GenericTypeConverter())
            .registerTypeAdapter(Date.class, new TypeConverterDate())
            .registerTypeAdapter(Trigger.class, new GenericTypeConverter()) //TODO:  register correct Converter!
            .create();
        
        return gson;
    }
    //~ Private Artifact(s) ////////////////////////////////////////////////////

}
