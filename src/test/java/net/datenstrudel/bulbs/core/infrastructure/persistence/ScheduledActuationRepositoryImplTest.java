package net.datenstrudel.bulbs.core.infrastructure.persistence;

import net.datenstrudel.bulbs.core.IntegrationTest;
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
import net.datenstrudel.bulbs.core.infrastructure.PersistenceConfig;
import net.datenstrudel.bulbs.core.infrastructure.services.InfrastructureServicesConfig;
import net.datenstrudel.bulbs.shared.domain.model.bulb.CommandPriority;
import net.datenstrudel.bulbs.shared.domain.model.scheduling.PointInTimeTrigger;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

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
            InfrastructureServicesConfig.class
    })
//@ComponentScan(excludeFilters = @ComponentScan.Filter(Configuration.class) )
@Category(IntegrationTest.class)
public class ScheduledActuationRepositoryImplTest {
    
    private static final Logger log = LoggerFactory.getLogger(PresetRepositoryImplTest.class);
    
    @Autowired
    ScheduledActuationRepositoryImpl instance;
    @Autowired
    private MongoTemplate mongo;
    
    JobCoordinator mk_jobCoordinator;
    
    public ScheduledActuationRepositoryImplTest() {
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
    public void testNextIdentity() {
        System.out.println("nextIdentity");
        BulbsContextUserId creator = new BulbsContextUserId("testNextId__userId");
        ScheduledActuationId result = instance.nextIdentity(creator);
        assertEquals(creator, result.getUserId() );
        assertTrue(!StringUtils.isEmpty(result.getUuid()));
    }

    @Test
    public void testLoadById() {
        System.out.println("loadById");
        BulbsContextUserId creator = new BulbsContextUserId("testLoadById__userId");
        ScheduledActuationId id = instance.nextIdentity(creator);
        ScheduledActuation expResult = newTestInstance(id, true, true);
        
        instance.store(expResult);
        ScheduledActuation result = instance.loadById(id);
        
        assertEquals(expResult, result);
        assertEquals(expResult.getTrigger(), result.getTrigger());
        assertEquals(expResult.getStates(), result.getStates());
        assertEquals(expResult.getName(), result.getName());
        assertEquals(expResult.getCreated(), result.getCreated());
        assertEquals(expResult.isDeleteAfterExecution(), result.isDeleteAfterExecution() );
        
    }

    @Test
    public void testLoadByName() {
        System.out.println("loadByName");
        BulbsContextUserId creator = new BulbsContextUserId("testLoadById__userId");
        ScheduledActuationId id = instance.nextIdentity(creator);
        ScheduledActuation expResult = newTestInstance(id, false, false);
        
        instance.store(expResult);
        ScheduledActuation result = instance.loadByName(creator, expResult.getName());
        assertEquals(expResult, result);
    }

    public void testStore() {
        // -> See testLoadById()
    }

    @Test
    public void testRemove() {
        System.out.println("delete");
        BulbsContextUserId creator = new BulbsContextUserId("testLoadById__userId");
        ScheduledActuationId id = instance.nextIdentity(creator);
        ScheduledActuation expResult = newTestInstance(id, true, true);
        
        instance.store(expResult);
        ScheduledActuation res = instance.loadById(id);
        assertNotNull(res);
        instance.remove(id);
        res = instance.loadById(id);
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
            states.add(new BulbActuatorCommand(new BulbId(bridgeId, 1), AppIdCore.instance(), "testUserApiKey", 
                    new CommandPriority(0), new LinkedList<>()));
            states.add(new BulbActuatorCommand(new BulbId(bridgeId, 2), AppIdCore.instance(), "testUserApiKey",
                    new CommandPriority(0), new LinkedList<>()));
            states.add(new BulbActuatorCommand(new BulbId(bridgeId, 3), AppIdCore.instance(), "testUserApiKey",
                    new CommandPriority(0), new LinkedList<>()));
            res.setNewStates(states, mk_jobCoordinator);
        }
        
        return res;
    }
    
}
