package com.authentication.gw.common.resilence4j;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.cloud.gateway.filter.factory.RetryGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.SpringCloudCircuitBreakerFilterFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

import java.time.Duration;
import java.util.Collections;
import java.util.function.Consumer;

@Configuration
public class GwFilterResilienceConfig {
    @Bean("resilienceRetry")
    public Consumer<RetryGatewayFilterFactory.RetryConfig> retryConfig() {
        return retryConfig -> {
            retryConfig.setRetries(1);

            var backoffConfig = new RetryGatewayFilterFactory.BackoffConfig();
            backoffConfig.setFirstBackoff(Duration.ofMillis(100));
            backoffConfig.setMaxBackoff(Duration.ofSeconds(5));
            backoffConfig.setFactor(2);
            backoffConfig.setBasedOnPreviousValue(false);

            retryConfig.setBackoff(backoffConfig);
            retryConfig.setStatuses(HttpStatus.BAD_GATEWAY);

        };
    }

    @Bean("resilienceCircuit")
    public Consumer<SpringCloudCircuitBreakerFilterFactory.Config> circuitBreakerConfig() {
        return conf -> conf
            .setName("customCircuitBreakerCustomizer")
            .setFallbackUri("/fallback")
            .setStatusCodes(Collections.singleton(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value())));
    }

    @Bean
    public Customizer<ReactiveResilience4JCircuitBreakerFactory> defaultCircuitBreakerCustomizer() {
        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
            .circuitBreakerConfig(CircuitBreakerConfig.custom()
                                                      .waitDurationInOpenState(Duration.ofMillis(10000))
                                                      .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                                                      .slidingWindowSize(10)
                                                      .failureRateThreshold(90)
                                                      .build())
            .build());
    }

    // GW Filter에 주입할 서킷 브레이커 팩토리
    @Bean
    public Customizer<ReactiveResilience4JCircuitBreakerFactory> customCircuitBreakerCustomizer() {
        return factory -> factory.configure(builder ->
                builder.circuitBreakerConfig(
                           CircuitBreakerConfig.custom()
                                               .waitDurationInOpenState(Duration.ofMillis(10000))
                                               .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                                               .slidingWindowSize(4)
                                               .failureRateThreshold(50)
                                               .build())
                       .build(),
            "customCircuitBreakerCustomizer");
    }
}
