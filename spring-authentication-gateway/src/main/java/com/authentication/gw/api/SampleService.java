package com.authentication.gw.api;

import com.authentication.gw.common.error.ApiException;
import com.authentication.gw.common.model.ApiStatus;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class SampleService {

    private final WebClient webClient;

    public SampleService() {
        this.webClient = WebClient.create();
    }


    // 적용 되는 순서 Retry( CircuitBreaker( RateLimiter( TimeLimiter( Bulkhead( function)))))
    // @Retry(name = "sample-request", fallbackMethod = "fallBackCircuitTest")
    @CircuitBreaker(name = "gwCircuitBreaker", fallbackMethod = "fallback")
    public Mono<String> greeting() {
        return webClient.get().uri("http://localhost:8090/errortest").retrieve()
                        .onStatus(HttpStatusCode::is5xxServerError,
                            response -> Mono.error(new ApiException(HttpStatus.BAD_REQUEST, ApiStatus.BAD_REQUEST)))
                        .bodyToMono(String.class);
    }

    // Exception에 따라 분기한다.
    private Mono<String> fallback(ApiException e) {
        log.error("ApiException circuitBreakerFallback message: {}", e.getMessage());
        return Mono.just("fallbackApiException");
    }

    private Mono<String> fallback(Throwable t) {
        log.error("Throwable circuitBreakerFallback message: {}", t.getMessage());
        return Mono.just("fallbackException");
    }
}
