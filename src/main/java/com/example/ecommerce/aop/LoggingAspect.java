package com.example.ecommerce.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    /**
     * Pointcut: log all service layer methods
     */
    @Pointcut("execution(* com.example.ecommerce.service..*(..))")
    public void serviceMethods() {}

    /**
     * Log before service method execution
     */
    @Before("serviceMethods()")
    public void logBefore(JoinPoint joinPoint) {
        log.info("[SERVICE CALL] {} -> args={}",
                joinPoint.getSignature().toShortString(),
                sanitize(joinPoint.getArgs()));
    }

    /**
     * Log after successful service execution
     */
    @AfterReturning(value = "serviceMethods()", returning = "result")
    public void logAfterReturn(JoinPoint joinPoint, Object result) {
        log.info("[SERVICE SUCCESS] {} -> return={}",
                joinPoint.getSignature().toShortString(),
                sanitize(result));
    }

    /**
     * Capture and log execution time
     */
    @Around("serviceMethods()")
    public Object logExecutionTime(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();

        Object result = pjp.proceed();

        long time = System.currentTimeMillis() - start;

        log.info("[EXECUTION TIME] {} -> {} ms",
                pjp.getSignature().toShortString(),
                time);

        return result;
    }

    /**
     * Log exceptions thrown in service layer
     */
    @AfterThrowing(value = "serviceMethods()", throwing = "ex")
    public void logException(JoinPoint joinPoint, Throwable ex) {
        log.error("[SERVICE ERROR] {} -> {}",
                joinPoint.getSignature().toShortString(),
                ex.getMessage(), ex);
    }


    /**
     * Removes sensitive data (passwords) from logs
     */
    private Object sanitize(Object value) {
        if (value == null) return null;

        // Avoid logging passwords in DTOs
        String str = value.toString().toLowerCase();

        if (str.contains("password")) {
            return "[HIDDEN]";
        }
        return value;
    }

    private Object sanitize(Object[] args) {
        if (args == null) return null;

        Object[] cleanArgs = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            cleanArgs[i] = sanitize(args[i]);
        }
        return cleanArgs;
    }
}
