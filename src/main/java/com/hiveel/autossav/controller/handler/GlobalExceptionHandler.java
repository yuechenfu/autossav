package com.hiveel.autossav.controller.handler;

import com.hiveel.core.debug.DebugSetting;
import com.hiveel.core.exception.ParameterException;
import com.hiveel.core.exception.UnauthorizationException;
import com.hiveel.core.exception.type.RestCodeException;
import com.hiveel.core.log.util.LogUtil;
import com.hiveel.core.model.rest.BasicRestCode;
import com.hiveel.core.model.rest.Rest;
import com.hiveel.core.model.rest.RestCode;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = Exception.class)
    public Rest<String> all(HttpServletRequest request, Exception e) {
        RestCode code = e instanceof RestCodeException ? ((RestCodeException) e).getCode() : BasicRestCode.FAIL;
        Rest<String> rest = Rest.createFail(code);
        String message = LogUtil.getMessageAndLog(e);
        if (DebugSetting.debug) {
            rest.setMessage(message);
        }
        return rest;
    }

    @ExceptionHandler(UnauthorizationException.class)
    public Rest<String> unauth(HttpServletRequest request, UnauthorizationException e) {
        Rest<String> rest = Rest.createFail(BasicRestCode.UNAUTHORIZED);
        String message = LogUtil.getMessageAndLog(e);
        if (DebugSetting.debug) {
            rest.setMessage(message);
        }
        return rest;
    }

    @ExceptionHandler({ParameterException.class, BindException.class})
    public Rest<String> param(HttpServletRequest request, Exception e) {
        Rest<String> rest = Rest.createFail(BasicRestCode.PARAMETER);
        String message = LogUtil.getMessageAndLog(e);
        if (DebugSetting.debug) {
            rest.setMessage(message);
        }
        return rest;
    }
}
