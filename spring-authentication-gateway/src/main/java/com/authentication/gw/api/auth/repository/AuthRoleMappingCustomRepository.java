package com.authentication.gw.api.auth.repository;

import io.r2dbc.spi.Result;
import reactor.core.publisher.Flux;

import java.util.List;

public interface AuthRoleMappingCustomRepository {
    Flux<Result> saveAuthRoleMapping(String role, List<String> authorities);
}
