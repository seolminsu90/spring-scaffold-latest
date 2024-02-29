package com.authentication.gw.common.gateway;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationGatewayFilterConfig {
    private String authority;
    private String service;
    private boolean permitAll;
}
