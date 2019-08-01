package com.hiveel.autossav.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * 决定定时任务是否执行，避免在多节点部署时同时执行定时任务导致数据错乱
 */
@Component
@Aspect
@Profile({"dev", "co", "prod"})
public class JobRunableAspect {
    @Value("${autossav.runjob:false}")
    private boolean jobRun;

    @Pointcut("@annotation(org.springframework.scheduling.annotation.Scheduled)")
    public void processJobExec() {}

    @Around("processJobExec()")
    public void processException(ProceedingJoinPoint joinPoint) throws Throwable {
        if(!jobRun){
            return;
        }
        joinPoint.proceed();
    }
}
