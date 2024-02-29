package com.authentication.gw.api.auth.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table(name = "auth_role_mapping")
public class AuthRoleMapping {
    @Id
    private String authRole;
    private String roleAuthority;
}
