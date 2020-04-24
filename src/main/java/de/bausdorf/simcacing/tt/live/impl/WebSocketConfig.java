package de.bausdorf.simcacing.tt.live.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private TeamTacticsBroadcaster broadcaster;

    public WebSocketConfig(@Autowired TeamTacticsBroadcaster broadcaster) {
        this.broadcaster = broadcaster;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        broadcaster.setBrokerRegistry(config);
        config.enableSimpleBroker("/live");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/liveclient");
        registry.addEndpoint("/liveclient").withSockJS();
    }
}