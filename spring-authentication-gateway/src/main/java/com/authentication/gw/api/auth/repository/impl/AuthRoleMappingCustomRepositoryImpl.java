package com.authentication.gw.api.auth.repository.impl;

import com.authentication.gw.api.auth.repository.AuthRoleMappingCustomRepository;
import io.r2dbc.spi.Result;
import io.r2dbc.spi.Statement;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.StringJoiner;

@RequiredArgsConstructor
@Repository
public class AuthRoleMappingCustomRepositoryImpl implements AuthRoleMappingCustomRepository {

    private final DatabaseClient databaseClient;


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
}
