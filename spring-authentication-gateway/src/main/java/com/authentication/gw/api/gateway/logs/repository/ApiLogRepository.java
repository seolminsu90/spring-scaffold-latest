package com.authentication.gw.api.gateway.logs.repository;

import com.authentication.gw.api.gateway.logs.entity.ApiLog;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ApiLogRepository extends ReactiveCrudRepository<ApiLog, Long> {
    @NotNull Mono<ApiLog> save(@NotNull ApiLog apiLog);

    @Modifying
    @Query("""
                UPDATE api_log SET code = :code, res_datetime = NOW() WHERE txid = :txid
        """)
    Mono<Integer> updateApiLogByTxid(Integer code, String txid);

    Flux<ApiLog> findAllByOrderByReqDateTimeDesc(Pageable pageable);

    Mono<Long> count();
}
