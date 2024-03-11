package com.authentication.gw.api.auth.repository;

import io.r2dbc.spi.Result;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface AuthRoleMappingCustomRepository {
    Mono<Long> deleteAuthRoleMapping(String role, String service);
    Mono<Long> saveAuthRoleMapping(String role, String service, List<String> authorities);
}
