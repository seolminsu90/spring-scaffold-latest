package com.authentication.gw.common.gateway;


import com.authentication.gw.common.error.ApiException;
import com.authentication.gw.common.model.ApiStatus;
import com.authentication.gw.common.model.SessionUserDetails;
import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Component
public class AuthenticationGatewayFilterFactory extends AbstractGatewayFilterFactory<AuthenticationGatewayFilterConfig> {
    @Override
    public GatewayFilter apply(AuthenticationGatewayFilterConfig config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            // Basic Logging
            log.info("Gateway Request Headers: {}", exchange.getRequest()
                                                            .getHeaders());
            log.info("Gateway Setting Config Arg: {}, {}, {}", config.getService(), config.getAuthority(),
                config.isPermitAll());

            // Route가 PermitAll일 경우 모든 사용자가 사용 가능
            if (config.isPermitAll()) return filterCaining(exchange, chain);

            // 이전 필터로부터 유저 정보를 읽어온다
            SessionUserDetails user = exchange.getAttribute("user");

            if (user == null) throw new ApiException(HttpStatus.UNAUTHORIZED, ApiStatus.UNAUTHORIZED);

            // 서비스 로그인 유저의 권한을 체크한다. 관리자는 모든 기능을 사용할 수 있다.
            if (user.isAdmin()) return filterCaining(exchange, chain, user);

            // 관리자가 아닌 경우 가진 권한을 체크한다.
            if (!user.hasAuthority(config.getAuthority()))
                throw new ApiException(HttpStatus.FORBIDDEN, ApiStatus.FORBIDDEN);


            return filterCaining(exchange, chain, user);
        };
    }

    private Mono<Void> filterCaining(ServerWebExchange exchange, GatewayFilterChain chain) {
        return filterCaining(exchange, chain, null);
    }

    private Mono<Void> filterCaining(ServerWebExchange exchange, GatewayFilterChain chain,
                                     @Nullable SessionUserDetails user) {
        return chain.filter(exchange)
                    .publishOn(Schedulers.boundedElastic())
                    .doOnSuccess(s -> {
                        // 토큰 검증 과정 중, 토큰 갱신이 필요할 경우 갱신 후 갱신된 Token을 전달해준다.
                        if (user != null && user.getToken() != null) {
                            var response = exchange.getResponse();
                            var headers = response.getHeaders();
                            headers.add("X-Refresh-Token", user.getToken());
                        }
                    });
    }

    // 게이트웨이 -> 서비스로 건내 줄 헤더가 필요한 경우
    private void addAuthorizationHeaders(ServerHttpRequest request) {
        request.mutate()
               // .header("X-Authorization-Id", tokenUser.getId())
               // .header("X-Authorization-Role", tokenUser.getRole())
               .build();
    }


}
