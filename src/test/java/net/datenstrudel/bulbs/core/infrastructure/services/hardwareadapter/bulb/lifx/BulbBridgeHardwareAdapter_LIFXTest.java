package net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter.bulb.lifx;

import net.datenstrudel.bulbs.core.domain.model.bulb.Bulb;
import net.datenstrudel.bulbs.core.domain.model.bulb.BulbBridge;
import net.datenstrudel.bulbs.core.domain.model.bulb.BulbBridgeId;
import net.datenstrudel.bulbs.core.domain.model.identity.AppIdCore;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUserId;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsPrincipal;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsPrincipalState;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbBridgeAddress;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbState;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbsPlatform;
import net.datenstrudel.bulbs.shared.domain.model.color.ColorHSB;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@ContextConfiguration(classes = {BulbBridgeHardwareAdapter_LIFXTest.Cfg.class })
@RunWith(SpringJUnit4ClassRunner.class)
public class BulbBridgeHardwareAdapter_LIFXTest {

    @Configuration
    @ComponentScan(basePackages = {
            "net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter"
    }, excludeFilters = @ComponentScan.Filter(Configuration.class) )
    public static class Cfg{

        @Bean
        public AsyncTaskExecutor taskExecutor(){
            return new SimpleAsyncTaskExecutor();
        }
    }

    public static final Logger log = LoggerFactory.getLogger(BulbBridgeHardwareAdapter_LIFXTest.class);
    @Autowired
    BulbBridgeHardwareAdapter_LIFX hwAdapter;

    @Test @Ignore("Ignored due to for manual testing and playing only.")
    public void playAndTry() throws Exception {

        BulbBridgeAddress address = new BulbBridgeAddress("192.168.1.255", 56700);
        BulbBridgeId bridgeId = new BulbBridgeId("test_uuid");
        BulbsPrincipal principal = new BulbsPrincipal("test_user", AppIdCore.instance(), bridgeId.getUuId(), BulbsPrincipalState.PENDING);
        BulbBridge bridge = hwAdapter.toBridge(
                address,
                bridgeId,
                principal,
                new BulbsContextUserId("test_useruuid"),
                BulbsPlatform.LIFX);

        log.info("Bridge found: {}", bridge );
        assertThat(bridge.getMacAddress(), is(notNullValue()));

        bridge.getMacAddress();
        Bulb[] bulbs = hwAdapter.toBulbs(bridge, principal, BulbsPlatform.LIFX);
        assertThat(bulbs.length, greaterThan(0));
        log.debug(" [B] Bulbs found: ", bulbs[0]);

        Bulb bulb_0 = bulbs[0];

        log.debug(" [Apply] bulb state.. ", bulbs[0]);
        hwAdapter.applyBulbState(
                bulb_0.getId(),
                bulb_0.getBridge().getLocalAddress(),
                principal,
                new BulbState(new ColorHSB(45f, 255f, 100f), true),
                BulbsPlatform.LIFX, null );

        Thread.sleep(3500);
    }
}

