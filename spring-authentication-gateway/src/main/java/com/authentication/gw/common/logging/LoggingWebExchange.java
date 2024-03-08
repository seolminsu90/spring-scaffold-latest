package com.authentication.gw.common.logging;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebExchangeDecorator;

public class LoggingWebExchange extends ServerWebExchangeDecorator {
    private final ServerHttpRequestDecorator requestDecorator;
    private final ServerHttpResponseDecorator responseDecorator;

    public LoggingWebExchange(ServerWebExchange delegate) {
        super(delegate);
        this.requestDecorator = new LoggingRequestDecorator(delegate.getRequest());
        this.responseDecorator = new LoggingResponseDecorator(delegate.getResponse());
    }

    @Override
    public @NotNull ServerHttpRequest getRequest() {
        return this.requestDecorator;
    }

    @Override
    public @NotNull ServerHttpResponse getResponse() {
        return this.responseDecorator;
    }
}
