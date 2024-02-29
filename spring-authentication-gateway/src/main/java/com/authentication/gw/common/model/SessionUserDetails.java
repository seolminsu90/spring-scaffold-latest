package com.authentication.gw.common.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Objects;

import static com.authentication.gw.common.ServiceConst.ADMIN_ROLE_NAME;

@NoArgsConstructor
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class SessionUserDetails implements UserDetails {

    public SessionUserDetails(String id, String role, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.role = role;
        this.authorities = authorities;
    }

    private String id;
    @Getter
    private String role;

    @Getter
    @Setter
    private String token;
    private Collection<? extends GrantedAuthority> authorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public boolean isAdmin() {
        return Objects.equals(role, ADMIN_ROLE_NAME);
    }

    public boolean hasAuthority(String targetAuthority) {
        for (GrantedAuthority authority : authorities) {
            if (authority.getAuthority()
                         .equals(targetAuthority)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return this.id;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
