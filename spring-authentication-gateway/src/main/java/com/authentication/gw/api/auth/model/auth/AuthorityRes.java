package com.authentication.gw.api.auth.model.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthorityRes {
    private String authority;
    private String desc;
    private String method;
}
