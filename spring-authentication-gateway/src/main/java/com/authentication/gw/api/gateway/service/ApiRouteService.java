package com.authentication.gw.api.gateway.service;

import com.authentication.gw.api.gateway.entity.ApiRoute;
import com.authentication.gw.api.gateway.model.ApiRouteReq;
import com.authentication.gw.api.gateway.model.ApiRouteRes;
import com.authentication.gw.api.gateway.repository.ApiRouteRepository;
import com.authentication.gw.api.gateway.repository.GatewayCommonRepository;
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
    private final GatewayCommonRepository gatewayCommonRepository;
    // private final MappingR2dbcConverter mappingR2dbcConverter; // https://godekdls.github.io/Spring%20Data%20R2DBC/mapping/
    // (중첩 지원 안해서 흠..)

    public Flux<ApiRouteRes> getAll() {
        return gatewayCommonRepository.findAllRoutes();
    }

    public Mono<ApiRoute> getByRouteId(String routeId) {
        return apiRouteRepository.findByRouteId(routeId);
    }

    @Transactional
    public Mono<Long> upsertServiceAndRoutes(String service, String uri, List<ApiRouteReq> routes) {
        return gatewayCommonRepository.saveApiService(service, uri)
                                      .flatMap(res -> gatewayCommonRepository.saveApiRouteReqs(service, routes));
    }
}
