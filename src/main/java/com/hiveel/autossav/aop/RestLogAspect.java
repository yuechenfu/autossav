package com.hiveel.autossav.aop;

import com.google.gson.Gson;
import com.hiveel.core.log.util.LogUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

@Component
@Aspect
public class RestLogAspect {
    private static Logger logger = LoggerFactory.getLogger(RestLogAspect.class);

    @Pointcut("@annotation(org.springframework.web.bind.annotation.GetMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PostMapping)  || " +
            "@annotation(org.springframework.web.bind.annotation.PutMapping ) || " +
            "@annotation(org.springframework.web.bind.annotation.DeleteMapping ) ")
    public void processRestRequest() {
    }

    @Around("processRestRequest()")
    public Object processException(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        Long startTime = System.currentTimeMillis();
        StringBuilder stbHeader = new StringBuilder();
        StringBuilder stbParam = new StringBuilder();
        StringBuilder logStrBuff = new StringBuilder();
        if (request != null) {
            Enumeration<?> enu = request.getParameterNames();
            while (enu.hasMoreElements()) {
                String name = (String) enu.nextElement();
                String value = request.getParameter(name);
                if (value != null) {
                    value = value.replaceAll(0x0A + "", "");// 去掉换行符
                    value = value.replaceAll(0x0D + "", "");// 去掉回车符
                    stbParam.append(name).append("=").append(value).append(";");
                }
            }
            enu = request.getHeaderNames();
            while (enu.hasMoreElements()) {
                String name = (String) enu.nextElement();
                String value = request.getHeader(name);
                if (value != null) {
                    stbHeader.append(name).append("=").append(value).append(";");
                }
            }
            String methodName = request.getRequestURI();
            String requestMethod = request.getMethod();
            logStrBuff.append("requestMethod:[" + requestMethod + "]\t");
            logStrBuff.append("requestPath:[" + methodName + "]\t");
            logStrBuff.append("requestHeaders:[" + stbHeader.toString() + "]\t");
            logStrBuff.append("requestParams:[" + stbParam.toString() + "]\t");
            logStrBuff.append("connectIp:[" + request.getRemoteAddr() + "]\t");
        }
        Object object = null;
        try {
            object = joinPoint.proceed();
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            String msg = e.getMessage();
            logStrBuff.append("\t timeCosted:[" + (endTime - startTime) + "ms]\t");
            logStrBuff.append("exception occur-:" + msg);
            throw e;
        }finally {
            long endTime = System.currentTimeMillis();
            String msg = new Gson().toJsonTree(object).toString();
            logStrBuff.append("result--->>:" + msg);
            logStrBuff.append("\t timeCosted:[" + (endTime - startTime) + "ms]\t");
            logger.info(logStrBuff.toString());
        }

        return object;
    }
}
