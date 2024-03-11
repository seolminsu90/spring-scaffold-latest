package com.authentication.gw.api.auth.repository.impl;

import com.authentication.gw.api.auth.entity.AuthUser;
import com.authentication.gw.api.auth.model.auth.AuthRoleAuthorityRes;
import com.authentication.gw.api.auth.model.auth.AuthUserWithRole;
import com.authentication.gw.api.auth.model.auth.AuthorityRes;
import com.authentication.gw.api.auth.model.login.LoginAuthUserServiceRes;
import com.authentication.gw.api.auth.repository.AuthUserServiceCustomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.authentication.gw.common.ServiceConst.ADMIN_ROLE_NAME;
import static com.authentication.gw.common.ServiceConst.SERVICE_ADMIN_KEY;

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

    @Override
    public Flux<AuthUserWithRole> findAllUserByServiceName(String service) {
        var findAllUserByServiceNameSQL = """
                SELECT a.*, b.role FROM auth_user a INNER JOIN auth_user_service b
                ON a.uid = b.uid AND b.service = :service
            """;

        return databaseClient.sql(findAllUserByServiceNameSQL)
                             .bind("service", service)
                             .map((row, meta) -> {
                                 AuthUser user = AuthUser.builder()
                                                         .name(row.get("name", String.class))
                                                         .uid(row.get("uid", String.class))
                                                         .email(row.get("email", String.class))
                                                         .title(row.get("title", String.class))
                                                         .isAdmin(row.get("is_admin", Integer.class))
                                                         .build();
                                 return new AuthUserWithRole(user, row.get("role", String.class));
                             })
                             .all();
    }


    @Override
    public Mono<List<AuthRoleAuthorityRes>> findServiceRoleAuthorities(String service) {
        var findServiceRoleAuthorities = """
            SELECT c.route_id AS authority, c.desc, c.method AS method, a.`role`, a.role_desc FROM auth_role a
            INNER JOIN auth_role_mapping b
            ON a.role = b.auth_role
            INNER JOIN api_route c
            ON b.route_id = c.route_id AND c.service = :service

            """;

        return databaseClient.sql(findServiceRoleAuthorities)
                             .bind("service", service)
                             .fetch()
                             .all()
                             .collectList()
                             .map(rows -> {
                                 Map<String, AuthRoleAuthorityRes> map = new HashMap<>();
                                 for (var row : rows) {
                                     String role = String.valueOf(row.get("role"));
                                     String roleDesc = String.valueOf(row.get("role_desc"));
                                     String authority = String.valueOf(row.get("authority"));
                                     String desc = String.valueOf(row.get("desc"));
                                     String method = String.valueOf(row.get("method"));
                                     if (!map.containsKey(role)) {
                                         var authRole = AuthRoleAuthorityRes.builder()
                                                                            .role(role)
                                                                            .roleDesc(roleDesc)
                                                                            .authorities(new ArrayList<>())
                                                                            .build();
                                         map.put(role, authRole);
                                     } else {
                                         var authRole = map.get(role);
                                         var authorities = authRole.getAuthorities();
                                         authorities.add(new AuthorityRes(authority, desc, method));
                                     }
                                 }
                                 return map.values().stream().toList();
                             });
    }
}
