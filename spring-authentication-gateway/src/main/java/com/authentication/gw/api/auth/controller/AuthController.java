package com.authentication.gw.api.auth.controller;

import com.authentication.gw.api.auth.model.auth.AuthAuthorityReq;
import com.authentication.gw.api.auth.model.auth.AuthUserRes;
import com.authentication.gw.api.auth.model.auth.AuthUserServiceReq;
import com.authentication.gw.api.auth.model.auth.AuthUserServiceRes;
import com.authentication.gw.api.auth.service.AuthService;
import com.authentication.gw.api.gateway.routes.model.ApiRouteRes;
import com.authentication.gw.api.gateway.routes.service.ApiRouteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
@RestController
@Tag(name = "유저 권한 설정")
public class AuthController {
    private final ApiRouteService apiRouteService;
    private final AuthService authService;

    @GetMapping("/authorities")
    @Operation(summary = "권한 목록 조회")
    public Mono<ResponseEntity<List<ApiRouteRes>>> getAllByService(@RequestParam(name = "serviceName") String serviceName) {
        return apiRouteService.getAllByService(serviceName)
                              .collectList()
                              .map(routes -> ResponseEntity.ok()
                                                           .body(routes))
                              .defaultIfEmpty(ResponseEntity.notFound()
                                                            .build());
    }

    @GetMapping("/users")
    @Operation(summary = "모든 유저 조회")
    public Mono<ResponseEntity<List<AuthUserRes>>> getAllUsers() {
        return authService.findAllUser()
                          .map(AuthUserRes::of)
                          .collectList()
                          .map(routes -> ResponseEntity.ok()
                                                       .body(routes))
                          .defaultIfEmpty(ResponseEntity.notFound()
                                                        .build());
    }

    @PutMapping("/users/{uid}/service/{service}")
    @Operation(summary = "유저 서비스 권한 변경")
    public Mono<ResponseEntity<AuthUserServiceRes>> saveUserServiceRole(@PathVariable("uid") String uid,
                                                                        @PathVariable("service") String service,
                                                                        @RequestBody @Valid AuthUserServiceReq request) {
        return authService.saveUserServiceRole(uid, service, request.getRole())
                          .map(res -> ResponseEntity.ok()
                                                    .body(res));
    }

    @PostMapping("/roles/{role}/authorities")
    @Operation(summary = "역할 권한 수정")
    public Mono<ResponseEntity<?>> saveRoleAuthorities(@PathVariable("role") String role,
                                                       @RequestBody AuthAuthorityReq request) {
        return authService.saveRoleAuthorities(role, request.getAuthorities())
                          .collectList()
                          .map(res -> ResponseEntity.ok().body(res));
    }
}
