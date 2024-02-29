package com.authentication.gw.api.gateway.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ApiRouteCreateReq {
    @NotNull
    private String service;
    @NotNull
    private String uri;

    @NotNull
    private List<ApiRouteReq> routes;
}
