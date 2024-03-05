package com.authentication.gw.api.gateway.routes.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class ApiRouteRes {
    private String routeId;
    private String service;
    private String uri;
    private String path;
    private String method;
    private String desc;
    private Integer permitAll;

    public boolean isPermitAll() {
        return this.permitAll == 1;
    }
}
