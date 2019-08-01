package com.hiveel.autossav.controller.websocket.cache;

import com.hiveel.autossav.controller.websocket.util.CloseStatusUtil;
import com.hiveel.autossav.model.entity.Person;
import com.hiveel.core.log.util.LogUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AsSocketCache {

    private Map<Long, WebSocketSession> asToSessionMap = new ConcurrentHashMap<>();
    private Map<String, Long> sessionToMemberMap = new ConcurrentHashMap<>();

    public void save(WebSocketSession session, Person person) {
        Long id = person.getId();
        //单点登录，同时防止错误数据
        if (asToSessionMap.containsKey(id)) {
            WebSocketSession oldSession = asToSessionMap.get(id);
            int compare = session.getId().compareTo(oldSession.getId());
            if (compare < 0) {
                WebSocketSession temp = oldSession;
                oldSession = session;
                session = temp;
            }
            try {
                oldSession.close((CloseStatusUtil.loginByOtherEndpoint()));
            } catch (IOException e) {
                LogUtil.warn(id + "---" + person + "下线 失败 , 单点登录");
            }
            sessionToMemberMap.remove(oldSession.getId());
        }
        asToSessionMap.put(id, session);
        sessionToMemberMap.put(session.getId(), id);
    }

    public void remove(WebSocketSession session) {
        if (sessionToMemberMap.containsKey(session.getId())) {
            Long id = sessionToMemberMap.get(session.getId());
            asToSessionMap.remove(id);
        }
        sessionToMemberMap.remove(session.getId());
    }

    public void send(Person person,String content) throws IOException{
        WebSocketMessage message = new TextMessage(content.getBytes(Charset.forName("UTF-8")));
        WebSocketSession session = getAsToSessionMap().get(person.getId()) ;
        session.sendMessage(message);
    }

    public Map<Long, WebSocketSession> getAsToSessionMap() {
        return asToSessionMap;
    }

    public Map<String, Long> getSessionToMemberMap() {
        return sessionToMemberMap;
    }

}
