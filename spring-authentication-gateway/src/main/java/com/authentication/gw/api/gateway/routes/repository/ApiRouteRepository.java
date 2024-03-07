package com.authentication.gw.api.gateway.routes.repository;

import com.authentication.gw.api.gateway.routes.entity.ApiRoute;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApiRouteRepository extends ReactiveCrudRepository<ApiRoute, String>, GatewayCommonRepository {
}
