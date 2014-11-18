package net.datenstrudel.bulbs.core.application.messaging;

import net.datenstrudel.bulbs.core.IntegrationTest;
import net.datenstrudel.bulbs.core.TestConfig;
import net.datenstrudel.bulbs.core.TestUtil;
import net.datenstrudel.bulbs.core.application.ApplicationLayerConfig;
import net.datenstrudel.bulbs.core.application.services.ActuatorService;
import net.datenstrudel.bulbs.core.config.BulbsCoreConfig;
import net.datenstrudel.bulbs.core.domain.model.bulb.AbstractActuatorCmd;
import net.datenstrudel.bulbs.core.domain.model.bulb.ActuatorDomainService;
import net.datenstrudel.bulbs.core.domain.model.bulb.BulbActuatorCommand;
import net.datenstrudel.bulbs.core.domain.model.messaging.DomainEventPublisher;
import net.datenstrudel.bulbs.core.security.config.SecurityConfig;
import net.datenstrudel.bulbs.core.websocket.WebSocketConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;

/**
 *
 * @author Thomas Wendzinski
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(
    initializers = {TestConfig.class},
    classes = {
            SecurityConfig.class,
            TestConfig.class,
            BulbsCoreConfig.class,
            ApplicationLayerConfig.class,
            WebSocketConfig.class
})
@Category(IntegrationTest.class)
public class BulbsCoreEventProcessorTest {

    @Autowired
    @Qualifier("bulbActuatorServiceImpl")
    private ActuatorService applicationService;
    private ActuatorDomainService mk_actuatorDomainService = Mockito.mock(ActuatorDomainService.class);
    
    @Before
    public void setUp() throws Exception {
        ReflectionTestUtils.setField(TestUtil.unwrappedProxiedBean(applicationService), "actuatorDomainService", mk_actuatorDomainService);
        Mockito.doNothing().when(mk_actuatorDomainService).execute(any(AbstractActuatorCmd.class));
        DomainEventPublisher.instance().reset();
    }
    
    @Test
    public void listenToDomainEvents() throws Exception {
        applicationService.execute(new BulbActuatorCommand(null, null, "IRRELEVANT", null, null) );

        DomainEventPublisher publisher = DomainEventPublisher.instance();
        ThreadLocal<Map> expSubscr = (ThreadLocal<Map>) ReflectionTestUtils.getField(publisher, "subscribers");
        assertTrue(!expSubscr.get().isEmpty());
    }
}
