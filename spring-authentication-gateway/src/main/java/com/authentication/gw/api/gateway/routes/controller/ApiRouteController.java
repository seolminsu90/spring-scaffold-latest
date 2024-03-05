package com.authentication.gw.api.gateway.routes.controller;

import com.authentication.gw.api.gateway.routes.entity.ApiRoute;
import com.authentication.gw.api.gateway.routes.model.ApiRouteCreateReq;
import com.authentication.gw.api.gateway.routes.model.ApiRouteRes;
import com.authentication.gw.api.gateway.routes.service.ApiRouteService;
import com.authentication.gw.common.model.ApiResponse;
import com.authentication.gw.common.model.ApiStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.endpoint.RefreshEndpoint;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RequestMapping("/routes")
@RestController
@RequiredArgsConstructor
@Tag(name = "게이트웨이 라우트")
public class ApiRouteController {
    private final ApiRouteService apiRouteService;
    private final RefreshEndpoint refreshEndpoint;

    @Secured("ROLE_Admin")
    @PostMapping
    @Operation(summary = "라우트 생성", description = "등록 후 라우트는 자동으로 갱신됩니다.")
    public Mono<ResponseEntity<ApiResponse<Long>>> create(@RequestBody @Valid ApiRouteCreateReq request) {
        return apiRouteService.upsertServiceAndRoutes(request.getService(), request.getUri(), request.getRoutes())
                              .map(res -> ResponseEntity.ok()
                                                        .body(new ApiResponse<>(ApiStatus.SUCCESS, res)))
                              .doOnSuccess(res -> refreshEndpoint.refresh())
                              .defaultIfEmpty(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                                            .build());
    }

    // 아래의 API들은 없어져도 됨

    @GetMapping
    @Operation(summary = "모든 라우트 조회")
    public Mono<ResponseEntity<List<ApiRouteRes>>> getAll() {
        return apiRouteService.getAll()
                              .collectList()
                              .map(routes -> ResponseEntity.ok()
                                                           .body(routes))
                              .defaultIfEmpty(ResponseEntity.notFound()
                                                            .build());
    }

    @GetMapping("/{routeId}")
    @Operation(summary = "한개의 라우트 조회")
    public Mono<ResponseEntity<ApiRoute>> getById(@PathVariable("routeId") String routeId) {
        return apiRouteService.getByRouteId(routeId)
                              .map(route -> ResponseEntity.ok()
                                                          .body(route))
                              .defaultIfEmpty(ResponseEntity.notFound()
                                                            .build());
    }

    @GetMapping("/refresh")
    @Operation(summary = "라우트 갱신")
    public Mono<ResponseEntity<String>> refreshRoutes() {
        refreshEndpoint.refresh();

        return Mono.just(ResponseEntity.ok()
                                       .body("Routes reloaded successfully"));
    }
}
