package com.authentication.gw.api.auth.service;

import com.authentication.gw.api.auth.entity.AuthUser;
import com.authentication.gw.api.auth.model.login.LoginAuthUserServiceRes;
import com.authentication.gw.api.auth.model.login.LoginReq;
import com.authentication.gw.api.auth.model.login.LoginRes;
import com.authentication.gw.api.auth.model.login.LoginRoleAuthRes;
import com.authentication.gw.api.auth.repository.AuthCommonRepository;
import com.authentication.gw.api.auth.repository.AuthUserRepository;
import com.authentication.gw.common.error.ApiException;
import com.authentication.gw.common.ldap.LdapUser;
import com.authentication.gw.common.model.ApiStatus;
import com.authentication.gw.common.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.authentication.gw.common.ServiceConst.ADMIN_ROLE_NAME;
import static com.authentication.gw.common.ServiceConst.DEFAULT_ROLE_NAME;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class AuthService {
    private final AuthUserRepository authUserRepository;
    private final AuthCommonRepository authCommonRepository;

    public Mono<LoginRes> findUserAndParse(LdapUser ldapUser, LoginReq request) {
        return authUserRepository.findByUid(ldapUser.getUid())
                                 .flatMap(authUser -> getRoleAndAuthority(authUser, request))
                                 .map(this::setLoginResponseToken)
                                 .switchIfEmpty(createNewUserAndLogin(ldapUser, request));
    }

    private Mono<LoginAuthUserServiceRes> insertNewRecord(String uid, String service, String role) {
        return authCommonRepository.saveAuthUserService(uid, service, role)
                                   .then(Mono.just(new LoginAuthUserServiceRes(uid, service, role)))
                                   .onErrorResume(Exception.class,
                                       ex -> Mono.error(new ApiException(HttpStatus.BAD_REQUEST,
                                           ApiStatus.BAD_REQUEST_SERVICE)));
    }

    private Mono<LoginRes> getRoleAndAuthority(AuthUser authUser, LoginReq request) {
        Mono<LoginAuthUserServiceRes> roleMono;
        if (StringUtils.hasLength(request.getService())) {
            roleMono = authCommonRepository.findAuthUserServiceRole(authUser.getUid(), request.getService())
                                           .switchIfEmpty(insertNewRecord(authUser.getUid(), request.getService(),
                                               DEFAULT_ROLE_NAME));
        } else {
            roleMono = Mono.just(new LoginAuthUserServiceRes(authUser.getUid(), null, authUser.isAdmin() ?
                ADMIN_ROLE_NAME : DEFAULT_ROLE_NAME));
        }

        return roleMono.flatMap(role ->
            authCommonRepository.findRoleAndAuthority(role)
                                .collectList()
                                .map(maps -> makeLoginRes(maps, authUser, role.getRole()))
        );
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
        return authUserRepository.save(user)
                                 .flatMap(authUser -> getRoleAndAuthority(authUser, request))
                                 .flatMap(loginRes -> Mono.just(setLoginResponseToken(loginRes)))
                                 .switchIfEmpty(Mono.error(new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "유저 생성에" +
                                                                                                              " 실패했습니다.", 5000)));
    }

    private LoginRes setLoginResponseToken(LoginRes user) {
        user.setAccess(JWTUtil.createToken(user.getUid(), user.getRoleAuth()
                                                              .getRole(), user.getRoleAuth()
                                                                              .getAuthorities()));
        user.setRefresh(JWTUtil.createRefreshToken(user.getUid()));
        return user;
    }
}
