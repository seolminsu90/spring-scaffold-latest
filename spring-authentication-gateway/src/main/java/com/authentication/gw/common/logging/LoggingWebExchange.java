package com.authentication.gw.common.logging;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebExchangeDecorator;

public class LoggingWebExchange extends ServerWebExchangeDecorator {
    private final LoggingRequestDecorator requestDecorator;
    private final LoggingResponseDecorator responseDecorator;

    public LoggingWebExchange(ServerWebExchange delegate) {
        super(delegate);
        this.requestDecorator = new LoggingRequestDecorator(delegate.getRequest());
        this.responseDecorator = new LoggingResponseDecorator(this.requestDecorator, delegate.getResponse());
    }

    @Override
    public ServerHttpRequest getRequest() {
        return this.requestDecorator;
    }

    @Override
    public ServerHttpResponse getResponse() {
        return this.responseDecorator;
    }
}
