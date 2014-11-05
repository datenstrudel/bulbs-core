package net.datenstrudel.bulbs.core.infrastructure.persistence;

import net.datenstrudel.bulbs.core.IntegrationTest;
import net.datenstrudel.bulbs.core.TestConfig;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUser;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUserId;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsPrincipal;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsPrincipalState;
import net.datenstrudel.bulbs.core.infrastructure.PersistenceConfig;
import net.datenstrudel.bulbs.shared.domain.model.identity.AppId;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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
        TestConfig.class
})
@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class BulbsContextUserRepositoryImplTest {
    
    @Autowired
    BulbsContextUserRepositoryImpl instance = new BulbsContextUserRepositoryImpl();
    @Autowired
    private MongoTemplate mongo;
    private static boolean initialized = false;
    
    public BulbsContextUserRepositoryImplTest() {
    }
    
    @Before
    public void setUp() {
        if(BulbsContextUserRepositoryImplTest.initialized) return ;
        BulbsContextUserRepositoryImplTest.initialized = true;
        mongo.dropCollection(BulbsContextUser.class);
    }

    @Test
    public void testNextIdentity() {
        System.out.println("nextIdentity");
        BulbsContextUserId result = instance.nextIdentity();
        assertNotNull(result.getUuid());
        assertTrue(!result.getUuid().isEmpty());
    }
    @Test
    public void testLoadByEmail() {
        System.out.println("loadByEmail");
        BulbsContextUser testUser = newTestUser();
        String email = testUser.getEmail();
        instance.store(testUser); // Store in order there is sth that can be found
        BulbsContextUser result = instance.loadByEmail(email);
        assertEquals(testUser, result);
    }
    @Test
    public void testStore() {
        System.out.println("store");
        BulbsContextUser testUser = newTestUser();
        instance.store(testUser);
        
        BulbsContextUser res = instance.loadById(testUser.getBulbsContextUserlId());
        assertEquals(testUser, res);
        assertEquals(testUser.getEmail(), res.getEmail());
        assertEquals(testUser.getCredentials(), res.getCredentials());
        assertEquals(testUser.getBulbsPrincipals(), res.getBulbsPrincipals());
        assertEquals(testUser.getNickname(), res.getNickname());
        
        res.addBulbsPrincipal(
                new BulbsPrincipal(
                "addBulbsPrincipal", 
                new AppId("testApp"), 
                "testBBId", 
                BulbsPrincipalState.REGISTERED));
        instance.store(res);
        BulbsContextUser res2 = instance.loadById(testUser.getBulbsContextUserlId());
        assertEquals(res.getBulbsPrincipals().size(), res2.getBulbsPrincipals().size());
        assertEquals(res.getBulbsPrincipals(), res2.getBulbsPrincipals() );
        
    }
    public void testLoadById() {
        // Tested in testStore()
    }
    public void testRemove() {
        // Trivial
    }
    
    private BulbsContextUser newTestUser(){
        BulbsContextUser testUser = new BulbsContextUser(
                instance.nextIdentity(), 
                "testUserEmail_"+ (int)((Math.random() * 10000f)), 
                "testCredentials", 
                "testNickName",
                UUID.randomUUID().toString().toUpperCase());
        testUser.addBulbsPrincipal(new BulbsPrincipal("usernameTest_01", new AppId("testAppType_1"), "testBulbBridgeId_1", BulbsPrincipalState.REGISTERED));
        testUser.addBulbsPrincipal(new BulbsPrincipal("usernameTest_02", new AppId("testAppType_2"), "testBulbBridgeId_2", BulbsPrincipalState.REGISTERED));
        return testUser;
    }
}