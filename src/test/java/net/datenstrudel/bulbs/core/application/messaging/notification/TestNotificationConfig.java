package net.datenstrudel.bulbs.core.application.messaging.notification;

import net.datenstrudel.bulbs.core.application.messaging.eventStore.DomainEventStore;
import net.datenstrudel.bulbs.core.application.messaging.eventStore.PublishedMessageTrackerStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import static org.mockito.Mockito.mock;


@Configuration
public class TestNotificationConfig {

    @Bean
    @Primary
    public PublishedMessageTrackerStore notificationConfigMock(){
        return mock(PublishedMessageTrackerStore.class);
    }

    @Bean
    @Primary
    public DomainEventStore domainEventStoreMock(){
        return mock(DomainEventStore.class);
    }
}
