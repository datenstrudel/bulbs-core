package net.datenstrudel.bulbs.core;

import net.datenstrudel.bulbs.core.application.ApplicationLayerConfig;
import net.datenstrudel.bulbs.core.config.BulbsCoreConfig;
import net.datenstrudel.bulbs.core.security.config.SecurityConfig;
import net.datenstrudel.bulbs.core.util.IdentityUtlConfig;
import net.datenstrudel.bulbs.core.web.config.WebConfig;
import net.datenstrudel.bulbs.core.websocket.WebSocketConfig;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
        classes = {
                BulbsCoreApp.class,
                IdentityUtlConfig.class,
                SecurityConfig.class,
                BulbsCoreConfig.class,
                WebConfig.class,
                ApplicationLayerConfig.class,
                WebSocketConfig.class,
                IdentityUtlConfig.class,
        },
//        properties = "server.port=9092",
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public abstract class AbstractBulbsWebIT extends AbstractBulbsIT {

}
