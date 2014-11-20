package net.datenstrudel.bulbs.core.application.messaging.notification;

import net.datenstrudel.bulbs.core.application.messaging.notification.infrastructure.NotificationServiceRabbitMq;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ChannelListener;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PreDestroy;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Thomas Wendzinski
 */
@Configuration
@EnableScheduling
@ComponentScan(value = "net.datenstrudel.bulbs.core.application.messaging.notification.infrastructure")
public class NotificationConfig {

    //~ Member(s) //////////////////////////////////////////////////////////////
    private static final Logger log = LoggerFactory.getLogger(NotificationConfig.class);

    private CachingConnectionFactory connectionFactory;
    //~ Construction ///////////////////////////////////////////////////////////
    @Bean
    public ConnectionFactory rabbitConnectionFactory(
            @Value("${rabbitMq.connection.host}") String host,
            @Value("${rabbitMq.connection.port}") Integer port,
            @Value("${rabbitMq.connection.username}") String username,
            @Value("${rabbitMq.connection.password}") String password
    ) {
        CachingConnectionFactory connectionFactory =
            new CachingConnectionFactory(host, port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setChannelCacheSize(1);
        List<ChannelListener> listeners = new LinkedList<>();
        listeners.add(
                (channel, transactional) -> log.info("Channel created; Number:  " + channel.getChannelNumber())
        );
        connectionFactory.setChannelListeners(listeners);
        this.connectionFactory = connectionFactory;
        return connectionFactory;
    }
    
    @Bean 
    public NotificationServiceRabbitMq coreInternalNotificationService(){
        return new NotificationServiceRabbitMq(Exchanges.EXCHANGE_TOPIC__BUBLS_CORE);
    }

    @PreDestroy
    public void destroy(){
        log.info("Going to destroy AMQP connection factory..");
        this.connectionFactory.destroy();
    }

    //~ Method(s) //////////////////////////////////////////////////////////////
    //~ Private Artifact(s) ////////////////////////////////////////////////////

}
