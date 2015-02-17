package net.datenstrudel.bulbs.core.application.facade;

import net.datenstrudel.bulbs.core.domain.model.bulb.Bulb;
import net.datenstrudel.bulbs.core.domain.model.bulb.BulbBridge;
import net.datenstrudel.bulbs.core.domain.model.bulb.BulbBridgeId;
import net.datenstrudel.bulbs.core.domain.model.bulb.BulbId;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUserId;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbBridgeAddress;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbState;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbsPlatform;
import net.datenstrudel.bulbs.shared.domain.model.color.ColorHSB;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static org.easymock.classextension.EasyMock.*;
import static org.junit.Assert.assertEquals;

/**
 *
 * @author Thomas Wendzinski
 */
public class ModelFacadeOutPortTest {
    
    ModelFacadeOutPort instance = new ModelFacadeOutPort();
    public ModelFacadeOutPortTest() {
    }
    
    private DtoConverterRegistry convReg_mk;
    
    @Before
    public void before(){
        convReg_mk = createMock(DtoConverterRegistry.class);
        ReflectionTestUtils.setField(instance, "converterFactory", convReg_mk);
    }
    
    @Test
    public void testWrite() {
        System.out.println("write");
        Integer domainObject = 1;
        
        
        expect(convReg_mk.convertersFor((Class[]) anyObject())).andReturn(
                new HashSet<DtoConverter>(){{ add(new TestDtoConverter()); }} ).anyTimes();
        replay(convReg_mk);
        instance.registerConverterFor(Integer.class);
        instance.write(domainObject);
        instance.outputAs(Integer.class);
        assertEquals(domainObject, instance.outputAs(Integer.class) );
    }
    @Test
    public void testWriteColl() {
        System.out.println("write");
        List<Integer> domainObject = new ArrayList<>();
        domainObject.add(1);
        domainObject.add(2);
        
        expect(convReg_mk.convertersFor((Class[]) anyObject())).andReturn(
                new HashSet<DtoConverter>(){{ add(new TestDtoConverter()); }}  ).anyTimes();
        
        replay(convReg_mk);
        instance.registerConverterFor(Integer.class);
        instance.write(domainObject);
        assertEquals(domainObject, instance.outputAsList(Integer.class));
    }
    
    
    private static final class TestDtoConverter implements DtoConverter<Object, Integer>{
        int invocation = 0;
        @Override
        public Integer convert(Object src) {
            return (int)src;
        }
        @Override
        public Integer reverseConvert(Integer src) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
        @Override
        public Class supportedDtoClass() {
            return Integer.class;
        }
        @Override
        public Class supportedDomainClass() {
            return Integer.class;
        }
    }
    
    private Bulb domainObjectInstance(){
        BulbBridgeId bridgeId = new BulbBridgeId("testBulbRdigeUUId");
        Bulb domainObject = new Bulb(
                new BulbId(bridgeId, "1"),
                BulbsPlatform._EMULATED, 
                "testBulb", 
                new BulbBridge(bridgeId, 
                    "testMAc", BulbsPlatform._EMULATED, 
                    new BulbBridgeAddress("blub", 123), "testName",  
                new BulbsContextUserId("testUserUUID"), new HashMap<String, Object>()),
                new BulbState(new ColorHSB(1f, 1f, 1f), true), true, new HashMap<String, Object>() );
        return domainObject;
    }
    

}