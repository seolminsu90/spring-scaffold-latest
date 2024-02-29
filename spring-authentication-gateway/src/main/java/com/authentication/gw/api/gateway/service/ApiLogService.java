package com.authentication.gw.api.gateway.service;

import com.authentication.gw.api.gateway.entity.ApiLog;
import com.authentication.gw.api.gateway.repository.ApiLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ApiLogService {
    private final ApiLogRepository apiLogRepository;

    public Mono<ApiLog> saveApiLog(ApiLog apiLog) {
        return apiLogRepository.save(apiLog);
    }

    public Mono<Integer> updateApiLogByTxid(String txid, Integer code) {
        return apiLogRepository.updateApiLogByTxid(code, txid);
    }
}
