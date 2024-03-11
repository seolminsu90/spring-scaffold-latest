package com.authentication.gw.common.gateway;


import com.authentication.gw.api.gateway.logs.entity.ApiLog;
import com.authentication.gw.api.gateway.logs.service.ApiLogService;
import com.authentication.gw.common.logging.LoggingRequestDecorator;
import com.authentication.gw.common.logging.LoggingWebExchange;
import com.authentication.gw.common.model.SessionUserDetails;
import com.authentication.gw.common.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.scheduler.Schedulers;

import java.util.Objects;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoggingGatewayFilterFactory extends AbstractGatewayFilterFactory<LoggingGatewayFilterFactory.Config> {

    private final ApiLogService apiLogService;

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            var request = exchange.getRequest();
            SessionUserDetails user = getAuthenticationFromHeader(request);
            if (user != null) exchange.getAttributes().put("user", user);

            return chain.filter(exchange).publishOn(Schedulers.boundedElastic())
                        .doOnTerminate(() -> apiLogService.saveApiLog(createLog(user, exchange))
                                                      .subscribeOn(Schedulers.boundedElastic())
                                                      .subscribe())
                        .then();
        };
    }

    private ApiLog createLog(SessionUserDetails user, ServerWebExchange exchange) {
        var request = exchange.getRequest();
        var response = exchange.getResponse();
        var loggingExchange = (LoggingWebExchange) exchange;
        var loggingRequestDecorator = (LoggingRequestDecorator) loggingExchange.getRequest();

        log.info("Request Body: {}", loggingRequestDecorator.getCachedBody());
        log.info("Response Status: {}", response.getStatusCode());

        String txId = String.valueOf(UUID.randomUUID());
        return ApiLog.builder().txid(txId).username(user == null ? "" : user.getUsername())
                     .uri(request.getURI().toString()).method(request.getMethod().toString())
                     .code(Objects.requireNonNull(response.getStatusCode()).value())
                     .params(loggingRequestDecorator.getCachedBody()).build();
    }

    private SessionUserDetails getAuthenticationFromHeader(ServerHttpRequest request) {
        String token = request.getHeaders()
                              .getFirst("Authorization");

        if (token == null) return null;
        if (token.startsWith("Bearer")) token = token.replace("Bearer ", "");

        return JWTUtil.checkToken(token);
    }

    public static class Config {
    }
}
