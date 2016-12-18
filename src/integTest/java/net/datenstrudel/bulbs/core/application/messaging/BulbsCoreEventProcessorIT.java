package net.datenstrudel.bulbs.core.application.messaging;

import net.datenstrudel.bulbs.core.AbstractBulbsIT;
import net.datenstrudel.bulbs.core.TestUtil;
import net.datenstrudel.bulbs.core.application.services.ActuatorService;
import net.datenstrudel.bulbs.core.domain.model.bulb.AbstractActuatorCmd;
import net.datenstrudel.bulbs.core.domain.model.bulb.ActuatorDomainService;
import net.datenstrudel.bulbs.core.domain.model.bulb.BulbActuatorCommand;
import net.datenstrudel.bulbs.core.domain.model.messaging.DomainEventPublisher;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;

/**
 *
 * @author Thomas Wendzinski
 */
public class BulbsCoreEventProcessorIT extends AbstractBulbsIT {

    @Autowired
    @Qualifier("bulbActuatorServiceImpl")
    private ActuatorService applicationService;
    private ActuatorDomainService mk_actuatorDomainService = mock(ActuatorDomainService.class);
    
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


    @Configuration
    public static class Cfg{
        @Bean
        public SimpMessageSendingOperations simpMessageSendingOperations(){
            return mock(SimpMessageSendingOperations.class);
        }
    }
}
