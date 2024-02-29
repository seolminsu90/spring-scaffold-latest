package com.authentication.gw.api.auth.repository;

import com.authentication.gw.api.auth.model.login.LoginAuthUserServiceRes;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface AuthCommonRepository {
    Mono<Long> saveAuthUserService(String uid, String service, String role);

    Mono<LoginAuthUserServiceRes> findAuthUserServiceRole(String uid, String service);

    Flux<Map<String, Object>> findRoleAndAuthority(LoginAuthUserServiceRes role);
}
