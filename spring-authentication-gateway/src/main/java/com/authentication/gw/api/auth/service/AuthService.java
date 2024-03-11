package com.authentication.gw.api.auth.service;

import com.authentication.gw.api.auth.entity.AuthUser;
import com.authentication.gw.api.auth.model.auth.ApiServiceRes;
import com.authentication.gw.api.auth.model.auth.AuthRoleAuthorityRes;
import com.authentication.gw.api.auth.model.auth.AuthUserServiceRes;
import com.authentication.gw.api.auth.model.auth.AuthUserWithRole;
import com.authentication.gw.api.auth.model.login.LoginAuthUserServiceRes;
import com.authentication.gw.api.auth.model.login.LoginReq;
import com.authentication.gw.api.auth.model.login.LoginRes;
import com.authentication.gw.api.auth.model.login.LoginRoleAuthRes;
import com.authentication.gw.api.auth.repository.AuthRoleMappingRepository;
import com.authentication.gw.api.auth.repository.AuthRoleRepository;
import com.authentication.gw.api.auth.repository.AuthUserRepository;
import com.authentication.gw.api.auth.repository.AuthUserServiceRepository;
import com.authentication.gw.api.gateway.routes.repository.ApiServiceRepository;
import com.authentication.gw.common.error.ApiException;
import com.authentication.gw.common.ldap.LdapUser;
import com.authentication.gw.common.model.ApiStatus;
import com.authentication.gw.common.model.SessionUserDetails;
import com.authentication.gw.common.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.authentication.gw.common.ServiceConst.*;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class AuthService {
    private final AuthUserRepository authUserRepository;
    private final AuthUserServiceRepository authUserServiceRepository;
    private final AuthRoleMappingRepository authRoleMappingRepository;
    private final AuthRoleRepository authRoleRepository;
    private final ApiServiceRepository apiServiceRepository;

    public Mono<LoginRes> findUserAndParse(LdapUser ldapUser, LoginReq request) {
        return authUserRepository.findByUid(ldapUser.getUid())
                                 .flatMap(authUser -> getRoleAndAuthority(authUser, request))
                                 .map(this::setLoginResponseToken)
                                 .switchIfEmpty(createNewUserAndLogin(ldapUser, request));
    }

    private Mono<LoginAuthUserServiceRes> insertNewRecord(String uid, String service) {
        return authUserServiceRepository.saveAuthUserService(uid, service, DEFAULT_ROLE_NAME)
                                        .then(Mono.just(new LoginAuthUserServiceRes(uid, service, DEFAULT_ROLE_NAME)))
                                        .onErrorMap(e -> new ApiException(HttpStatus.BAD_REQUEST,
                                            ApiStatus.BAD_REQUEST_SERVICE));
    }

    private Mono<LoginRes> getRoleAndAuthority(AuthUser authUser, LoginReq request) {
        Mono<LoginAuthUserServiceRes> roleMono;
        if (StringUtils.hasLength(request.getService())) {
            roleMono = authUserServiceRepository.findAuthUserServiceRole(authUser.getUid(), request.getService())
                                                .switchIfEmpty(insertNewRecord(authUser.getUid(),
                                                    request.getService()));
        } else {
            roleMono = Mono.just(new LoginAuthUserServiceRes(authUser.getUid(), null, authUser.isAdmin() ?
                ADMIN_ROLE_NAME : DEFAULT_ROLE_NAME));
        }

        return roleMono.flatMap(role -> authUserServiceRepository.findRoleAndAuthority(role).collectList()
                                                                 .map(maps -> makeLoginRes(maps, authUser,
                                                                     role.getRole())));
    }

    private LoginRes makeLoginRes(List<Map<String, Object>> maps, AuthUser authUser, String role) {
        LoginRoleAuthRes res = new LoginRoleAuthRes();
        res.setRole(role);
        if (authUser.isAdmin() && role.equals(ADMIN_ROLE_NAME)) {
            res.setAuthorities(Collections.emptyList());
        } else {
            List<String> authorities = new ArrayList<>();
            for (Map<String, Object> tuple : maps) {
                authorities.add(String.valueOf(tuple.get("authority")));
            }
            res.setAuthorities(authorities);
        }
        LoginRes loginRes = new LoginRes();
        loginRes.setUid(authUser.getUid());
        loginRes.setRoleAuth(res);
        return loginRes;
    }

    private Mono<LoginRes> createNewUserAndLogin(LdapUser ldapUser, LoginReq request) {
        AuthUser user = ldapUser.toUser();
        return authUserRepository.save(user).flatMap(authUser -> getRoleAndAuthority(authUser, request))
                                 .flatMap(loginRes -> Mono.just(setLoginResponseToken(loginRes)))
                                 .switchIfEmpty(Mono.error(new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "유저 생성에" + " 실패했습니다.", 5000)));
    }

    private LoginRes setLoginResponseToken(LoginRes user) {
        user.setAccess(JWTUtil.createToken(user.getUid(), user.getRoleAuth().getRole(), user.getRoleAuth()
                                                                                            .getAuthorities()));
        user.setRefresh(JWTUtil.createRefreshToken(user.getUid()));
        return user;
    }

    public Flux<AuthUserWithRole> findAllUser(@Nullable String service) {
        return authUserServiceRepository.findAllUserByServiceName(service);
    }

    public Mono<AuthUserServiceRes> saveUserServiceRole(Authentication auth, String uid, String service, String role) {
        SessionUserDetails userDetails = (SessionUserDetails) auth.getPrincipal();
        if (SERVICE_ADMIN_KEY.equals(userDetails.getRole())) {
            if (role.equals(ADMIN_ROLE_NAME)) throw new ApiException(HttpStatus.FORBIDDEN, ApiStatus.FORBIDDEN);
        }

        return authUserRepository.findByUid(uid)
                                 .switchIfEmpty(Mono.error(new ApiException(HttpStatus.NOT_FOUND, "없는 유저입니다.")))
                                 .flatMap(user -> saveUserRole(user, service, role));
    }

    public Mono<Long> saveRoleAuthorities(String role, String service, List<String> authorities) {
        return authRoleRepository.findById(role)
                                 .switchIfEmpty(Mono.error(new ApiException(HttpStatus.BAD_REQUEST,
                                     ApiStatus.BAD_REQUEST)))
                                 .flatMap(authRole -> authRoleMappingRepository.deleteAuthRoleMapping(role, service)
                                                                               .then(authRoleMappingRepository.saveAuthRoleMapping(role, service,
                                                                                   authorities))
                                                                               .onErrorMap(e -> new ApiException(HttpStatus.BAD_REQUEST,
                                                                                   ApiStatus.BAD_REQUEST_ROLE_UPDATE_FAIL)));
    }


    private Mono<AuthUserServiceRes> saveUserRole(AuthUser user, String service, String role) {
        return authUserServiceRepository.saveAuthUserServiceRole(user.getUid(),
                                            service, role)
                                        .then(Mono.just(new AuthUserServiceRes(user.getUid(), service, role)))
                                        .onErrorMap(error -> new ApiException(HttpStatus.BAD_REQUEST, "잘못된 정보를 요청했습니다" +
                                                                                                      ". service가 있는지" +
                                                                                                      " 확인하세요."));
    }

    public Mono<List<AuthRoleAuthorityRes>> getServiceRoleAuthorities(String service) {
        return authUserServiceRepository.findServiceRoleAuthorities(service);
    }

    public Flux<ApiServiceRes> getServices() {
        return apiServiceRepository.findAll()
                                   .map(s -> new ApiServiceRes(s.getService(), s.getUri()));
    }
}
