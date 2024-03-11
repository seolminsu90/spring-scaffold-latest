package com.authentication.gw.api;

import com.authentication.gw.common.util.JWTUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.authentication.gw.common.ServiceConst.ADMIN_ROLE_NAME;

@RestController
@Tag(name = "샘플")
public class SampleController {

    @GetMapping("/token/admin")
    public Mono<String> getToken() {
        return Mono.just(JWTUtil.createToken("Admin", ADMIN_ROLE_NAME, new ArrayList<>()));
    }

    // @Secured({"ROLE_SERVICE_ADMIN", "ROLE_SYSADMIN"}) -- Not Work !
    // @PreAuthorize("hasAnyRole('SERVICE_ADMIN', 'SYSADMIN')") --- Work !
    @PreAuthorize("hasRole('SYSADMIN')")
    @GetMapping("/token/me")
    public Mono<Map<String, Object>> getSessionData22() {
        return ReactiveSecurityContextHolder.getContext()
                                            .map(securityContext -> {
                                                Authentication authentication = securityContext.getAuthentication();
                                                Map<String, Object> sessionData = new HashMap<>();
                                                sessionData.put("authenticated",
                                                    authentication != null && authentication.isAuthenticated());
                                                sessionData.put("principal", authentication != null ?
                                                    authentication.getPrincipal() : null);
                                                sessionData.put("authorities", authentication != null ?
                                                    authentication.getAuthorities() : null);
                                                return sessionData;
                                            });
    }

    @PreAuthorize("hasAnyRole('SERVICE_ADMIN', 'SYSADMIN')")// @PreAutorize 는 안되는데 왜그렇지?
    @GetMapping("/token/me2")
    public Mono<Map<String, Object>> getSessionData2(@AuthenticationPrincipal Mono<Authentication> auth) {
        return auth
            .map(authentication -> {
                Map<String, Object> sessionData = new HashMap<>();
                sessionData.put("authenticated", authentication != null && authentication.isAuthenticated());
                sessionData.put("principal", authentication != null ? authentication.getPrincipal() : null);
                sessionData.put("authorities", authentication != null ? authentication.getAuthorities() : null);
                return sessionData;
            });
    }
}
