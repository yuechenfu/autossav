package com.hiveel.autossav.controller.interceptor;

import com.google.gson.Gson;
import com.hiveel.autossav.model.entity.Person;
import com.hiveel.autossav.model.entity.PersonType;
import com.hiveel.core.model.rest.BasicRestCode;
import com.hiveel.core.model.rest.Rest;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class DrInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Person person = (Person) request.getAttribute("loginPerson");
        if (person.getType() != PersonType.DR && person.getType() != PersonType.DR_PRIVATE) {
            response.setContentType("application/json;charset=UTF-8");
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.getWriter().print(new Gson().toJson(Rest.createFail(BasicRestCode.UNAUTHORIZED)));
            return false;
        }
        return true;
    }

}
