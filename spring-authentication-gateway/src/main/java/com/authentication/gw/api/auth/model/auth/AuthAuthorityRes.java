package com.authentication.gw.api.auth.model.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthAuthorityRes {
    private String role;
    private List<String> authorities; // route id 목록이 된다.
}
