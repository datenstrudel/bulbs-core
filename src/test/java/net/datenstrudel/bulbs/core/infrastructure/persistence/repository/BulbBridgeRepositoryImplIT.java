package net.datenstrudel.bulbs.core.infrastructure.persistence.repository;

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
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.hamcrest.Matchers.is;
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
@DirtiesContext
public class BulbBridgeRepositoryImplIT {
    
    private static final Logger log = LoggerFactory.getLogger(BulbBridgeRepositoryImplIT.class);
    
    @Autowired
    BulbBridgeRepository instance;
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
        instance.save(bulbBridge_0);
        instance.save(bulbBridge_1);
        Set expResult = new HashSet<BulbBridge>(){{
          add(bulbBridge_0);
          add(bulbBridge_1);
        }};
        
        Set result = instance.findByOwner(userId);
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

        instance.save(bulbBridge_1);
        instance.save(bulbBridge_0);
        assertThat(instance.findAll().containsAll(Arrays.asList(bulbBridge_0, bulbBridge_1)), is(Boolean.TRUE));
        Set expResult = new HashSet<BulbBridge>(){{
          add(bulbBridge_0);
          add(bulbBridge_1);
        }};
        
        Set result = instance.findByOwnerAndBulbs_Name(userId, "testBulb_1");
        assertEquals(expResult, result);
    }
    
    @Test
    public void testRemove() {
        final BulbBridge bulbBridge = aTestBridge(2);
        
        instance.save(bulbBridge);
        instance.delete(bulbBridge.getId());
        
        BulbBridge res = instance.findOne(bulbBridge.getId());
        assertNull(res);
    }
    @Test
    public void save() throws Exception {
        System.out.println("save");
        final BulbBridge bulbBridge = aTestBridge(1);
        final Bulb firstBulb = bulbBridge.getBulbs().iterator().next();
        
        instance.save(bulbBridge);
        
        BulbBridge res = instance.findOne(bulbBridge.getId());
        assertBulbBridge(bulbBridge, res, firstBulb);
    }
    @Test
    public void save_many()throws Exception{
        System.out.println("save_many");
        int numBulbs = 100;
        final BulbBridge bulbBridge = aTestBridge(numBulbs);
        final Bulb firstBulb = bulbBridge.bulbById(new BulbId(bulbBridge.getId(), 1));
        Long start;
        BulbBridge res;
        
        for (int i = 0; i < 50; i++) {
            start = System.currentTimeMillis();
            instance.save(bulbBridge);
            log.info("StoreTime of " + numBulbs + " bulbs (ms): " + (System.currentTimeMillis() - start) );
            
            start = System.currentTimeMillis();
            res = instance.findOne(bulbBridge.getId());
            log.info("Restore time of " + numBulbs + " (ms): " + (System.currentTimeMillis() - start));
            
            assertBulbBridge(bulbBridge, res, firstBulb);
        }
    }
    
//    @Test
//    public void testStoreDuplicate() throws Exception {
//        System.out.println("testStoreDuplicate");
//        final BulbBridge bulbBridge = aTestBridge(1);
//        final BulbBridge bulbBridge_dup = aTestBridge(1);
//        ReflectionTestUtils.setField(bulbBridge_dup.getId(), "macAddress", bulbBridge.getId().getMacAddress());
//        ReflectionTestUtils.setField(bulbBridge_dup, "name", "nameDup");
//        
//        
//        instance.save(bulbBridge);
//        instance.save(bulbBridge_dup);
//    }
    
    public void testLoadById() {
        // See Store
    }
    
    private void assertBulbBridge(BulbBridge expResult, BulbBridge result, Bulb firstBulb )
            throws Exception{
        
        assertEquals(expResult, result);

        Bulb el = result.bulbById(new BulbId(result.getId(), 1));
        assertEquals(firstBulb.getId(), el.getId());
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
                        new BulbId(bulbBridge.getId(), i),
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