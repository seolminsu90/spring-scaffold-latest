package com.authentication.gw.common.logging;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import reactor.core.publisher.Flux;

import java.util.Optional;

import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
public class LoggingRequestDecorator extends ServerHttpRequestDecorator {
    private final StringBuilder cachedBody = new StringBuilder();

    public LoggingRequestDecorator(ServerHttpRequest request) {
        super(request);

        String path = request.getURI().getPath();
        String query = request.getURI().getQuery();
        String method = Optional.of(request.getMethod()).orElse(HttpMethod.GET).name();
        String headers = request.getHeaders().toString();
        log.info("RequestId: {}, {} {}\n {}", request.getId(), method, path + (query != null ? "?" + query : ""),
            headers);
    }

    @Override
    public Flux<DataBuffer> getBody() {
        return super.getBody().doOnNext(this::cache);
    }

    @SneakyThrows
    private void cache(DataBuffer buffer) {
        cachedBody.append(UTF_8.decode(buffer.asByteBuffer()));
    }

    public String getCachedBody() {
        return cachedBody.toString();
    }

}
