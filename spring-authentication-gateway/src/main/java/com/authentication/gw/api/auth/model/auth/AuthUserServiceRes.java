package com.authentication.gw.api.auth.model.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthUserServiceRes {
    private String uid;
    private String service;
    private String role;
}
