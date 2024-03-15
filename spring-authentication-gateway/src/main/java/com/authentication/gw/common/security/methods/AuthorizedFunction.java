package com.authentication.gw.common.security.methods;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuthorizedFunction {

    public boolean authorizedSelf(Authentication authentication, String name) {
        String authenticationName = authentication.getName();
        if (authenticationName.equals(name)) {
            log.info("Authorize with method {}", name);
            return true;
        } else {
            log.info("Authorize Name Not matched {} : {}", authenticationName, name);
            return false;
        }
    }
}
