package net.datenstrudel.bulbs.core.infrastructure.persistence.repository;

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
import net.datenstrudel.bulbs.core.domain.model.preset.PresetRepository;
import net.datenstrudel.bulbs.core.infrastructure.PersistenceConfig;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbState;
import net.datenstrudel.bulbs.shared.domain.model.bulb.CommandPriority;
import net.datenstrudel.bulbs.shared.domain.model.color.ColorRGB;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
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
public class PresetRepositoryImplIT {
    
    private static final Logger log = LoggerFactory.getLogger(PresetRepositoryImplIT.class);
    
    @Autowired
    PresetRepository instance;
    @Autowired
    private MongoTemplate mongo;
    private static boolean initialized = false;
    
    public PresetRepositoryImplIT() {
    }
    
    @Before
    public void setUp() {
        if(PresetRepositoryImplIT.initialized) return ;
        PresetRepositoryImplIT.initialized = true;
        mongo.dropCollection(Preset.class);
    }

    @Test
    public void testNextIdentity() {
        System.out.println("nextIdentity");
        BulbsContextUserId userId = new BulbsContextUserId("testPresets__userUuid");
        PresetId result = instance.nextIdentity(userId);
        assertEquals(userId, result.getCreator());
        assertNotNull(result.getPresetUuid());
    }
    @Test
    public void findOne() {
        System.out.println("findOne");
        Preset expResult = newTestPreset();
        PresetId presetId = expResult.getId();
        
        instance.save(expResult);
        Preset result = instance.findOne(presetId);
        assertEquals(expResult, result);
        assertEquals(expResult.getName(), result.getName());
        assertEquals(expResult.getStates(), result.getStates());
        expResult.getStates().get(0).equals(result.getStates().get(0));
    }
    @Test
    public void findByNameAndPresetId_Creator() {
        System.out.println("findByName");
        instance.deleteAll();
        Preset expResult = newTestPreset();
        Preset unExpResult = newTestPreset();
        BulbsContextUserId userId = expResult.getId().getCreator();
        String presetName = expResult.getName();
        instance.save(expResult);
        instance.save(unExpResult);
        assertThat(instance.findAll().size(), Matchers.greaterThan(1));
        Preset result = instance.findByNameAndId_Creator(presetName, userId);
        
        assertEquals(expResult, result);
    }
    @Test
    public void findByPresetId_Creator() {
        System.out.println("findById_Creator");
        Preset xp1 = newTestPreset();
        Preset xp2 = newTestPreset();
        ReflectionTestUtils.setField(
                xp2.getId().getCreator(),
                "uuid", 
                xp1.getId().getCreator().getUuid());
        Preset uxp1 = newTestPreset();
        Set<Preset> expResult = new HashSet<>();
        expResult.add(xp1);
        expResult.add(xp2);
        BulbsContextUserId userId = xp1.getId().getCreator();
        
        instance.save(uxp1);
        instance.save(xp1);
        instance.save(xp2);
        Set<Preset> result = instance.findById_Creator(userId);
        assertEquals(expResult, result);
    }
    @Test
    public void testRemove() {
        System.out.println("delete");
        
        Preset o1 = newTestPreset();
        Preset o2 = newTestPreset();
        PresetId presetId = o1.getId();
        
        instance.save(o1);
        instance.save(o2);
        
        instance.delete(presetId);
        
        assertNotNull(instance.findOne(o2.getId()));
    }

    private Preset newTestPreset(){
        PresetId presetId = instance.nextIdentity(
                new BulbsContextUserId("testPresets__testUserUuid" + UUID.randomUUID().toString()));
        Preset res = new Preset(presetId, "testPresets_TestName__" + (Math.random() * 100 )/ (int)100);
        res.addState(
                new BulbActuatorCommand(
                        new BulbId(
                                new BulbBridgeId("testPresets__bridgeUuid"), "0"),
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
