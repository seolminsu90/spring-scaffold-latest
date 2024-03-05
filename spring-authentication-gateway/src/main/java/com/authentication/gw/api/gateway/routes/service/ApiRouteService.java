package com.authentication.gw.api.gateway.routes.service;

import com.authentication.gw.api.gateway.routes.entity.ApiRoute;
import com.authentication.gw.api.gateway.routes.model.ApiRouteReq;
import com.authentication.gw.api.gateway.routes.model.ApiRouteRes;
import com.authentication.gw.api.gateway.routes.repository.ApiRouteRepository;
import com.authentication.gw.api.gateway.routes.repository.GatewayCommonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ApiRouteService {

    private final ApiRouteRepository apiRouteRepository;
    // private final MappingR2dbcConverter mappingR2dbcConverter; // https://godekdls.github.io/Spring%20Data%20R2DBC/mapping/
    // (중첩 지원 안해서 흠..)

    public Flux<ApiRouteRes> getAll() {
        return apiRouteRepository.findAllRoutes();
    }

    public Flux<ApiRouteRes> getAllByService(String serviceName) {
        return apiRouteRepository.findAllRoutes(serviceName);
    }


    public Mono<ApiRoute> getByRouteId(String routeId) {
        return apiRouteRepository.findByRouteId(routeId);
    }

    @Transactional
    public Mono<Long> upsertServiceAndRoutes(String service, String uri, List<ApiRouteReq> routes) {
        return apiRouteRepository.saveApiService(service, uri)
                                      .flatMap(res -> apiRouteRepository.saveApiRouteReqs(service, routes));
    }
}
