package com.authentication.gw.api.gateway.routes.repository;

import com.authentication.gw.api.gateway.routes.entity.ApiRoute;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ApiRouteRepository extends ReactiveCrudRepository<ApiRoute, Long>, GatewayCommonRepository {
    Mono<ApiRoute> findByRouteId(String routeId);
}
