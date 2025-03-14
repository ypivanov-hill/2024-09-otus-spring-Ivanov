package ru.otus.hw.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.ratelimiter.internal.InMemoryRateLimiterRegistry;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class Resilience4jConfig {

    @Autowired
    private RateLimiterRegistry inMemoryRateLimiterRegistry;

    @Bean
    public RateLimiterConfig rateLimiterConfig() {
        return RateLimiterConfig.custom()
                .timeoutDuration(Duration.ofMillis(100))
                .limitRefreshPeriod(Duration.ofSeconds(10))
                .limitForPeriod(5)
                .build();
    }

    @Bean(name = "defaultRateLimiterX")
    public RateLimiter rateLimiter(RateLimiterConfig config) {
        RateLimiter rateLimiter = RateLimiter.of("defaultRateLimiterX", config);
        inMemoryRateLimiterRegistry.replace("defaultRateLimiterX", rateLimiter);
        return rateLimiter;
    }

    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> defaultCustomizer() {
        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
                .timeLimiterConfig(TimeLimiterConfig.custom()
                        .timeoutDuration(Duration.ofMillis(100))
                        .build())
                .circuitBreakerConfig(CircuitBreakerConfig.ofDefaults())
                .build());
    }

    @Bean(name = "defaultCircuitBreaker")
    public CircuitBreaker circuitBreaker(CircuitBreakerFactory<?, ?> circuitBreakerFactory) {
        return circuitBreakerFactory.create("defaultCircuitBreaker");
    }
}
