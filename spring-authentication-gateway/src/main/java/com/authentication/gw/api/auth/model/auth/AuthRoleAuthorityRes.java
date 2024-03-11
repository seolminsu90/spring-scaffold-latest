package com.authentication.gw.api.auth.model.auth;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AuthRoleAuthorityRes {
    private String role;
    private String roleDesc;
    private List<AuthorityRes> authorities;
}
