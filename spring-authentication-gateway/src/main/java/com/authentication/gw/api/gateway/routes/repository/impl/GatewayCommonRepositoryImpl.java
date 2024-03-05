package com.authentication.gw.api.gateway.routes.repository.impl;

import com.authentication.gw.api.gateway.routes.model.ApiRouteReq;
import com.authentication.gw.api.gateway.routes.model.ApiRouteRes;
import com.authentication.gw.api.gateway.routes.repository.GatewayCommonRepository;
import com.authentication.gw.common.ServiceConst;
import com.authentication.gw.common.error.ApiException;
import com.authentication.gw.common.model.ApiStatus;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.StringJoiner;

@RequiredArgsConstructor
@Repository
@Transactional
public class GatewayCommonRepositoryImpl implements GatewayCommonRepository {
    private final DatabaseClient databaseClient;

    @Override
    public Flux<ApiRouteRes> findAllRoutes() {
        return findAllRoutes(null);
    }

    @Override
    public Flux<ApiRouteRes> findAllRoutes(@Nullable String serviceName) {
        var findAllApiRouteSQL = """
                SELECT r.*, s.uri from api_route r
                INNER JOIN  api_service s
                ON r.service = s.service
            """;

        DatabaseClient.GenericExecuteSpec spec;
        if (StringUtils.hasLength(serviceName)) {
            findAllApiRouteSQL += " WHERE s.service = :serviceName";
            spec = databaseClient.sql(findAllApiRouteSQL)
                                 .bind("serviceName", serviceName);
        } else {
            spec = databaseClient.sql(findAllApiRouteSQL);
        }

        return spec.map((row, meta) -> ApiRouteRes.builder()
                                                  .routeId(row.get("route_id", String.class))
                                                  .service(row.get("service", String.class))
                                                  .path(row.get("path", String.class))
                                                  .uri(row.get("uri", String.class))
                                                  .method(row.get("method", String.class))
                                                  .permitAll(row.get("permit_all", Integer.class))
                                                  .build())
                   .all();
    }

    @Override
    public Mono<Long> saveApiService(String service, String uri) {
        var saveApiServiceSQL = """
                INSERT INTO api_service
                (service, uri)
                VALUES
                (:service, :uri)
                ON DUPLICATE KEY UPDATE uri = VALUES(uri)
            """;

        return databaseClient.sql(saveApiServiceSQL)
                             .bind("service", service)
                             .bind("uri", uri)
                             .fetch()
                             .rowsUpdated();
    }

    @Override
    public Mono<Long> saveApiRouteReqs(String service, List<ApiRouteReq> routes) {
        StringJoiner joiner = getStringJoiner(service, routes);
        String insertQuery = String.format("""
                    INSERT INTO api_route (route_id, service, `path`, method, `desc`, permit_all) VALUES %s
                    ON DUPLICATE KEY UPDATE
                        service = VALUES(service),
                        `desc` = VALUES(`desc`),
                        path = VALUES(path),
                        method = VALUES(method),
                        permit_all = VALUES(permit_all)
                """,
            joiner);

        return databaseClient.sql(insertQuery)
                             .fetch()
                             .rowsUpdated();
    }

    @NotNull
    private StringJoiner getStringJoiner(String service, List<ApiRouteReq> routes) {
        StringJoiner joiner = new StringJoiner(",");
        for (ApiRouteReq authRouteReq : routes) {
            if (authRouteReq.getRouteId()
                            .equals(ServiceConst.ADMIN_ROLE_NAME)) {
                throw new ApiException(HttpStatus.BAD_REQUEST, ApiStatus.BAD_REQUEST_ROUTE_ID);
            }

            joiner.add(String.format("('%s', '%s', '%s', '%s', '%s', '%s')",
                authRouteReq.getRouteId(), service,
                authRouteReq.getPath(), authRouteReq.getMethod(), authRouteReq.getDesc(), authRouteReq.getPermitAll()));
        }
        return joiner;
    }
}
