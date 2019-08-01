package com.hiveel.autossav.controller.interceptor;

import com.google.gson.Gson;
import com.hiveel.auth.sdk.model.Account;
import com.hiveel.autossav.manager.AuthManager;
import com.hiveel.autossav.model.entity.Person;
import com.hiveel.autossav.service.PersonService;
import com.hiveel.core.model.rest.BasicRestCode;
import com.hiveel.core.model.rest.Rest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AuthInterceptor extends HandlerInterceptorAdapter {
    @Autowired
    private PersonService personService;
    @Autowired
    private AuthManager authManager;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            setResponse(response);
            return false;
        }
        Account account = authManager.findAccountByToken(token);
        if(account.isNull()){
            setResponse(response);
            return false;
        }
        Person person = personService.findById(new Person.Builder().set("id", Long.valueOf(account.getPersonId()) ).build());
        request.setAttribute("loginPerson",person);
        return true;
    }

    private void setResponse(HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Headers", "*, authorization");
        response.getWriter().print(new Gson().toJson(Rest.createFail(BasicRestCode.UNAUTHORIZED)));
    }

    //预留给测试代码替换authManager
    public void setAuthManager(AuthManager authManager) {
        this.authManager = authManager;
    }
}
