package ru.santurov.paceopp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/websocket").setAllowedOrigins("http://localhost", "http://83.166.235.56").withSockJS();
        registry.addEndpoint("/auth/stompEndpoint").setAllowedOrigins("http://localhost", "http://83.166.235.56").withSockJS();
        registry.addEndpoint("/stompEndpoint").setAllowedOrigins("http://localhost", "http://83.166.235.56").withSockJS();
    }
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.setApplicationDestinationPrefixes("/app");
        config.enableSimpleBroker("/topic", "/queue");
    }

}