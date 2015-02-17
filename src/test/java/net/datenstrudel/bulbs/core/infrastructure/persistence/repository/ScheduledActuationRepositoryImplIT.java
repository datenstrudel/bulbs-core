package net.datenstrudel.bulbs.core.infrastructure.persistence.repository;

import net.datenstrudel.bulbs.core.TestConfig;
import net.datenstrudel.bulbs.core.domain.model.bulb.AbstractActuatorCmd;
import net.datenstrudel.bulbs.core.domain.model.bulb.BulbActuatorCommand;
import net.datenstrudel.bulbs.core.domain.model.bulb.BulbBridgeId;
import net.datenstrudel.bulbs.core.domain.model.bulb.BulbId;
import net.datenstrudel.bulbs.core.domain.model.identity.AppIdCore;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUserId;
import net.datenstrudel.bulbs.core.domain.model.scheduling.JobCoordinator;
import net.datenstrudel.bulbs.core.domain.model.scheduling.ScheduledActuation;
import net.datenstrudel.bulbs.core.domain.model.scheduling.ScheduledActuationId;
import net.datenstrudel.bulbs.core.domain.model.scheduling.ScheduledActuationRepository;
import net.datenstrudel.bulbs.core.infrastructure.PersistenceConfig;
import net.datenstrudel.bulbs.core.infrastructure.services.InfrastructureServicesConfig;
import net.datenstrudel.bulbs.core.testConfigs.InfrastructureServicesTestConfig;
import net.datenstrudel.bulbs.shared.domain.model.bulb.CommandPriority;
import net.datenstrudel.bulbs.shared.domain.model.scheduling.PointInTimeTrigger;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.StringUtils;

import java.util.*;

import static org.junit.Assert.*;

/**
 *
 * @author Thomas Wendzinski
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
    initializers = TestConfig.class,
    classes = {
            TestConfig.class,
            PersistenceConfig.class,
            InfrastructureServicesConfig.class,
            InfrastructureServicesTestConfig.class
    })
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
        mk_jobCoordinator = EasyMock.createNiceMock(JobCoordinator.class);
    }

    @Test
    public void nextIdentity() {
        System.out.println("nextIdentity");
        BulbsContextUserId creator = new BulbsContextUserId("testNextId__userId");
        ScheduledActuationId result = instance.nextIdentity(creator);
        assertEquals(creator, result.getCreator() );
        assertTrue(!StringUtils.isEmpty(result.getUuid()));
    }

    @Test
    public void findOne() {
        System.out.println("findOne");
        BulbsContextUserId creator = new BulbsContextUserId("findById__userId");
        ScheduledActuationId id = instance.nextIdentity(creator);
        ScheduledActuation expResult = newTestInstance(id, true, true);
        
        instance.save(expResult);
        ScheduledActuation result = instance.findOne(id);
        
        assertEquals(expResult, result);
        assertEquals(expResult.getTrigger(), result.getTrigger());
        assertEquals(expResult.getStates(), result.getStates());
        assertEquals(expResult.getName(), result.getName());
        assertEquals(expResult.getCreated(), result.getCreated());
        assertEquals(expResult.isDeleteAfterExecution(), result.isDeleteAfterExecution() );
        
    }

    @Test
    public void findByNameAndScheduleId_Creator() {
        System.out.println("findByName");
        BulbsContextUserId creator = new BulbsContextUserId("testLoadById__userId");
        ScheduledActuationId id = instance.nextIdentity(creator);
        ScheduledActuation expResult = newTestInstance(id, false, false);
        
        instance.save(expResult);
        ScheduledActuation result = instance.findByNameAndId_Creator(expResult.getName(), creator);
        assertEquals(expResult, result);
    }
    @Test
    public void findByScheduleId_Owner(){
        System.out.println("findByScheduleId_Owner");
        BulbsContextUserId userXp = new BulbsContextUserId("test_UserUuid_exp");
        BulbsContextUserId userUXp = new BulbsContextUserId("test_UserUuid_unExp");
        ScheduledActuation xp1 = newTestInstance(instance.nextIdentity(userXp), false, false);
        ScheduledActuation xp2 = newTestInstance(instance.nextIdentity(userXp), false, false);
        ScheduledActuation uxp1 = newTestInstance(instance.nextIdentity(userUXp), false, false);
        Set<ScheduledActuation> expResult = new HashSet<>();
        expResult.add(xp1);
        expResult.add(xp2);

        instance.save(Arrays.asList(uxp1, xp1, xp2));
        Set<ScheduledActuation> result = instance.findById_Creator(userXp);
        assertEquals(expResult, result);
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
        assertNotNull(res);
        instance.delete(id);
        res = instance.findOne(id);
        assertNull(res);
    }
    
    private ScheduledActuation newTestInstance(ScheduledActuationId id, boolean withTrigger, boolean withStates){
        final ScheduledActuation res;
        if(withTrigger){
            res = new ScheduledActuation(
                    id, 
                    "testLoadById_actName", 
                    new PointInTimeTrigger(new Date(), "UTC"), true);
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
            res.setNewStates(states, mk_jobCoordinator);
        }
        
        return res;
    }
    
}
