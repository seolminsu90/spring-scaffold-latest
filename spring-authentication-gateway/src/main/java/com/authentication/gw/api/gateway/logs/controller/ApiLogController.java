package com.authentication.gw.api.gateway.logs.controller;

import com.authentication.gw.api.gateway.logs.entity.ApiLog;
import com.authentication.gw.api.gateway.logs.service.ApiLogService;
import com.authentication.gw.common.model.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/logs")
@RequiredArgsConstructor
@Tag(name = "게이트웨이 로그")
public class ApiLogController {

    private final ApiLogService apiLogService;

    @GetMapping
    @Operation(summary = "게이트웨이 로그 조회 (페이징)")
    public Mono<ApiResponse<List<ApiLog>>> findApiLogs(@RequestParam(name = "page", defaultValue = "1", required =
        false) Integer page) {
        return apiLogService.findAllLogByPage(page);
    }
}

