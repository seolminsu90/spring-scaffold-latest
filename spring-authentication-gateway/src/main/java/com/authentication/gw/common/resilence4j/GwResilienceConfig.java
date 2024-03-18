package com.authentication.gw.common.resilence4j;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class GwResilienceConfig {
    // GW 내부 API에서 사용할 서킷 브레이커 정의
    @Bean("gwCircuitBreaker")
    public CircuitBreakerRegistry gwCircuitBreakerRegistry() {
        return CircuitBreakerRegistry.of(
            CircuitBreakerConfig.custom()
                                .waitDurationInOpenState(Duration.ofMillis(10000))
                                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                                .slidingWindowSize(4)
                                .failureRateThreshold(50)
                                .build());
    }

}
