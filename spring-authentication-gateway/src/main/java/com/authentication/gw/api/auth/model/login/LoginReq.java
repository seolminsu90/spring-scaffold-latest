package com.authentication.gw.api.auth.model.login;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginReq {
    @NotNull
    private String username;
    @NotNull
    private String password;
    private String service;
}
