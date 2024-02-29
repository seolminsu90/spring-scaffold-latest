package com.authentication.gw.api.auth.model.login;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginRes {
    private String uid;
    private String access;
    private String refresh;
    private LoginRoleAuthRes roleAuth;
}
