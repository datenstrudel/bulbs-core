package net.datenstrudel.bulbs.core.application.messaging.notification;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Created by derTom on 29.07.2014.
 */
@Configuration
@ComponentScan(
        value = "net.datenstrudel.bulbs.core.application.messaging.notification.handling",
        excludeFilters = @ComponentScan.Filter(Configuration.class))
public class NotificationHandlingConfig {
}
