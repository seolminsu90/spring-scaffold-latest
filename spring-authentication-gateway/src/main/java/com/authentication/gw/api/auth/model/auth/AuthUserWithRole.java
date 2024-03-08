package com.authentication.gw.api.auth.model.auth;

import com.authentication.gw.api.auth.entity.AuthUser;
import lombok.Data;

@Data
public class AuthUserWithRole {
    private AuthUser authUser;
    private String role;

    public AuthUserWithRole(AuthUser authUser, String role) {
        this.authUser = authUser;
        this.role = role;
    }
}
