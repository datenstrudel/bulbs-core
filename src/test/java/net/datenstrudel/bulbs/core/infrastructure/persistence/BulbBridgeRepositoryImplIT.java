package net.datenstrudel.bulbs.core.infrastructure.persistence;

import net.datenstrudel.bulbs.core.TestConfig;
import net.datenstrudel.bulbs.core.domain.model.bulb.*;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUserId;
import net.datenstrudel.bulbs.core.infrastructure.PersistenceConfig;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbBridgeAddress;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbState;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbsPlatform;
import net.datenstrudel.bulbs.shared.domain.model.color.ColorRGB;
import net.datenstrudel.bulbs.shared.domain.model.identity.AppId;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 *
 * @author Thomas Wendzinski
 */
@ContextConfiguration(
    initializers = TestConfig.class,
    classes = {
            PersistenceConfig.class,
    })
@RunWith(SpringJUnit4ClassRunner.class)
@IntegrationTest
@DirtiesContext
public class BulbBridgeRepositoryImplIT {
    
    private static final Logger log = LoggerFactory.getLogger(BulbBridgeRepositoryImplIT.class);
    
    @Autowired
    BulbBridgeRepositoryImpl instance;
    @Autowired
    private MongoTemplate mongo;
    private static boolean initialized = false;
    
    public BulbBridgeRepositoryImplIT() {
    }
    
    @Before
    public void setUp() {
        if(BulbBridgeRepositoryImplIT.initialized) return ;
        BulbBridgeRepositoryImplIT.initialized = true;
        mongo.dropCollection(BulbBridge.class);
    }

    @Test
    public void testLoadByUser() {
        System.out.println("testLoadByUser");
        final BulbBridge bulbBridge_0 = aTestBridge(2);
        final BulbBridge bulbBridge_1 = aTestBridge(2);
        BulbsContextUserId userId = new BulbsContextUserId("__SPEC_USER_ID__testLoadByUser");
        ReflectionTestUtils.setField(bulbBridge_0, "owner", userId);
        ReflectionTestUtils.setField(bulbBridge_1, "owner", userId);
        instance.store(bulbBridge_0);
        instance.store(bulbBridge_1);
        Set expResult = new HashSet<BulbBridge>(){{
          add(bulbBridge_0);
          add(bulbBridge_1);
        }};
        
        Set result = instance.loadByOwner(userId);
        assertEquals(expResult, result);
    }
    @Test
    public void testLoadByOwnerAndBulbname(){
        System.out.println("testLoadByOwnerAndBulbname");
        final BulbBridge bulbBridge_0 = aTestBridge(2);
        final BulbBridge bulbBridge_1 = aTestBridge(2);
        BulbsContextUserId userId = new BulbsContextUserId("__SPEC_USER_ID__testLoadByOwnerAndBulbname");
        ReflectionTestUtils.setField(bulbBridge_0, "owner", userId);
        ReflectionTestUtils.setField(bulbBridge_1, "owner", userId);

        instance.store(bulbBridge_0);
        instance.store(bulbBridge_1);
        Set expResult = new HashSet<BulbBridge>(){{
          add(bulbBridge_0);
          add(bulbBridge_1);
        }};
        
        Set result = instance.loadByOwnerAndBulbname(userId, "testBulb_1");
        assertEquals(expResult, result);
    }
    
    @Test
    public void testRemove() {
        final BulbBridge bulbBridge = aTestBridge(2);
        
        instance.store(bulbBridge);
        instance.remove(bulbBridge.getBridgeId());
        
        BulbBridge res = instance.loadById(bulbBridge.getBridgeId());
        assertNull(res);
    }
    @Test
    public void testStore() throws Exception {
        System.out.println("testStore");
        final BulbBridge bulbBridge = aTestBridge(1);
        final Bulb firstBulb = bulbBridge.getBulbs().iterator().next();
        
        instance.store(bulbBridge);
        
        BulbBridge res = instance.loadById(bulbBridge.getBridgeId());
        assertBulbBridge(bulbBridge, res, firstBulb);
    }
    @Test
    public void testStoreMany()throws Exception{
        System.out.println("testStoreMany");
        int numBulbs = 100;
        final BulbBridge bulbBridge = aTestBridge(numBulbs);
        final Bulb firstBulb = bulbBridge.bulbById(new BulbId(bulbBridge.getBridgeId(), 1));
        Long start;
        BulbBridge res;
        
        for (int i = 0; i < 50; i++) {
            start = System.currentTimeMillis();
            instance.store(bulbBridge);
            log.info("StoreTime of " + numBulbs + " bulbs (ms): " + (System.currentTimeMillis() - start) );
            
            start = System.currentTimeMillis();
            res = instance.loadById(bulbBridge.getBridgeId());
            log.info("Restore time of " + numBulbs + " (ms): " + (System.currentTimeMillis() - start));
            
            assertBulbBridge(bulbBridge, res, firstBulb);
        }
    }
    
//    @Test
//    public void testStoreDuplicate() throws Exception {
//        System.out.println("testStoreDuplicate");
//        final BulbBridge bulbBridge = aTestBridge(1);
//        final BulbBridge bulbBridge_dup = aTestBridge(1);
//        ReflectionTestUtils.setField(bulbBridge_dup.getBridgeId(), "macAddress", bulbBridge.getBridgeId().getMacAddress());
//        ReflectionTestUtils.setField(bulbBridge_dup, "name", "nameDup");
//        
//        
//        instance.store(bulbBridge);
//        instance.store(bulbBridge_dup);
//    }
    
    public void testLoadById() {
        // See Store
    }
    
    private void assertBulbBridge(BulbBridge expResult, BulbBridge result, Bulb firstBulb )
            throws Exception{
        
        assertEquals(expResult, result);
        assertNotNull(result.getId());
        
        Bulb el = result.bulbById(new BulbId(result.getBridgeId(), 1));
        assertEquals(firstBulb.getBulbId(), el.getBulbId());
        assertEquals(firstBulb.getBulbAttributes(), el.getBulbAttributes());
        assertEquals(firstBulb.getState(), el.getState());
        assertEquals(firstBulb.getPlatform(), el.getPlatform());
        assertEquals(firstBulb.getPriorityCoordinator(), el.getPriorityCoordinator());
        
        assertEquals(expResult, ReflectionTestUtils.getField(el, "bridge"));
        assertEquals(expResult.getBulbs().size(), result.getBulbs().size());
        
        assertEquals(expResult.getLocalAddress(), result.getLocalAddress());
        assertEquals(expResult.getName(), result.getName());
        assertEquals(expResult.getOwner(), result.getOwner());
        assertEquals(expResult.getPlatform(), result.getPlatform());
        assertEquals(expResult.getBridgeAttributes(), result.getBridgeAttributes());
    }
    
    private BulbBridge aTestBridge(final int numBulbs){
        final BulbBridge bulbBridge  = new BulbBridge(
                new BulbBridgeId(UUID.randomUUID().toString()), 
                "00:00:00:00:..test_"+ (int)(Math.random() * 1000000f),
                BulbsPlatform.PHILIPS_HUE, 
                new BulbBridgeAddress("testhost", 1234), 
                "testBridgeName", 
                new BulbsContextUserId("testUserId"),
                new HashMap<String, Object>(){{
                    put("testAttrb", 42);
                    put("testNestedAttrb",
                        new HashMap<String, String>(){{
                            put("nestedAttrb", "84"); 
                        }}
                    );
                }});
        bulbBridge.setBulbs(new HashSet<Bulb>(){{
            for (int i = 1; i < numBulbs+1; i++) {
                add(new Bulb(
                        new BulbId(bulbBridge.getBridgeId(), i), 
                        BulbsPlatform.PHILIPS_HUE, 
                        "testBulb_"+i, 
                        bulbBridge, 
                        new BulbState(new ColorRGB(255, 0, 0), true, 42), 
                        false, 
                        new HashMap<String, Object>(){{
                            put("testBulbAttrb", "bulbAttrb");
                        }},
                        new PriorityCoordinator(new AppId("appId_0"), new AppId("appId_1"), 1000l, 1200)
                ));
            }
        }});
        return bulbBridge;
    }
}