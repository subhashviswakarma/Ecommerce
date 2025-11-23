package com.example.ecommerce.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LoggingAspect {

    private final ObjectMapper mapper;

    /**
     * Pointcut for all controller methods
     */
    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void controllerLayer() {}

    /**
     * Pointcut for all service methods
     */
    @Pointcut("within(@org.springframework.stereotype.Service *)")
    public void serviceLayer() {}

    /**
     * Log controller method entry
     */
    @Before("controllerLayer()")
    public void logControllerInput(JoinPoint joinPoint) {
        try {
            Object[] args = joinPoint.getArgs();
            log.info("[API CALL] {} -> args = {}",
                    joinPoint.getSignature(),
                    mapper.writeValueAsString(args));
        } catch (Exception e) {
            log.warn("[API CALL] Failed to log input for {}", joinPoint.getSignature());
        }
    }

    /**
     * Log controller responses
     */
    @AfterReturning(value = "controllerLayer()", returning = "result")
    public void logControllerOutput(JoinPoint joinPoint, Object result) {
        try {
            log.info("[API RESPONSE] {} -> result = {}",
                    joinPoint.getSignature(),
                    mapper.writeValueAsString(result));
        } catch (Exception e) {
            log.warn("[API RESPONSE] Failed to log output for {}", joinPoint.getSignature());
        }
    }

    /**
     * Measure execution time of all service methods
     */
    @Around("serviceLayer()")
    public Object logExecutionTime(ProceedingJoinPoint pjp) throws Throwable {

        long start = System.currentTimeMillis();
        Object result = pjp.proceed();
        long duration = System.currentTimeMillis() - start;

        log.info("[SERVICE] {} executed in {} ms",
                pjp.getSignature(),
                duration);

        return result;
    }

    /**
     * Log exceptions thrown by any service method
     */
    @AfterThrowing(value = "serviceLayer()", throwing = "ex")
    public void logServiceException(JoinPoint joinPoint, Throwable ex) {
        log.error("[SERVICE ERROR] {} -> {}",
                joinPoint.getSignature(),
                ex.getMessage(),
                ex);
    }
}
