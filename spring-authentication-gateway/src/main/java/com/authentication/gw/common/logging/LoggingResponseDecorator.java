package com.authentication.gw.common.logging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import reactor.core.publisher.Mono;

@Slf4j
public class LoggingResponseDecorator extends ServerHttpResponseDecorator {

    public LoggingResponseDecorator(ServerHttpRequest request, ServerHttpResponse response) {
        super(response);

        getBodyAsString(request)
            .subscribe(s -> {
                log.info("RequestId: {}, Status {} ", request.getId(), response.getStatusCode());
                log.info("RequestBody :: {}", s);
            });
    }

    public Mono<String> getBodyAsString(ServerHttpRequest request) {
        return Mono.just(((LoggingRequestDecorator) request).getCachedBody());
    }
}
