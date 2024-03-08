package com.authentication.gw.common.logging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;

@Slf4j
public class LoggingResponseDecorator extends ServerHttpResponseDecorator {

    public LoggingResponseDecorator(ServerHttpResponse response) {
        super(response);
        log.info("Status {} ", response.getStatusCode());
    }
}
