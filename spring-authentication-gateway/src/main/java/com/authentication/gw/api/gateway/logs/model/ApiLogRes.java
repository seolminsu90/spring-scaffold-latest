package com.authentication.gw.api.gateway.logs.model;

import com.authentication.gw.api.gateway.logs.entity.ApiLog;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiLogRes {
    private Long seq;
    private String txid;
    private String username;
    private String method;
    private String uri;
    private int code;
    private String params;
    private LocalDateTime reqDateTime;
    private LocalDateTime resDateTime;

    public static ApiLogRes of(ApiLog entity) {
        return ApiLogRes.builder()
                        .seq(entity.getSeq())
                        .txid(entity.getTxid())
                        .username(entity.getUsername())
                        .method(entity.getMethod())
                        .uri(entity.getUri())
                        .code(entity.getCode())
                        .params(entity.getParams())
                        .reqDateTime(entity.getReqDateTime())
                        .resDateTime(entity.getResDateTime())
                        .build();
    }
}
