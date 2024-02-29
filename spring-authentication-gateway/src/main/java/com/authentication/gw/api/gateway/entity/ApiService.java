package com.authentication.gw.api.gateway.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table(name = "api_service")
public class ApiService {
    @Id
    private String service;
    private String uri;
}
