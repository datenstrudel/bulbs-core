package net.datenstrudel.bulbs.core.infrastructure.persistence;

import net.datenstrudel.bulbs.core.IntegrationTest;
import net.datenstrudel.bulbs.core.TestConfig;
import net.datenstrudel.bulbs.core.domain.model.bulb.BulbActuatorCommand;
import net.datenstrudel.bulbs.core.domain.model.bulb.BulbBridgeId;
import net.datenstrudel.bulbs.core.domain.model.bulb.BulbId;
import net.datenstrudel.bulbs.core.domain.model.group.BulbGroupId;
import net.datenstrudel.bulbs.core.domain.model.group.GroupActuatorCommand;
import net.datenstrudel.bulbs.core.domain.model.identity.AppIdCore;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUserId;
import net.datenstrudel.bulbs.core.domain.model.preset.Preset;
import net.datenstrudel.bulbs.core.domain.model.preset.PresetId;
import net.datenstrudel.bulbs.core.infrastructure.PersistenceConfig;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbState;
import net.datenstrudel.bulbs.shared.domain.model.bulb.CommandPriority;
import net.datenstrudel.bulbs.shared.domain.model.color.ColorRGB;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 *
 * @author Thomas Wendzinski
 */
@ContextConfiguration(
    initializers = TestConfig.class,
    classes = {
        PersistenceConfig.class,
        TestConfig.class
    })
@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class PresetRepositoryImplTest {
    
    private static final Logger log = LoggerFactory.getLogger(PresetRepositoryImplTest.class);
    
    @Autowired
    PresetRepositoryImpl instance;
    @Autowired
    private MongoTemplate mongo;
    private static boolean initialized = false;
    
    public PresetRepositoryImplTest() {
    }
    
    @Before
    public void setUp() {
        if(PresetRepositoryImplTest.initialized) return ;
        PresetRepositoryImplTest.initialized = true;
        mongo.dropCollection(Preset.class);
    }

    @Test
    public void testNextIdentity() {
        System.out.println("nextIdentity");
        BulbsContextUserId userId = new BulbsContextUserId("testPresets__userUuid");
        PresetId result = instance.nextIdentity(userId);
        assertEquals(userId, result.getUserId());
        assertNotNull(result.getPresetUuid());
    }
    @Test
    public void testLoadById() {
        System.out.println("loadById");
        Preset expResult = newTestPreset();
        PresetId presetId = expResult.getPresetId();
        
        instance.store(expResult);
        Preset result = instance.loadById(presetId);
        assertEquals(expResult, result);
        assertEquals(expResult.getName(), result.getName());
        assertEquals(expResult.getStates(), result.getStates());
        expResult.getStates().get(0).equals(result.getStates().get(0));
    }
    @Test
    public void testLoadByName() {
        System.out.println("loadByName");
        Preset expResult = newTestPreset();
        Preset unExpResult = newTestPreset();
        BulbsContextUserId userId = expResult.getPresetId().getUserId();
        String presetName = expResult.getName();

        instance.store(expResult);
        instance.store(unExpResult);
        Preset result = instance.loadByName(userId, presetName);
        
        assertEquals(expResult, result);
    }
    @Test
    public void testLoadByOwner() {
        System.out.println("loadByOwner");
        Preset xp1 = newTestPreset();
        Preset xp2 = newTestPreset();
        ReflectionTestUtils.setField(
                xp2.getPresetId().getUserId(), 
                "uuid", 
                xp1.getPresetId().getUserId().getUuid());
        Preset uxp1 = newTestPreset();
        Set<Preset> expResult = new HashSet<>();
        expResult.add(xp1);
        expResult.add(xp2);
        BulbsContextUserId userId = xp1.getPresetId().getUserId();
        
        instance.store(uxp1);
        instance.store(xp1);
        instance.store(xp2);
        Set<Preset> result = instance.loadByOwner(userId);
        assertEquals(expResult, result);
    }
    @Test
    public void testRemove() {
        System.out.println("remove");
        
        Preset o1 = newTestPreset();
        Preset o2 = newTestPreset();
        PresetId presetId = o1.getPresetId();
        
        instance.store(o1);
        instance.store(o2);
        
        instance.remove(presetId);
        
        assertNotNull(instance.loadById(o2.getPresetId()));
    }
//    @Test
    public void testStore() {
        // see testloadById() 
    }
    
    private Preset newTestPreset(){
        PresetId presetId = instance.nextIdentity(
                new BulbsContextUserId("testPresets__testUserUuid" + UUID.randomUUID().toString()));
        Preset res = new Preset(presetId, "testPresets_TestName__" + (Math.random() * 100 )/ (int)100);
        res.addState(
                new BulbActuatorCommand(
                        new BulbId(
                                new BulbBridgeId("testPresets__bridgeUuid"), 0), 
                        AppIdCore.instance(), 
                        "testPresets__userApiKey", 
                        CommandPriority.override(), 
                        new ArrayList<BulbState>(){{
                            add(new BulbState(new ColorRGB(42, 42, 42), true));
                        }})
        );
        res.addState(
                new GroupActuatorCommand(
                        new BulbGroupId(new BulbsContextUserId("testPresets_groupUserId"), "testPresetes__groupId"), 
                        AppIdCore.instance(), 
                        "testPresets__userApiKey", 
                        CommandPriority.override(), 
                        new ArrayList<BulbState>(){{
                            add(new BulbState(new ColorRGB(17, 17, 17), true));
                        }},
                        false)
        );
        return res;
    }
    
}
