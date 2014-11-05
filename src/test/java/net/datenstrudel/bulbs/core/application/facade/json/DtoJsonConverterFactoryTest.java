/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.datenstrudel.bulbs.core.application.facade.json;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.datenstrudel.bulbs.core.domain.model.identity.AppIdCore;
import net.datenstrudel.bulbs.shared.domain.model.client.bulb.DtoAbstractActuatorCmd;
import net.datenstrudel.bulbs.shared.domain.model.client.bulb.DtoBulbActuatorCmd;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author Thomas Wendzinski
 */
public class DtoJsonConverterFactoryTest {
    
    private Gson instance = DtoJsonConverterFactory.dtoJsonConverter();
    
    public DtoJsonConverterFactoryTest() {
    }
    
    @Before
    public void setUp() {
    }

    @Test
    public void testDtoJsonConverter() {
        System.out.println("dtoJsonConverter");
        Set expRes = new HashSet<>();
        DtoBulbActuatorCmd obj = new DtoBulbActuatorCmd();
        obj.setAppId(AppIdCore.instance().getUniqueAppName());
        expRes.add(obj);
        
        String json = instance.toJson(expRes);
        Set<DtoAbstractActuatorCmd> res = (Set) instance.fromJson(json, new TypeToken<Set<DtoAbstractActuatorCmd>>(){}.getType());
        assertEquals(expRes, res);
       
    }
    
}
