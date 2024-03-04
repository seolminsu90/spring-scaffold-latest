package com.authentication.gw.common.gateway;

import com.authentication.gw.api.gateway.model.ApiRouteRes;
import com.authentication.gw.api.gateway.service.ApiRouteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class GatewayConfig {
    private final AuthenticationGatewayFilterFactory authenticationGatewayFilterFactory;
    private final LoggingGatewayFilterFactory loggingGatewayFilterFactory;
    private final GatewayRoutesRefresher gatewayRoutesRefresher;

    @Bean
    @RefreshScope
    public RouteLocator routeLocator(ApiRouteService routeService, RouteLocatorBuilder routeLocationBuilder) {
        log.info("GatewayConfig is initialized.");

        RouteLocatorBuilder.Builder builder = routeLocationBuilder.routes();

        routeService.getAll()
                    .doOnComplete(gatewayRoutesRefresher::refreshRoutes)
                    .subscribe(apiRoute -> builder.route(
                        apiRoute.getRouteId(),
                        route -> setPredicateSpec(apiRoute, route)
                    ));

        return builder.build();
    }

    private Buildable<Route> setPredicateSpec(ApiRouteRes apiRoute, PredicateSpec predicateSpec) {
        AuthenticationGatewayFilterConfig config = new AuthenticationGatewayFilterConfig(apiRoute.getRouteId(),
            apiRoute.getService(), apiRoute.isPermitAll());
        LoggingGatewayFilterFactory.Config defaultConfig = new LoggingGatewayFilterFactory.Config();

        // Route path /[serviceName]/[serviceApiPath] 로 지정
        String serviceName = apiRoute.getService();
        String servicePath = "/" + serviceName + apiRoute.getPath(); // getPath는 /로 시작하게 할것

        BooleanSpec booleanSpec = predicateSpec.path(servicePath);
        if (StringUtils.hasLength(apiRoute.getMethod())) {
            booleanSpec.and()
                       .method(apiRoute.getMethod());
        }

        // /[serviceName]/~~ 을 통한 요청을 실제 서비스의 /~~ 요청으로 rewrite 해준다.
        String rewriteRegex = String.format("/%s/(?<segment>.*)", serviceName);

        UriSpec uriSpec = booleanSpec.filters(f -> f
            .rewritePath(rewriteRegex, "/$\\{segment}")
            .filter(loggingGatewayFilterFactory.apply(defaultConfig), -1)
            .filter(authenticationGatewayFilterFactory.apply(config)));
        return uriSpec.uri(apiRoute.getUri());
    }
}
