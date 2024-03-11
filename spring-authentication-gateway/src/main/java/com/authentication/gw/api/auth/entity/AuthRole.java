package com.authentication.gw.api.auth.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table(name = "auth_role")
public class AuthRole {
    @Id
    private String role;
    @JsonProperty("role_desc")
    private String roleDesc;
}
