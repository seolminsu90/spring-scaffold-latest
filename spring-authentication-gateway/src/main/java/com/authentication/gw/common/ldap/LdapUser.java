package com.authentication.gw.common.ldap;

import com.authentication.gw.api.auth.entity.AuthUser;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LdapUser {
    private String uid;
    private String name;
    private String email;
    private String title;

    public AuthUser toUser() {
        AuthUser user = new AuthUser();
        user.setUid(this.uid);
        user.setName(this.name);
        user.setEmail(this.email);
        user.setTitle(this.title);
        return user;
    }
}
