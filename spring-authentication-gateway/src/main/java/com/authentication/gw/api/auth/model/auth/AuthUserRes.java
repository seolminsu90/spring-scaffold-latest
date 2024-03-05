package com.authentication.gw.api.auth.model.auth;

import com.authentication.gw.api.auth.entity.AuthUser;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AuthUserRes {
    private Long seq;
    private String uid;
    private String name;
    private String email;
    private String title;
    private Integer isAdmin;

    private LocalDateTime createdDate;
    private LocalDateTime lastLoginDate;

    public static AuthUserRes of(AuthUser entity) {
        return AuthUserRes.builder()
                          .seq(entity.getSeq())
                          .uid(entity.getUid())
                          .name(entity.getName())
                          .email(entity.getEmail())
                          .title(entity.getTitle())
                          .isAdmin(entity.getIsAdmin())
                          .createdDate(entity.getCreatedDate())
                          .lastLoginDate(entity.getLastLoginDate())
                          .build();
    }
}
