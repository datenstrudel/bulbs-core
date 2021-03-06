package net.datenstrudel.bulbs.core.application.messaging.notification;

import net.datenstrudel.bulbs.core.TestConfig;
import net.datenstrudel.bulbs.core.application.ApplicationLayerConfig;
import net.datenstrudel.bulbs.core.application.messaging.eventStore.DomainEventStore;
import net.datenstrudel.bulbs.core.application.messaging.notification.handling.RabbitMqExchangeListener;
import net.datenstrudel.bulbs.core.application.messaging.notification.handling.identiy.BulbsPrincipalInitLinkEstablishedHandler;
import net.datenstrudel.bulbs.core.application.messaging.notification.infrastructure.NotificationServiceRabbitMq;
import net.datenstrudel.bulbs.core.config.BulbsCoreConfig;
import net.datenstrudel.bulbs.core.domain.model.messaging.DomainEvent;
import net.datenstrudel.bulbs.core.domain.model.messaging.DomainEventPublisher;
import net.datenstrudel.bulbs.core.domain.model.messaging.DomainEventSubscriber;
import net.datenstrudel.bulbs.core.security.config.SecurityConfig;
import net.datenstrudel.bulbs.core.web.config.WebConfig;
import net.datenstrudel.bulbs.core.websocket.WebSocketConfig;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbState;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Thomas Wendzinski
 */
@SpringBootTest(
        classes = {
                TestConfig.class,
                WebConfig.class,
                BulbsCoreConfig.class,
                ApplicationLayerConfig.class,
                SecurityConfig.class,
                WebSocketConfig.class
        }, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application-itest.properties")
public class NotificationServiceRabbitMq_RabbitMqExchangeListenerIT
        extends RabbitMqExchangeListener {

    private static final Logger log = LoggerFactory.getLogger(NotificationServiceRabbitMq_RabbitMqExchangeListenerIT.class);

    @Autowired
    @Qualifier(value = "coreInternalNotificationService")
    private NotificationServiceRabbitMq instance;

    @Autowired
    @Qualifier(value = "domainEventStore")
    private DomainEventStore eventStore;

    @Autowired
    private BulbsPrincipalInitLinkEstablishedHandler listener;

    private volatile boolean invocationSucceeded = false;

    @Test
    public void testPublishNotifications() {
        DomainEventPublisher.instance().subscribe("ID_TEST_PUBLISHER", new DomainEventSubscriber<DomainEvent>() {
            @Override
            public void handleEvent(DomainEvent event) {
                eventStore.store(event);
            }
            @Override
            public Class<DomainEvent> subscribedToEventType() {
                return DomainEvent.class;
            }
        });

        DomainEventPublisher.instance().publish(new TestEvent(null, "domainIdTest", new Date()));

        try {
            Thread.sleep(5000l);
        } catch (InterruptedException ex) {
        }
        assertTrue(invocationSucceeded);
    }

    @Override
    protected String topicName() {
        return Exchanges.EXCHANGE_TOPIC__BUBLS_CORE;
    }
    @Override
    protected String[] listensToEvents() {
        return new String[]{TestEvent.class.getName()};
    }
    @Override
    protected void filteredDispatch(String type, String message) {
        log.info(" [ <- ] Retrieved message with containing type[" + type + "]");
        assertEquals(TestEvent.class.getName(), type);
        invocationSucceeded = true;
    }

    private static final class TestEvent implements DomainEvent {

        private BulbState state;
        private String domainId;
        private Date occuredOn = new Date();

        public TestEvent(BulbState state, String domainId, Date occuredOn) {
            this.state = state;
            this.domainId = domainId;
            this.occuredOn = occuredOn;
        }

        public BulbState getState() {
            return state;
        }
        public void setState(BulbState state) {
            this.state = state;
        }

        public Date getOccurredOn() {
            return occuredOn;
        }
        public void setOccuredOn(Date occuredOn) {
            this.occuredOn = occuredOn;
        }

        @Override
        public String getDomainId() {
            return domainId;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 71 * hash + Objects.hashCode(this.state);
            hash = 71 * hash + Objects.hashCode(this.domainId);
            hash = 71 * hash + Objects.hashCode(this.occuredOn);
            return hash;
        }
        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final TestEvent other = (TestEvent) obj;
            if (!Objects.equals(this.state, other.state)) {
                return false;
            }
            if (!Objects.equals(this.domainId, other.domainId)) {
                return false;
            }
            if (!Objects.equals(this.occuredOn, other.occuredOn)) {
                return false;
            }
            return true;
        }
    }
}