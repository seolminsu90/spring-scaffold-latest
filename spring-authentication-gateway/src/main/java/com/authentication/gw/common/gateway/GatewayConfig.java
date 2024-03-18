package com.authentication.gw.common.gateway;

import com.authentication.gw.api.gateway.routes.model.ApiRouteRes;
import com.authentication.gw.api.gateway.routes.service.ApiRouteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.factory.RetryGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.SpringCloudCircuitBreakerFilterFactory;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.util.function.Consumer;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class GatewayConfig {
    private final AuthenticationGatewayFilterFactory authenticationGatewayFilterFactory;
    private final LoggingGatewayFilterFactory loggingGatewayFilterFactory;
    private final GatewayRoutesRefresher gatewayRoutesRefresher;
    private final Consumer<RetryGatewayFilterFactory.RetryConfig> resilienceRetry;
    private final Consumer<SpringCloudCircuitBreakerFilterFactory.Config> resilienceCircuit;

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


        String serviceName = apiRoute.getService().toUpperCase(); // Eureka Default Service => UPPERCASE 라서 맞추기
        String servicePath = "/" + serviceName + apiRoute.getPath();

        BooleanSpec booleanSpec = predicateSpec.path(servicePath);
        if (StringUtils.hasLength(apiRoute.getMethod()) && !apiRoute.getMethod().equals("*")) { // * === All Allow
            booleanSpec.and().method(apiRoute.getMethod(), "HEAD", "OPTIONS");
        }


        String rewriteRegex = String.format("/%s/(?<segment>.*)", serviceName);

        UriSpec uriSpec = booleanSpec.filters(f -> f
            .rewritePath(rewriteRegex, "/$\\{segment}")
            .filter(loggingGatewayFilterFactory.apply(defaultConfig), -1)
            .filter(authenticationGatewayFilterFactory.apply(config))
            .retry(resilienceRetry)
            .circuitBreaker(resilienceCircuit)
        );
        return uriSpec.uri(apiRoute.getUri());
    }
}
