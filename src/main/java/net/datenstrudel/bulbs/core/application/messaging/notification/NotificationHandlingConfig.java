package net.datenstrudel.bulbs.core.application.messaging.notification;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


@Configuration
@ComponentScan(
        value = "net.datenstrudel.bulbs.core.application.messaging.notification.handling",
        excludeFilters = @ComponentScan.Filter(Configuration.class))
public class NotificationHandlingConfig {
}
