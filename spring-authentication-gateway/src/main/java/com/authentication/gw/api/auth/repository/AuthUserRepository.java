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

    // 간단한 건 그냥 DatabaseClient 보다 이렇게 하는게 나을 듯.
    @Query(
        value = """
                SELECT a.*, b.role FROM auth_user a INNER JOIN auth_user_service b
                ON a.uid = b.uid AND (:service is null OR b.service = :service)
            """
    )
    Flux<AuthUser> findAllUserByServiceName(@Param("service") String service);
}
