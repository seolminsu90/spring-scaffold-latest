package com.authentication.gw.common.security;

import com.authentication.gw.common.util.JWTUtil;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class AuthenticationTokenProcessingFilter implements WebFilter {

    @Override
    public @NotNull Mono<Void> filter(ServerWebExchange exchange, @NotNull WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String authenticationStr = getAuthentication(request);

        if (StringUtils.hasText(authenticationStr)) {
            Authentication authentication = validateTokenAndGetUserDetails(authenticationStr);
            if (authentication == null) return chain.filter(exchange);

            return chain.filter(exchange)
                        .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
        }

        return chain.filter(exchange);
    }

    private String getAuthentication(ServerHttpRequest request) {
        String token = request.getHeaders()
                              .getFirst("Authorization");

        if (token == null) return null;
        if (token.startsWith("Bearer")) token = token.replace("Bearer ", "");
        return token;
    }

    private Authentication validateTokenAndGetUserDetails(String token) {
        UserDetails user = JWTUtil.checkToken(token);
        if (user == null) return null;
        return new UsernamePasswordAuthenticationToken(user, token, user.getAuthorities());
    }
}
