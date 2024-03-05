package com.authentication.gw.common.gateway;


import com.authentication.gw.api.gateway.logs.entity.ApiLog;
import com.authentication.gw.api.gateway.logs.service.ApiLogService;
import com.authentication.gw.common.model.SessionUserDetails;
import com.authentication.gw.common.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
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
            log.info("Request URI: {}", request.getURI());
            log.info("Request Method: {}", request.getMethod());
            log.info("Request Headers: {}", request.getHeaders());

            SessionUserDetails user = getAuthenticationFromHeader(request);
            if (user != null) {
                exchange.getAttributes()
                        .put("user", user);
            }

            String txid = String.valueOf(UUID.randomUUID());

            var requestLog = ApiLog.builder()
                                   .txid(txid)
                                   .username(user == null ? "" : user.getUsername())
                                   .uri(request.getURI()
                                               .toString())
                                   .method(request.getMethod()
                                                  .toString())
                                   .build();

            apiLogService.saveApiLog(requestLog)
                         .subscribe();

            return chain.filter(exchange)
                        .publishOn(Schedulers.boundedElastic())
                        .doOnSuccess(s -> {
                            var response = exchange.getResponse();
                            log.info("Response Status: {}", response.getStatusCode());

                            apiLogService.updateApiLogByTxid(txid, Objects.requireNonNull(response.getStatusCode())
                                                                          .value())
                                         .subscribe();
                        });
        };
    }

    private SessionUserDetails getAuthenticationFromHeader(ServerHttpRequest request) {
        String token = request.getHeaders()
                              .getFirst("Authorization");

        if (token == null) return null;
        if (token.startsWith("Bearer")) {
            token = token.replace("Bearer ", "");
        }

        return JWTUtil.checkToken(token);
    }

    public static class Config {
    }
}
