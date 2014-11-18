package net.datenstrudel.bulbs.core.websocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.server.standard.TomcatRequestUpgradeStrategy;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.util.List;
//import org.springframework.web.socket.messaging.config.StompEndpointRegistry;
//import org.springframework.web.socket.messaging.config.WebSocketMessageBrokerConfigurer;

@Configuration
@ComponentScan(
        basePackages = {
                "net.datenstrudel.bulbs.core.websocket"
        },
        excludeFilters = @ComponentScan.Filter(Configuration.class))
@EnableWebSocketMessageBroker
//@EnableWebSocket
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    
	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
//        SockJsServiceRegistration registration = 
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
        reg.taskExecutor().corePoolSize(2);
        reg.taskExecutor().maxPoolSize(5);
        reg.taskExecutor().queueCapacity(30);
    }

    @Override
    public void configureClientOutboundChannel(ChannelRegistration reg) {
        reg.taskExecutor().corePoolSize(2);
        reg.taskExecutor().maxPoolSize(5);
        reg.taskExecutor().queueCapacity(30);
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
    }

    @Bean
    public DefaultHandshakeHandler handshakeHandler() {
        TomcatRequestUpgradeStrategy requestUpgradeStrategy = new TomcatRequestUpgradeStrategy();
        return new DefaultHandshakeHandler(
                requestUpgradeStrategy);
    }
//    @Bean
//    public ServletServerContainerFactoryBean createWebSocketContainer() {
//        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
//        container.setMaxTextMessageBufferSize(8192);
//        container.setMaxBinaryMessageBufferSize(8192);
//        return container;
//    }



    @Override
    public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
        messageConverters.add(new DtoWsMessageJsonAdapter());
        return false;
    }
    
    
    
    
}