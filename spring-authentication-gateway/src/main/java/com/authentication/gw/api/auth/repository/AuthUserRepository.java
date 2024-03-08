package com.authentication.gw.api.auth.repository;

import com.authentication.gw.api.auth.entity.AuthUser;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface AuthUserRepository extends ReactiveCrudRepository<AuthUser, Long> {
    Mono<AuthUser> findByUid(String uid);
}
