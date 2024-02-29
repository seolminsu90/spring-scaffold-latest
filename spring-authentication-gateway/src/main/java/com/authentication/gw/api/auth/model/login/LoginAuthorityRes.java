package com.authentication.gw.api.auth.model.login;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginAuthorityRes {
    private String authority;
    private String authorityDesc;
}
