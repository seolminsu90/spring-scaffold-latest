package com.authentication.gw.api.auth.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table(name = "auth_user_service")
public class AuthUserService {
    @Id
    private String uid;
    private String service;
    private String role;
}
