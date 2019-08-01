package com.hiveel.autossav.controller.websocket.handler;

import com.hiveel.autossav.controller.websocket.cache.AsSocketCache;
import com.hiveel.autossav.controller.websocket.model.ConnectionCode;
import com.hiveel.autossav.controller.websocket.util.CloseStatusUtil;
import com.hiveel.autossav.model.entity.Person;
import com.hiveel.core.debug.DebugSetting;
import com.hiveel.core.log.util.LogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class WsHandler extends TextWebSocketHandler {
    @Autowired
    private AsSocketCache asSocketCache;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Person person = (Person)session.getAttributes().get("loginPerson");
        if (person == null) {
            throw new IllegalAccessException("without passing AuthWsInterceptor");
        }
        asSocketCache.save(session, person);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        if (ConnectionCode.HEARTBEAT.toString().equals(message.getPayload())) {
            if (DebugSetting.debug) {
                LogUtil.debug("进行心跳: " + asSocketCache.getSessionToMemberMap().get(session.getId()).toString());
            }
            session.sendMessage(new TextMessage(ConnectionCode.HEARTBEAT.toString()));
        }
        if (ConnectionCode.LOGOUT.toString().equals(message)) {
            session.close(CloseStatusUtil.logout());
        }
        if (ConnectionCode.MINIMIZE.toString().equals(message)) {
            session.close(CloseStatusUtil.minimize());
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        if (DebugSetting.debug) {
            exception.printStackTrace();
        }
        LogUtil.error("websocket连接出错, 来自[" + session + "], 的异常: ", exception);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        asSocketCache.remove(session);
        if (DebugSetting.debug) {
            LogUtil.debug("close:" + session.getId());
            LogUtil.debug("Closing a WebSocket due to " + closeStatus.getReason());
        }
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
