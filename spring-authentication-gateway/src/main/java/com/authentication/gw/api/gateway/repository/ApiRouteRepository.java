package com.authentication.gw.api.gateway.repository;

import com.authentication.gw.api.gateway.entity.ApiRoute;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ApiRouteRepository extends ReactiveCrudRepository<ApiRoute, Long> {
    Mono<ApiRoute> findByRouteId(String routeId);
}
