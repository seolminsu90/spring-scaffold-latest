package com.authentication.gw.api.auth.model.auth;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AuthUserServiceReq {
    @NotNull
    private String role;
}
