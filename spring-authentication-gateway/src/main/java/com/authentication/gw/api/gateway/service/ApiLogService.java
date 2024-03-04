package com.authentication.gw.api.gateway.service;

import com.authentication.gw.api.gateway.entity.ApiLog;
import com.authentication.gw.api.gateway.repository.ApiLogRepository;
import com.authentication.gw.common.model.ApiResponse;
import com.authentication.gw.common.model.ApiStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

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

    public Mono<ApiResponse<List<ApiLog>>> findAllLogByPage(int page) {
        // TIP: Pageable 대상 entity는 @AllArgsConstructor @NoArgsConstructor 필요
        Pageable pageable = PageRequest.of(page - 1, 20);

        return apiLogRepository.findAllByOrderByReqDateTimeDesc(pageable)
                               .collectList()
                               .zipWith((apiLogRepository.count()))
                               .map(p -> new ApiResponse<>(ApiStatus.SUCCESS, p.getT1(), p.getT2()));
    }
}
