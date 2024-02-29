package com.authentication.gw.api.gateway.repository;

import com.authentication.gw.api.gateway.model.ApiRouteReq;
import com.authentication.gw.api.gateway.model.ApiRouteRes;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface GatewayCommonRepository {
    Flux<ApiRouteRes> findAllRoutes();

    Mono<Long> saveApiService(String service, String uri);
    Mono<Long> saveApiRouteReqs(String service, List<ApiRouteReq> routes);
}
