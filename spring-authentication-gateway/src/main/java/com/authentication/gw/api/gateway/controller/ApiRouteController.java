package com.authentication.gw.api.gateway.controller;

import com.authentication.gw.api.gateway.entity.ApiRoute;
import com.authentication.gw.api.gateway.model.ApiRouteCreateReq;
import com.authentication.gw.api.gateway.service.ApiRouteService;
import com.authentication.gw.common.model.ApiResponse;
import com.authentication.gw.common.model.ApiStatus;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.endpoint.RefreshEndpoint;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RequestMapping("/routes")
@RestController
@RequiredArgsConstructor
@Tag(name = "게이트웨이 라우트")
public class ApiRouteController {
    private final ApiRouteService apiRouteService;
    private final RefreshEndpoint refreshEndpoint;

    @Secured("ROLE_Admin")
    @PostMapping
    public Mono<ResponseEntity<ApiResponse<Long>>> create(@RequestBody @Valid ApiRouteCreateReq request) {
        return apiRouteService.upsertServiceAndRoutes(request.getService(), request.getUri(), request.getRoutes())
                              .map(res -> ResponseEntity.ok()
                                                        .body(new ApiResponse<>(ApiStatus.SUCCESS, res)))
                              .doOnSuccess(res -> refreshEndpoint.refresh())
                              .defaultIfEmpty(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                                            .build());
    }

    @GetMapping("/{routeId}")
    public Mono<ResponseEntity<ApiRoute>> getById(@PathVariable("routeId") String routeId) {
        return apiRouteService.getByRouteId(routeId)
                              .map(route -> ResponseEntity.ok()
                                                          .body(route))
                              .defaultIfEmpty(ResponseEntity.notFound()
                                                            .build());
    }

    @GetMapping("/refresh")
    public Mono<ResponseEntity<String>> refreshRoutes() {
        refreshEndpoint.refresh();

        return Mono.just(ResponseEntity.ok()
                                       .body("Routes reloaded successfully"));
    }
}
