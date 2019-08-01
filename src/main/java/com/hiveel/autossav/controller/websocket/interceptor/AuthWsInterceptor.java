package com.hiveel.autossav.controller.websocket.interceptor;

import com.google.common.base.Strings;
import com.hiveel.auth.sdk.model.Account;
import com.hiveel.autossav.manager.AuthManager;
import com.hiveel.autossav.model.entity.Person;
import com.hiveel.autossav.service.PersonService;
import com.hiveel.core.debug.DebugSetting;
import com.hiveel.core.log.util.LogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
public class AuthWsInterceptor implements HandshakeInterceptor {
    @Autowired
    private AuthManager authManager;
    @Autowired
    private PersonService personService;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        String path = request.getURI().getPath();
        String token = path.substring(path.lastIndexOf('/') +1);
        Account account = authManager.findAccountByToken("Bearer "+token);
        if (account.isNull()) {
            LogUtil.error("开启webscoket失败, token 不存在 token:" + Strings.nullToEmpty(token));
            return false;
        }
        Long id = Long.valueOf(account.getPersonId());
        Person person = personService.findById(new Person.Builder().set("id", id).build());
        attributes.put("loginPerson",person);
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }
}
