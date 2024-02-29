package com.authentication.gw.api.gateway.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiRouteReq {
    @NotNull
    private String routeId;
    @NotNull
    private String path;
    private String method;
    private String desc;
    private Integer permitAll = 0;
}
