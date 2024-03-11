package com.authentication.gw.api.auth.repository.impl;

import com.authentication.gw.api.auth.repository.AuthRoleMappingCustomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class AuthRoleMappingCustomRepositoryImpl implements AuthRoleMappingCustomRepository {

    private final DatabaseClient databaseClient;

    @Override
    public Mono<Long> deleteAuthRoleMapping(String role, String service) {
        var deleteExcludeAuthRoleMappingQuery = """
            DELETE a FROM auth_role_mapping a
            INNER JOIN api_route b ON a.route_id = b.route_id AND b.service = :service
            WHERE a.auth_role = :role
            """;
        return databaseClient.sql(deleteExcludeAuthRoleMappingQuery)
                             .bind("role", role)
                             .bind("service", service)
                             .fetch()
                             .rowsUpdated();
    }


    @Override
    public Mono<Long> saveAuthRoleMapping(String role, String service, List<String> authorities) {
        var saveAuthRoleQuery = """
            INSERT INTO auth_role_mapping (auth_role, route_id)
            SELECT :role, route_id FROM api_route
            WHERE route_id IN (:authorities) AND service = :service
            """;

        return databaseClient.sql(saveAuthRoleQuery)
                             .bind("role", role)
                             .bind("service", service)
                             .bind("authorities", authorities)
                             .fetch()
                             .rowsUpdated();
    }


    // createStatement 방식 (Bulk insert할때 유용)
    /*
    @Override
    public Flux<Result> saveAuthRoleMapping(String role, List<String> authorities) {
        return databaseClient.inConnectionMany(connection -> {
            Statement statement = connection.createStatement(
                "INSERT INTO auth_role_mapping(auth_role, route_id) VALUES (?, ?)"
            ); // .returnGeneratedValues("auth_role", "route_id"); 반환값 사용 시
            for (String authority : authorities) {
                statement.bind(0, role).bind(1, authority).add();
            }
            return Flux.from(statement.execute());
        });
    }

    @NotNull
    private StringJoiner getStringJoiner(String role, List<String> authorities) {
        StringJoiner joiner = new StringJoiner(",");
        for (String authority : authorities) {
            joiner.add(String.format("('%s', '%s')", role, authority));
        }
        return joiner;
    }

     */
}
