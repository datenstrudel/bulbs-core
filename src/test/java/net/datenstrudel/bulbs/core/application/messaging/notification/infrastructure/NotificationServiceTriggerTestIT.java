package net.datenstrudel.bulbs.core.application.messaging.notification.infrastructure;

import net.datenstrudel.bulbs.core.TestConfig;
import net.datenstrudel.bulbs.core.application.ApplicationLayerConfig;
import net.datenstrudel.bulbs.core.application.messaging.notification.NotificationService;
import net.datenstrudel.bulbs.core.config.BulbsCoreConfig;
import net.datenstrudel.bulbs.core.security.config.SecurityConfig;
import net.datenstrudel.bulbs.core.testConfigs.WebTestConfig;
import net.datenstrudel.bulbs.core.websocket.WebSocketConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;

import static org.easymock.EasyMock.*;

/**
 *
 * @author Thomas Wendzinski
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(
    initializers = {TestConfig.class, ConfigFileApplicationContextInitializer.class},
    classes = {
            SecurityConfig.class,
            TestConfig.class,
            WebTestConfig.class,
            BulbsCoreConfig.class,
            ApplicationLayerConfig.class,
            WebSocketConfig.class
})
@IntegrationTest
public class NotificationServiceTriggerTestIT {
    
    @Autowired
    private NotificationServiceTrigger instance;
    private NotificationService mk_notificationService;
    public NotificationServiceTriggerTestIT() {
    }
    
    @Before
    public void setUp() {
        mk_notificationService = createMock(NotificationService.class);
        mk_notificationService.publishNotifications();
        expectLastCall().anyTimes();
        replay(mk_notificationService);
        
        ReflectionTestUtils.invokeSetterMethod(instance, "setNfService_coreInternal", mk_notificationService);
    }

    @Test
    public void testTrigger_coreInternal() {
        try {
            Thread.sleep(2000l);
        } catch (InterruptedException ex) {}
        verify(mk_notificationService);
    }
}