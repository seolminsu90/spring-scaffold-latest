package com.authentication.gw.api.auth.repository.impl;

import com.authentication.gw.api.auth.model.login.LoginAuthUserServiceRes;
import com.authentication.gw.api.auth.repository.AuthUserServiceCustomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

import static com.authentication.gw.common.ServiceConst.*;

@RequiredArgsConstructor
@Repository
@Transactional
public class AuthUserServiceCustomRepositoryImpl implements AuthUserServiceCustomRepository {
    private final DatabaseClient databaseClient;

    @Override
    public Mono<Long> saveAuthUserService(String uid, String service, String role) {
        var insertSql = "INSERT INTO auth_user_service (uid, service, role) VALUES (:uid, :service, :role)";

        return databaseClient.sql(insertSql)
                             .bind("uid", uid)
                             .bind("service", service)
                             .bind("role", role)
                             .fetch()
                             .rowsUpdated();
    }

    @Override
    public Mono<LoginAuthUserServiceRes> findAuthUserServiceRole(String uid, String service) {
        var findAuthUserServiceSQL = """
                SELECT s.uid, s.service, s.role FROM auth_user_service s
                WHERE s.uid = :uid AND s.service = :service
            """;

        return databaseClient.sql(findAuthUserServiceSQL)
                             .bind("uid", uid)
                             .bind("service", service)
                             .map((row, meta) ->
                                 new LoginAuthUserServiceRes(row.get("uid", String.class),
                                     row.get("service", String.class),
                                     row.get("role", String.class)
                                 )
                             )
                             .one();
    }

    @Override
    public Flux<Map<String, Object>> findRoleAndAuthority(LoginAuthUserServiceRes role) {
        if (role.getRole()
                .equals(ADMIN_ROLE_NAME)) return Flux.empty();

        if (role.getRole().equals(SERVICE_ADMIN_KEY)) {
            var findRoleAndAuthorityByUidSQL = """
                    SELECT r.route_id as authority FROM api_route r
                    WHERE service = :service
                """;

            return databaseClient.sql(findRoleAndAuthorityByUidSQL)
                                 .bind("service", role.getService())
                                 .fetch()
                                 .all();
        }

        var findRoleAndAuthorityByUidSQL = """
                SELECT a.route_id as authority FROM auth_role_mapping m
                INNER JOIN auth_role r
                ON m.auth_role = r.role
                INNER JOIN api_route a
                ON m.route_id = a.route_id
                WHERE auth_role = :authRole
            """;

        return databaseClient.sql(findRoleAndAuthorityByUidSQL)
                             .bind("authRole", role.getRole())
                             .fetch()
                             .all();
    }
}
