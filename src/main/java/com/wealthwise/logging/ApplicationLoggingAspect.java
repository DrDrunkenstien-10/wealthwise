package com.wealthwise.logging;

import org.apache.logging.log4j.ThreadContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ApplicationLoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationLoggingAspect.class);

    @Around("@within(org.springframework.stereotype.Service) " +
            "|| @within(org.springframework.stereotype.Component) " +
            "|| @within(org.springframework.stereotype.Repository) " +
            "|| @within(org.springframework.stereotype.Controller) " +
            "|| @within(org.springframework.web.bind.annotation.RestController)")
    public Object logServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        long startTime = System.currentTimeMillis();

        ThreadContext.put("class", className);
        ThreadContext.put("method", methodName);

        try {
            logger.info("START: {}.{}()", className, methodName);

            Object result = joinPoint.proceed();

            long duration = System.currentTimeMillis() - startTime;
            ThreadContext.put("duration", String.valueOf(duration));

            logger.info("END: {}.{}() duration={}ms", className, methodName, duration);

            return result;

        } catch (Throwable ex) {
            logger.error("EXCEPTION in {}.{}()", className, methodName, ex);
            throw ex;
        } finally {
            ThreadContext.remove("class");
            ThreadContext.remove("method");
            ThreadContext.remove("duration");
        }
    }
}
