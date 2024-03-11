package com.authentication.gw.api.auth.controller;

import com.authentication.gw.api.auth.model.auth.*;
import com.authentication.gw.api.auth.service.AuthService;
import com.authentication.gw.api.gateway.routes.model.ApiRouteRes;
import com.authentication.gw.api.gateway.routes.service.ApiRouteService;
import com.authentication.gw.common.model.ApiResponse;
import com.authentication.gw.common.model.ApiStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
@RestController
@Tag(name = "유저 권한 설정")
public class AuthController {
    private final ApiRouteService apiRouteService;
    private final AuthService authService;

    @GetMapping("/users")
    @Operation(summary = "모든 유저 조회")
    public Mono<ResponseEntity<ApiResponse<List<AuthUserRes>>>> getAllUsers(@RequestParam(name = "service") String service) {
        return authService.findAllUser(service)
                          .map(AuthUserRes::of)
                          .collectList()
                          .map(routes -> ResponseEntity.ok()
                                                       .body(new ApiResponse<>(ApiStatus.SUCCESS, routes)))
                          .defaultIfEmpty(ResponseEntity.notFound()
                                                        .build());
    }

    @PutMapping("/users/{uid}/service/{service}")
    @Operation(summary = "유저 서비스 권한 변경")
    public Mono<ResponseEntity<ApiResponse<AuthUserServiceRes>>> saveUserServiceRole(
        @AuthenticationPrincipal Mono<Authentication> auth,
        @PathVariable("uid") String uid,
        @PathVariable("service") String service,
        @RequestBody @Valid AuthUserServiceReq request) {

        return auth.flatMap(authentication -> authService.saveUserServiceRole(authentication, uid, service,
                       request.getRole()))
                   .map(res -> ResponseEntity.ok()
                                             .body(new ApiResponse<>(ApiStatus.SUCCESS, res)));

    }

    @GetMapping("/services")
    @Operation(summary = "서비스 조회")
    public Mono<ResponseEntity<ApiResponse<List<ApiServiceRes>>>> getServices()

    {
        return authService.getServices()
                          .collectList()
                          .map(res -> ResponseEntity.ok().body(new ApiResponse<>(ApiStatus.SUCCESS, res)));
    }

    @PostMapping("/services/{service}/roles/{role}/authorities")
    @Operation(summary = "서비스 역할의 권한 수정")
    public Mono<ResponseEntity<ApiResponse<?>>> saveRoleAuthorities(@PathVariable("role") String role,
                                                                    @PathVariable("service") String service,
                                                                    @RequestBody @Valid AuthAuthorityReq request) {
        return authService.saveRoleAuthorities(role, service, request.getAuthorities())
                          .map(res -> ResponseEntity.ok().body(new ApiResponse<>(ApiStatus.SUCCESS, res)));
    }

    @GetMapping("/services/{service}/roles")
    @Operation(summary = "서비스 역할 조회")
    public Mono<ResponseEntity<ApiResponse<List<AuthRoleAuthorityRes>>>> getServiceRoleAuthorities(@PathVariable(
        "service") String service) {
        return authService.getServiceRoleAuthorities(service)
                          .map(res -> ResponseEntity.ok().body(new ApiResponse<>(ApiStatus.SUCCESS, res)));
    }


    @GetMapping("/services/{service}/authorities")
    @Operation(summary = "서비스 모든 권한 조회")
    public Mono<ResponseEntity<ApiResponse<List<ApiRouteRes>>>> getServiceAuthorities(@PathVariable(
        "service") String service) {
        return apiRouteService.getAllByService(service)
                              .collectList()
                              .map(routes -> ResponseEntity.ok()
                                                           .body(new ApiResponse<>(ApiStatus.SUCCESS, routes)))
                              .defaultIfEmpty(ResponseEntity.notFound()
                                                            .build());
    }
}
