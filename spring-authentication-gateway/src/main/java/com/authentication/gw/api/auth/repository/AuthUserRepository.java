package com.authentication.gw.api.auth.repository;

import com.authentication.gw.api.auth.entity.AuthUser;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface AuthUserRepository extends ReactiveCrudRepository<AuthUser, Long> {
    Mono<AuthUser> findByUid(String uid);
}
