package com.authentication.gw.common.logging;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import reactor.core.publisher.Mono;

@Slf4j
public class LoggingResponseDecorator extends ServerHttpResponseDecorator {

    public LoggingResponseDecorator(ServerHttpResponse response) {
        super(response);
    }

    @Override
    public @NotNull Mono<Void> writeWith(@NotNull Publisher<? extends DataBuffer> body) {
        return super.writeWith(body).doOnTerminate(() -> log.info("Status {} ", getStatusCode()));
    }
}
