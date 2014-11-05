package net.datenstrudel.bulbs.core.application.messaging.notification;

import net.datenstrudel.bulbs.core.application.messaging.eventStore.DomainEventStore;
import net.datenstrudel.bulbs.core.application.messaging.eventStore.PublishedMessageTrackerStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.easymock.EasyMock.createNiceMock;

@Configuration
public class TestNotificationConfig {

    @Bean
    public PublishedMessageTrackerStore notificationConfigMock(){
        return createNiceMock(PublishedMessageTrackerStore.class);
    }
    @Bean
    public DomainEventStore domainEventStoreMock(){
        return createNiceMock(DomainEventStore.class);
    }
}
