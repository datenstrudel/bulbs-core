package net.datenstrudel.bulbs.core.infrastructure.persistence.repository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.datenstrudel.bulbs.core.TestConfig;
import net.datenstrudel.bulbs.core.application.ApplicationLayerConfig;
import net.datenstrudel.bulbs.core.config.BulbsCoreConfig;
import net.datenstrudel.bulbs.core.security.config.SecurityConfig;
import net.datenstrudel.bulbs.core.testConfigs.ScheduledActuationIntegrationTestConfig;
import net.datenstrudel.bulbs.core.web.config.SwaggerConfig;
import net.datenstrudel.bulbs.core.web.config.WebConfig;
import net.datenstrudel.bulbs.core.websocket.WebSocketConfig;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;

import net.datenstrudel.bulbs.core.domain.model.bulb.AbstractActuatorCmd;
import net.datenstrudel.bulbs.core.domain.model.bulb.BulbActuatorCommand;
import net.datenstrudel.bulbs.core.domain.model.bulb.BulbBridgeId;
import net.datenstrudel.bulbs.core.domain.model.bulb.BulbId;
import net.datenstrudel.bulbs.core.domain.model.identity.AppIdCore;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUserId;
import net.datenstrudel.bulbs.core.domain.model.preset.PresetActuatorCommand;
import net.datenstrudel.bulbs.core.domain.model.preset.PresetId;
import net.datenstrudel.bulbs.core.domain.model.scheduling.JobCoordinator;
import net.datenstrudel.bulbs.core.domain.model.scheduling.ScheduledActuation;
import net.datenstrudel.bulbs.core.domain.model.scheduling.ScheduledActuationId;
import net.datenstrudel.bulbs.core.domain.model.scheduling.ScheduledActuationRepository;
import net.datenstrudel.bulbs.core.infrastructure.PersistenceConfig;
import net.datenstrudel.bulbs.core.infrastructure.services.InfrastructureServicesConfig;
import net.datenstrudel.bulbs.core.testConfigs.InfrastructureServicesTestConfig;
import net.datenstrudel.bulbs.shared.domain.model.bulb.CommandPriority;
import net.datenstrudel.bulbs.shared.domain.model.scheduling.PointInTimeTrigger;

/**
 *
 * @author Thomas Wendzinski
 */
@ContextConfiguration(
    initializers = TestConfig.class,
    classes = {
        TestConfig.class,
        PersistenceConfig.class,
        BulbsCoreConfig.class,
        InfrastructureServicesConfig.class,
        ScheduledActuationIntegrationTestConfig.class
    }
)
@RunWith(SpringRunner.class)
public class ScheduledActuationRepositoryImplIT {
    
    @Autowired
    ScheduledActuationRepository instance;
    @Autowired
    private MongoTemplate mongo;
    
    JobCoordinator mk_jobCoordinator;
    
    public ScheduledActuationRepositoryImplIT() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    @Before
    public void setUp() {
        mongo.dropCollection(ScheduledActuation.class);
        mk_jobCoordinator = mock(JobCoordinator.class);
    }

    @Test
    public void nextIdentity() {
        System.out.println("nextIdentity");
        BulbsContextUserId creator = new BulbsContextUserId("testNextId__userId");
        ScheduledActuationId result = instance.nextIdentity(creator);
        assertThat(result.getCreator(), is(creator)  );
        assertThat("Identity uuid exists", !StringUtils.isEmpty(result.getUuid()));
    }

    @Test
    public void findOne() {
        System.out.println("findOne");
        BulbsContextUserId creator = new BulbsContextUserId("findById__userId");
        ScheduledActuationId id = instance.nextIdentity(creator);
        ScheduledActuation expResult = newTestInstance(id, true, true);
        
        instance.save(expResult);
        ScheduledActuation result = instance.findOne(id);
        
        assertThat(result, is(expResult) );
        assertThat(result.getTrigger(), is(expResult.getTrigger()) );
        assertThat(result.getStates(), is(expResult.getStates()));
        assertThat(result.getName(), is(expResult.getName()));
        assertThat(result.getCreated(), is(expResult.getCreated()));
        assertThat(result.isDeleteAfterExecution(), is(expResult.isDeleteAfterExecution())  );
        
    }

    @Test
    public void findByNameAndScheduleId_Creator() {
        System.out.println("findByName");
        BulbsContextUserId creator = new BulbsContextUserId("testLoadById__userId");
        ScheduledActuationId id = instance.nextIdentity(creator);
        ScheduledActuation expResult = newTestInstance(id, true, false);
        
        instance.save(expResult);
        ScheduledActuation result = instance.findByNameAndId_Creator(expResult.getName(), creator);
        assertThat(result, is(expResult));
        assertThat(result.getTrigger(), is(expResult.getTrigger()));
    }
    @Test
    public void findByScheduleId_Owner(){
        System.out.println("findByScheduleId_Owner");
        BulbsContextUserId userXp = new BulbsContextUserId("test_UserUuid_exp");
        BulbsContextUserId userUXp = new BulbsContextUserId("test_UserUuid_unExp");
        ScheduledActuation xp1 = newTestInstance(instance.nextIdentity(userXp), false, false);
        ScheduledActuation xp2 = newTestInstance(instance.nextIdentity(userXp), true, false);
        ScheduledActuation uxp1 = newTestInstance(instance.nextIdentity(userUXp), false, false);
        Set<ScheduledActuation> expResult = new HashSet<>();
        expResult.add(xp1);
        expResult.add(xp2);

        instance.save(Arrays.asList(uxp1, xp1, xp2));
        Set<ScheduledActuation> result = instance.findById_Creator(userXp);
        assertThat(result, is(expResult));
    }
    @Test
    public void findByStates_PresetId(){
        System.out.println("findByStates_PresetId");
        BulbsContextUserId userXp = new BulbsContextUserId("test_UserUuid_exp");
        BulbsContextUserId userUXp = new BulbsContextUserId("test_UserUuid_unExp");
        ScheduledActuation xp1 = newTestInstance(instance.nextIdentity(userXp), false, false);
        ScheduledActuation uxp1 = newTestInstance(instance.nextIdentity(userUXp), false, true);
        ScheduledActuation uxp2 = newTestInstance(instance.nextIdentity(userXp), false, true);
        Set<ScheduledActuation> expResult = new HashSet<>();
        expResult.add(xp1);
        PresetId presetId = new PresetId("testPresetUuid-1", xp1.getId().getCreator());
        PresetId presetId2 = new PresetId("testPresetUuid-2", xp1.getId().getCreator());
        xp1.setNewStates(Arrays.asList(
                new PresetActuatorCommand(AppIdCore.instance(), "testUserApiKey", new CommandPriority(0),
                        presetId, false),
                new PresetActuatorCommand(AppIdCore.instance(), "testUserApiKey", new CommandPriority(0),
                        presetId2, false)
        ), mk_jobCoordinator);


        instance.save(Arrays.asList(uxp1, uxp2, xp1));
        Set<ScheduledActuation> result = instance.findByStatesContainsTargetId(presetId);
        assertThat(result, is(expResult));
    }

    public void testStore() {
        // -> See testLoadById()
    }

    @Test
    public void delete() {
        System.out.println("delete");
        BulbsContextUserId creator = new BulbsContextUserId("testLoadById__userId");
        ScheduledActuationId id = instance.nextIdentity(creator);
        ScheduledActuation expResult = newTestInstance(id, true, true);
        
        instance.save(expResult);
        ScheduledActuation res = instance.findOne(id);
        assertThat(res, is(notNullValue()));
        instance.delete(id);
        res = instance.findOne(id);
        assertThat(res, is(nullValue()));
    }
    
    private ScheduledActuation newTestInstance(ScheduledActuationId id, boolean withTrigger, boolean withStates){
        final ScheduledActuation res;
        if(withTrigger){
            PointInTimeTrigger trigger = new PointInTimeTrigger(new Date(), "UTC");
            trigger.toCronExpression(); // create cron exp internally
            res = new ScheduledActuation(
                    id, 
                    "testLoadById_actName",
                    trigger, true);
        }else{
            res = new ScheduledActuation(id, "test_name", false);
        }

        if(withStates){
            final List<AbstractActuatorCmd> states = new ArrayList<>();
            final BulbBridgeId bridgeId = new BulbBridgeId("testSchedActRep_bridgeId_0");
            states.add(new BulbActuatorCommand(new BulbId(bridgeId, "1"), AppIdCore.instance(), "testUserApiKey",
                    new CommandPriority(0), new LinkedList<>()));
            states.add(new BulbActuatorCommand(new BulbId(bridgeId, "2"), AppIdCore.instance(), "testUserApiKey",
                    new CommandPriority(0), new LinkedList<>()));
            states.add(new BulbActuatorCommand(new BulbId(bridgeId, "3"), AppIdCore.instance(), "testUserApiKey",
                    new CommandPriority(0), new LinkedList<>()));
            states.add(new PresetActuatorCommand(AppIdCore.instance(), "testUserApiKe", new CommandPriority(0),
                    new PresetId("testPresetUuid-generic", id.getCreator()), false) );
            res.setNewStates(states, mk_jobCoordinator);
        }
        
        return res;
    }
    
}
