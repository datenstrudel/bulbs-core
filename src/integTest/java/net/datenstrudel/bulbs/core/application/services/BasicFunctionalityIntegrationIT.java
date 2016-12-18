package net.datenstrudel.bulbs.core.application.services;

import net.datenstrudel.bulbs.core.AbstractBulbsIT;
import net.datenstrudel.bulbs.core.domain.model.bulb.Bulb;
import net.datenstrudel.bulbs.core.domain.model.bulb.BulbActuatorCommand;
import net.datenstrudel.bulbs.core.domain.model.bulb.BulbBridge;
import net.datenstrudel.bulbs.core.domain.model.bulb.BulbBridgeRepository;
import net.datenstrudel.bulbs.core.domain.model.bulb.BulbId;
import net.datenstrudel.bulbs.core.domain.model.identity.AppIdCore;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUser;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUserRepository;
import net.datenstrudel.bulbs.core.domain.model.identity.ValidatorBulbsContextUser;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbBridgeAddress;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbState;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbsPlatform;
import net.datenstrudel.bulbs.shared.domain.model.bulb.CommandPriority;
import net.datenstrudel.bulbs.shared.domain.model.color.ColorRGB;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.annotation.DirtiesContext;

import java.util.LinkedList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

/**
 * @author Thomas Wendzinski
 */
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class BasicFunctionalityIntegrationIT extends AbstractBulbsIT {

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

    @Before
    public void setUp() {
        mongo.dropCollection(BulbBridge.class);
        mongo.dropCollection(BulbsContextUser.class);
    }

    @Test
    public void testSignup_BridgeCreation_CommandExec() throws Exception {
        BulbsContextUser user = userService.signUp("integrationTest_" + System.currentTimeMillis(),
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

        assertThat(user, is(notNullValue()));
        BulbBridge bridge = bridgeAdminService.findAndCreateBulbBridge(
                BulbsPlatform._EMULATED,
                new BulbBridgeAddress("intTestHost__" + System.currentTimeMillis(), 1234),
                user.getApiKey());
        assertThat(bridge, is(notNullValue()));
        log.info("Bridge created: " + bridge);
        Thread.sleep(4000);

        BulbsContextUser assertUser = userRepository.findByEmail(user.getEmail());
        assertThat("The user has got principals", !assertUser.getBulbsPrincipals().isEmpty());

        BulbId bulbAddressed = bridge.getBulbs().iterator().next().getId();
        final BulbState state2Apply = new BulbState(new ColorRGB(255, 255, 255), true);
        actuatorService.execute(
                new BulbActuatorCommand(
                        bulbAddressed,
                        AppIdCore.instance(),
                        assertUser.getApiKey(),
                        CommandPriority.standard(),
                        new LinkedList<BulbState>() {{
                            add(state2Apply);
                        }})
        );

        Thread.sleep(3000);
        bridge = bridgeAdminService.bridgesByContextUser(user.getApiKey()).iterator().next();
        Bulb assertBulb = bridge.bulbById(bulbAddressed);
        assertThat(assertBulb.getState(), is(state2Apply));
    }
}
