package com.authentication.gw.api.auth.repository;

import com.authentication.gw.api.auth.model.auth.AuthRoleAuthorityRes;
import com.authentication.gw.api.auth.model.auth.AuthUserWithRole;
import com.authentication.gw.api.auth.model.login.LoginAuthUserServiceRes;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

public interface AuthUserServiceCustomRepository {
    Mono<Long> saveAuthUserService(String uid, String service, String role);

    Mono<LoginAuthUserServiceRes> findAuthUserServiceRole(String uid, String service);

    Flux<Map<String, Object>> findRoleAndAuthority(LoginAuthUserServiceRes role);

    Flux<AuthUserWithRole> findAllUserByServiceName(String service);

    Mono<List<AuthRoleAuthorityRes>> findServiceRoleAuthorities(String service);
}
