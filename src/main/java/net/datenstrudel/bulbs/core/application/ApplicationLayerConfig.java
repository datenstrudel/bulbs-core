package net.datenstrudel.bulbs.core.application;

import net.datenstrudel.bulbs.core.application.messaging.notification.NotificationConfig;
import net.datenstrudel.bulbs.core.application.messaging.notification.NotificationHandlingConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

/**
 * Created by derTom on 18.07.2014.
 */
@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
@ComponentScan(excludeFilters = @ComponentScan.Filter(Configuration.class))
@Import(value = {
        NotificationConfig.class,
        NotificationHandlingConfig.class
})
public class ApplicationLayerConfig {
}
