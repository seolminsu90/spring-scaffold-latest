package com.authentication.gw.api.auth.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "auth_user")
public class AuthUser {
    @Id
    private Long seq;
    private String uid;
    private String name;
    private String email;
    private String title;
    @Column("is_admin")
    private Integer isAdmin;

    public boolean isAdmin() {
        return this.isAdmin.equals(1);
    }

    @Transient
    private List<AuthUserService> serviceRoles;

    @CreatedDate
    @Column("created_date")
    private LocalDateTime createdDate;
    @LastModifiedDate
    @Column("last_login_date")
    private LocalDateTime lastLoginDate;

}
