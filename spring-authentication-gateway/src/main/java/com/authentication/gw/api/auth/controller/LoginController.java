package com.authentication.gw.api.auth.controller;

import com.authentication.gw.api.auth.model.login.LoginReq;
import com.authentication.gw.api.auth.model.login.LoginRes;
import com.authentication.gw.api.auth.service.LoginService;
import com.authentication.gw.common.model.ApiResponse;
import com.authentication.gw.common.model.ApiStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RequestMapping("/auth")
@RestController
@RequiredArgsConstructor
@Tag(name = "인증")
public class LoginController {
    private final LoginService loginService;

    @PostMapping("/token")
    @Operation(summary = "공통 게이트웨이 로그인 인증")
    public Mono<ResponseEntity<ApiResponse<LoginRes>>> userLogin(@RequestBody @Valid LoginReq dto) {
        return loginService.login(dto)
                           .flatMap(loginResponse -> Mono.just(new ApiResponse<>(ApiStatus.SUCCESS, loginResponse)))
                           .map(ResponseEntity::ok)
                           .defaultIfEmpty(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                                         .build());
    }
}
