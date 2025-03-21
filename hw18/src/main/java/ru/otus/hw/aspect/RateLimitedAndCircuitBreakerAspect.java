package ru.otus.hw.aspect;

import io.github.resilience4j.core.functions.CheckedSupplier;
import io.github.resilience4j.ratelimiter.RateLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Optional;

//@Aspect
//@Component
@RequiredArgsConstructor
@Slf4j
public class RateLimitedAndCircuitBreakerAspect {

    private final CircuitBreaker circuitBreaker;

    private final RateLimiter rateLimiter;

    @Around("@annotation(RateLimitedAndCircuitBreaker)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        CheckedSupplier<Object> restrictedSupplier = RateLimiter.decorateCheckedSupplier(rateLimiter, () ->
                circuitBreaker.run(() -> {
                    try {
                        return joinPoint.proceed();
                    } catch (Throwable t) {
                        log.error("method {} failed error:{}", method, t.getMessage());
                        throw new RuntimeException(t);
                    }
                }, t -> {
                    log.error("delay call  {}  failed error:{}", method, t.getMessage());
                    return  null;
                })
        );
        Object object = initObject(signature.getReturnType().getName());
        try {
            object = restrictedSupplier.get();
        } catch (Throwable t) {
            log.error("Failed {} to retrieve: {}", method, t.getMessage());
        }
        return object;
    }

    private Object initObject(String type) {
        Object o = null;

        if ("java.util.List".equals(type)) {
            o = Collections.emptyList();
        } else if ("java.util.Optional".equals(type)) {
            o = Optional.empty();
        }
        return o;
    }
}