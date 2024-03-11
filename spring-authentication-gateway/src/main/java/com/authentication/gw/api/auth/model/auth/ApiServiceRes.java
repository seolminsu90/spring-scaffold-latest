package com.authentication.gw.api.auth.model.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiServiceRes {
    private String service;
    private String uri;
}
