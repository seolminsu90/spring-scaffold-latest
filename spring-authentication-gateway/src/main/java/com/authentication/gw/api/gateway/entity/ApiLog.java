package com.authentication.gw.api.gateway.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table(name = "api_log")
@Data
@Builder
public class ApiLog {
    @Id
    private Long seq;
    private String txid;
    private String username;
    private String method;
    private String uri;
    private int code;
    private String params;
    @CreatedDate
    @Column("req_datetime")
    private LocalDateTime reqDateTime;
    @LastModifiedDate
    @Column("res_datetime")
    private LocalDateTime resDateTime;
}
