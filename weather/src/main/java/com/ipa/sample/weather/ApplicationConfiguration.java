package com.ipa.sample.weather;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.circuitbreaker.springretry.SpringRetryCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.springretry.SpringRetryConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.policy.AlwaysRetryPolicy;

import java.time.Duration;

@Configuration
public class ApplicationConfiguration {

    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> globalBreakerFactory() {
        return f -> f.configureDefault(id -> new Resilience4JConfigBuilder(id)
                .timeLimiterConfig(TimeLimiterConfig.custom().timeoutDuration(Duration.ofSeconds(10)).build())
                .circuitBreakerConfig(CircuitBreakerConfig.ofDefaults()).build());
    }

    @Bean
    public Customizer<SpringRetryCircuitBreakerFactory> globalRetryBreakerFactory() {
        return f -> f.configureDefault(id -> new SpringRetryConfigBuilder(id)
                .retryPolicy(new AlwaysRetryPolicy())
                .build());
    }
}
