package net.datenstrudel.bulbs.core;

import net.datenstrudel.bulbs.core.application.ApplicationLayerConfig;
import net.datenstrudel.bulbs.core.config.BulbsCoreConfig;
import net.datenstrudel.bulbs.core.security.config.SecurityConfig;
import net.datenstrudel.bulbs.core.web.config.SwaggerConfig;
import net.datenstrudel.bulbs.core.web.config.WebConfig;
import net.datenstrudel.bulbs.core.websocket.WebSocketConfig;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author Thomas Wendzinski
 */
@ContextConfiguration(
        classes = {
                BulbsCoreConfig.class,
                ApplicationLayerConfig.class,
                SecurityConfig.class,
                WebConfig.class,
                WebSocketConfig.class,
                SwaggerConfig.class,

                TestConfig.class
        }
)
@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application-itest.properties")
public abstract class AbstractBulbsIT {
}
