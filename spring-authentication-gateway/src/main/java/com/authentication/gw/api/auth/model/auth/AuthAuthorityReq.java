package com.authentication.gw.api.auth.model.auth;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class AuthAuthorityReq {
    @NotNull
    private List<String> authorities; // route id 목록이 된다.
}
