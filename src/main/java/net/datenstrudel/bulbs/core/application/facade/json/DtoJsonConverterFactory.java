package net.datenstrudel.bulbs.core.application.facade.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.datenstrudel.bulbs.shared.commonTypeConverters.TypeConverterDate;
import net.datenstrudel.bulbs.shared.commonTypeConverters.TypeHierarchyAdapterColor;
import net.datenstrudel.bulbs.shared.commonTypeConverters.TypeHierarchyAdapterSimpleClassname;
import net.datenstrudel.bulbs.shared.domain.model.client.bulb.DtoAbstractActuatorCmd;
import net.datenstrudel.bulbs.shared.domain.model.color.Color;
import net.datenstrudel.bulbs.shared.domain.model.scheduling.DaysOfWeekTrigger;
import net.datenstrudel.bulbs.shared.domain.model.scheduling.IntervalTrigger;
import net.datenstrudel.bulbs.shared.domain.model.scheduling.PointInTimeTrigger;
import net.datenstrudel.bulbs.shared.domain.model.scheduling.Trigger;

import java.util.Date;

/**
 *
 * @author Thomas Wendzinski
 */
public class DtoJsonConverterFactory {

    //~ Member(s) //////////////////////////////////////////////////////////////
    private static final Gson DTO_JSON_CONVERTER = new GsonBuilder()
            .registerTypeAdapter(Color.class, new TypeHierarchyAdapterColor())
            .registerTypeAdapter(Date.class, new TypeConverterDate())
            .registerTypeAdapter(DtoAbstractActuatorCmd.class, new TypeHierarchyAdapterDtoActuatorCmd())
            .registerTypeAdapter(Trigger.class, new TypeHierarchyAdapterSimpleClassname(
                    PointInTimeTrigger.class,
                    IntervalTrigger.class,
                    DaysOfWeekTrigger.class
            ))
            .create();
//    private static final TypeToken TYPE_TK__MAP = new TypeToken<Map<String, Object>>(){};

    //~ Construction ///////////////////////////////////////////////////////////
    //~ Method(s) //////////////////////////////////////////////////////////////
    public static Gson dtoJsonConverter(){
        return DTO_JSON_CONVERTER;
    }

    //~ Private Artifact(s) ////////////////////////////////////////////////////

}
