package com.authentication.gw.api;

import com.authentication.gw.common.model.ApiResponse;
import com.authentication.gw.common.model.ApiStatus;
import com.authentication.gw.common.util.JWTUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.authentication.gw.common.ServiceConst.ADMIN_ROLE_NAME;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "샘플")
public class SampleController {
    private final SampleService sampleService;

    // 단순 Auth 출력
    @GetMapping("/greeting")
    @PreAuthorize("@authorizedProcess.apply(authentication)")
    public Mono<String> greeting() {
        return Mono.just("Authorized 된 유저의 이름을 로그에 출력합니다.");
    }

    // 함수형 인터페이스 사용
    @GetMapping("/greeting2")
    @PreAuthorize("@authorizedProcessWithParam.apply(authentication, #name)")
    public Mono<String> greeting2(@P("name") @RequestParam(name = "name") String name) {
        return Mono.just(String.format("Authorized 된 유저의 이름이 %s과 일치하면 로그에 출력합니다.", name));
    }

    // 일반 메서드 빈 사용
    @GetMapping("/greeting3")
    @PreAuthorize("@authorizedFunction.authorizedSelf(authentication, #name)")
    public Mono<String> greeting3(@P("name") @RequestParam(name = "name") String name) {
        return Mono.just(String.format("Authorized 된 유저의 이름이 %s과 일치하면 로그에 출력합니다.", name));
    }

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

    @PreAuthorize("hasAnyRole('SERVICE_ADMIN', 'SYSADMIN')")
    @GetMapping("/token/me2")
    public Mono<Map<String, Object>> getSessionData2(@AuthenticationPrincipal Mono<Authentication> auth) { // Mono가
        // 아니어도 됨.
        return auth
            .map(authentication -> {
                Map<String, Object> sessionData = new HashMap<>();
                sessionData.put("authenticated", authentication != null && authentication.isAuthenticated());
                sessionData.put("principal", authentication != null ? authentication.getPrincipal() : null);
                sessionData.put("authorities", authentication != null ? authentication.getAuthorities() : null);
                return sessionData;
            });
    }

    @GetMapping("/circuit")
    public Mono<String> circuitTest() {
        return sampleService.greeting();
    }


    // common fallback
    @GetMapping("/fallback")
    public Mono<ResponseEntity<ApiResponse<?>>> fallback() {
        return Mono.just(ResponseEntity.ok().body(new ApiResponse<>(ApiStatus.INTERNAL_SERVER_ERROR, "Fallback.")));
    }
}
