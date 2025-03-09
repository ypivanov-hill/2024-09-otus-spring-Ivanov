package ru.otus.hw.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RateLimitedAndCircuitBreaker {
    String rateLimiterName() default "defaultRateLimiter";
    String circuitBreakerName() default "defaultCircuitBreaker";
}