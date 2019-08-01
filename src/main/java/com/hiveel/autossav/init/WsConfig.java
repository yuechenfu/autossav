package com.hiveel.autossav.init;

import com.hiveel.autossav.controller.websocket.handler.WsHandler;
import com.hiveel.autossav.controller.websocket.interceptor.AuthWsInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WsConfig implements WebSocketConfigurer {
    @Autowired
    private AuthWsInterceptor authWsInterceptor;
    @Autowired
    private WsHandler wsHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(wsHandler, "as/*").addInterceptors(authWsInterceptor).setAllowedOrigins("*");
    }
}
