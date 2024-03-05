package com.authentication.gw.api.gateway.routes.repository;

import com.authentication.gw.api.gateway.routes.model.ApiRouteReq;
import com.authentication.gw.api.gateway.routes.model.ApiRouteRes;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface GatewayCommonRepository {
    Flux<ApiRouteRes> findAllRoutes();
    Flux<ApiRouteRes> findAllRoutes(String serviceName);

    Mono<Long> saveApiService(String service, String uri);
    Mono<Long> saveApiRouteReqs(String service, List<ApiRouteReq> routes);
}
