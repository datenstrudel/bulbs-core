package net.datenstrudel.bulbs.core.infrastructure.persistence;

import net.datenstrudel.bulbs.core.IntegrationTest;
import net.datenstrudel.bulbs.core.TestConfig;
import net.datenstrudel.bulbs.core.domain.model.bulb.BulbBridgeId;
import net.datenstrudel.bulbs.core.domain.model.bulb.BulbId;
import net.datenstrudel.bulbs.core.domain.model.group.BulbGroup;
import net.datenstrudel.bulbs.core.domain.model.group.BulbGroupId;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUserId;
import net.datenstrudel.bulbs.core.infrastructure.PersistenceConfig;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.junit.Assert.*;

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
public class BulbGroupRepositoryImplTest  {
    
    private static final Logger log = LoggerFactory.getLogger(BulbGroupRepositoryImplTest.class);
    
    @Autowired
    BulbGroupRepositoryImpl instance;
    @Autowired
    private MongoTemplate mongo;
    private static boolean initialized = false;
    
    public BulbGroupRepositoryImplTest() {
    }
    
    @Before
    public void setUp() {
        if(BulbGroupRepositoryImplTest.initialized) return ;
        BulbGroupRepositoryImplTest.initialized = true;
        mongo.dropCollection(BulbGroup.class);
    }

//    @Test
    public void testNextIdentity() {
        System.out.println("nextIdentity");
        BulbsContextUserId creatorId = null;
        BulbGroupRepositoryImpl instance = null;
        BulbGroupId expResult = null;
        BulbGroupId result = instance.nextIdentity(creatorId);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    @Test
    public void testLoadById() {
        System.out.println("loadById");
        BulbGroup expResult = newTestBulbGroup();

        instance.store(expResult);
        BulbGroup result = instance.loadById(expResult.getGroupId());
        
        assertEquals(expResult, result);
        assertEquals(expResult.getBulbs(), result.getBulbs());
        assertEquals(expResult.getName(), result.getName());
    }

    @Test
    public void testLoadByName() {
        System.out.println("loadByName");
        BulbGroup expResult = newTestBulbGroup();
        
        instance.store(expResult);
        BulbGroup result = instance.loadByName(
                expResult.getGroupId().getUserId(), expResult.getName());
        
        assertEquals(expResult, result);
    }

    @Test
    public void testLoadByOwner() {
        System.out.println("loadByOwner");
        LinkedHashSet<BulbGroup> expResult = new LinkedHashSet<>();
        BulbGroup g_0 = newTestBulbGroup();
        BulbGroup g_1 = newTestBulbGroup();
        ReflectionTestUtils.setField(g_1.getGroupId(), "userId", g_0.getGroupId().getUserId()); 
        expResult.add(g_0);
        expResult.add(g_1);

        Iterator<BulbGroup> it = (Iterator) expResult.iterator();
        instance.store(it.next());
        instance.store(it.next());
        Set<BulbGroup> result = instance.loadByOwner(expResult.iterator().next().userId());
        assertTrue(Objects.equals(expResult, result));
    }

    @Test
    public void testStore() {
        System.out.println("store - noOp see testLoadById");
    }

    @Test
    public void testRemove() {
        System.out.println("remove");
        BulbGroup group = newTestBulbGroup();
        BulbGroupId groupId = group.getGroupId();
        BulbGroup res;
        
        instance.store(group);
        res = instance.loadById(groupId);
        assertEquals(res, group);
        instance.remove(groupId); groupId.equals(res.getGroupId());
        res = instance.loadById(groupId);
        assertNull(res);
        
    }
    
    private BulbGroup newTestBulbGroup(){
        BulbGroupId groupId = instance.nextIdentity(new BulbsContextUserId(UUID.randomUUID().toString()));
        BulbGroup expResult = new BulbGroup(groupId, "TestLoadById__GroupName");
        expResult.addBulb(new BulbId(new BulbBridgeId("TestLoadById__BulbIdBridge"), 0));
        expResult.addBulb(new BulbId(new BulbBridgeId("TestLoadById__BulbIdBridge"), 1));
        return expResult;
    }
}
