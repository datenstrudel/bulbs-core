package net.datenstrudel.bulbs.core.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.server.standard.TomcatRequestUpgradeStrategy;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.util.List;

@Configuration
@ComponentScan(
        basePackages = {
                "net.datenstrudel.bulbs.core.websocket"
        },
        excludeFilters = @ComponentScan.Filter(Configuration.class))
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    CounterService counterService;

    @Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/core/websockets").withSockJS();
	}
	@Override
	public void configureMessageBroker(MessageBrokerRegistry configurer) {
		configurer.enableSimpleBroker("/topic/");
//		configurer.enableStompBrokerRelay("/queue/", "/topic/");
//		configurer.setApplicationDestinationPrefixes("/wsClient");
		configurer.setApplicationDestinationPrefixes("/core");
	}

    @Override
    public void configureClientInboundChannel(ChannelRegistration reg) {
        reg.setInterceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                counterService.increment("ws.inbound.receivedButNotProcessed");
                return message;
            }

            @Override
            public void postSend(Message<?> message, MessageChannel channel, boolean sent) {

            }

            @Override
            public void afterSendCompletion(Message<?> message, MessageChannel channel, boolean sent, Exception ex) {

            }

            @Override
            public boolean preReceive(MessageChannel channel) {
                return false;
            }

            @Override
            public Message<?> postReceive(Message<?> message, MessageChannel channel) {
                return message;
            }

            @Override
            public void afterReceiveCompletion(Message<?> message, MessageChannel channel, Exception ex) {

            }
        });
        reg.taskExecutor().corePoolSize(1);
        reg.taskExecutor().maxPoolSize(2);
        reg.taskExecutor().queueCapacity(180);
    }

    @Override
    public void configureClientOutboundChannel(ChannelRegistration reg) {
        reg.taskExecutor().corePoolSize(1);
        reg.taskExecutor().maxPoolSize(2);
        reg.taskExecutor().queueCapacity(180);
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
        registry.setSendTimeLimit(2500);
    }

    @Bean
    public DefaultHandshakeHandler handshakeHandler() {
        TomcatRequestUpgradeStrategy requestUpgradeStrategy = new TomcatRequestUpgradeStrategy();
        return new DefaultHandshakeHandler(
                requestUpgradeStrategy);
    }

    @Override
    public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
        messageConverters.add(new DtoWsMessageJsonAdapter());
        return false;
    }

}