package com.authentication.gw.api.gateway.routes.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table(name = "api_route")
public class ApiRoute {
    @Id
    @Column("route_id")
    private String routeId;
    @Transient
    private ApiService service;
    private String path;
    private String method;
    private String desc;
    @Column("permit_all")
    private Integer permitAll;
}
