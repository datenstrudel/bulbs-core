package net.datenstrudel.bulbs.core.application.services;

import net.datenstrudel.bulbs.core.TestConfig;
import net.datenstrudel.bulbs.core.application.ApplicationLayerConfig;
import net.datenstrudel.bulbs.core.config.BulbsCoreConfig;
import net.datenstrudel.bulbs.core.domain.model.bulb.*;
import net.datenstrudel.bulbs.core.domain.model.identity.AppIdCore;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUser;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUserRepository;
import net.datenstrudel.bulbs.core.domain.model.identity.ValidatorBulbsContextUser;
import net.datenstrudel.bulbs.core.security.config.SecurityConfig;
import net.datenstrudel.bulbs.core.websocket.WebSocketConfig;
import net.datenstrudel.bulbs.shared.domain.model.bulb.*;
import net.datenstrudel.bulbs.shared.domain.model.color.ColorRGB;
import org.junit.Before;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.LinkedList;

import static org.junit.Assert.*;

/**
 *
 * @author Thomas Wendzinski
 */
@SpringApplicationConfiguration(
    initializers = TestConfig.class,
    classes = {
        BulbsCoreConfig.class,
        ApplicationLayerConfig.class,
        SecurityConfig.class,
        WebSocketConfig.class,
        TestConfig.class
})
@RunWith(SpringJUnit4ClassRunner.class)
public class BasicFunctionalityIntegrationIT {
    
    //~ Member(s) //////////////////////////////////////////////////////////////
    private static final Logger log = LoggerFactory.getLogger(BasicFunctionalityIntegrationIT.class);
    @Autowired
    BulbsContextUserService userService;
    @Autowired
    BulbsContextUserRepository userRepository;
    @Autowired
    BulbBridgeAdminService bridgeAdminService;
    @Autowired
    ActuatorService actuatorService;
    @Autowired
    BulbBridgeRepository bridgeRepository;
    @Autowired
    private MongoTemplate mongo;
    
    //~ Construction ///////////////////////////////////////////////////////////
    //~ Method(s) //////////////////////////////////////////////////////////////
    @Before
    public void setUp() {
        mongo.dropCollection(BulbBridge.class);
        mongo.dropCollection(BulbsContextUser.class);
    }

    @Test
    public void testSignup_BridgeCreation_CommandExec() throws BulbBridgeHwException{
        BulbsContextUser user = userService.signUp("integrationTest_"+System.currentTimeMillis(), 
                "integrationTestCredentials", "integrationTestNickname",
                new ValidatorBulbsContextUser.NotificationHandlerBulbsContextUser() {
                    @Override
                    public void handleDuplicateEmail(String mailAddressConcerned) {
                        log.error("Duplicate mail[" + mailAddressConcerned + "]");
                    }
                    @Override
                    public void handleInvalidPassword() {
                        log.error("Invalid Password");
                    }
                }
        );

        assertNotNull(user);
        BulbBridge bridge = bridgeAdminService.findAndCreateBulbBridge(
                BulbsPlatform._EMULATED, 
                new BulbBridgeAddress("intTestHost__"+System.currentTimeMillis(), 1234),
                user.getApiKey()); 
        assertNotNull(bridge);
        log.info("Bridge created: " + bridge);
        try{
            Thread.sleep(1000);
        }catch (InterruptedException iex){}

        BulbsContextUser assertUser = userRepository.findByEmail(user.getEmail());
        assertTrue(!assertUser.getBulbsPrincipals().isEmpty());
        
        BulbId bulbAddressed = bridge.getBulbs().iterator().next().getId();
        final BulbState state2Apply = new BulbState(new ColorRGB(255, 255, 255), true);
        actuatorService.execute(
                new BulbActuatorCommand(
                        bulbAddressed, 
                        AppIdCore.instance(),
                        assertUser.getApiKey(), 
                        CommandPriority.standard(), 
                new LinkedList<BulbState>(){{
                    add(state2Apply);
                }})
        );
        
        try{
            Thread.sleep(2000);
        }catch (InterruptedException iex){
        }
        bridge = bridgeAdminService.bridgesByContextUser(user.getApiKey()).iterator().next();
        Bulb assertBulb = bridge.bulbById(bulbAddressed);
        assertEquals(state2Apply, assertBulb.getState());
        
        
        log.info("////////////////////////////////////////////////////////////////////////");
        log.info("/~ Integration test 'testSignupAndBridgeCreation' successfully finished.");
        log.info("////////////////////////////////////////////////////////////////////////");
        
    }
    
    //~ Private Artifact(s) ////////////////////////////////////////////////////

}
