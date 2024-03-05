package com.authentication.gw.api.auth.repository;

import com.authentication.gw.api.auth.entity.AuthUserService;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface AuthUserServiceRepository extends ReactiveCrudRepository<AuthUserService, String>, AuthUserServiceCustomRepository {

    @Modifying
    @Query("""
                    INSERT auth_user_service (uid, service,role) VALUES (:uid, :service, :role)
                    ON DUPLICATE KEY UPDATE role = VALUES(role)
            """)
    Mono<Long> saveAuthUserServiceRole(String uid, String service, String role);
}
