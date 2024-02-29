package com.authentication.gw.api.auth.service;

import com.authentication.gw.api.auth.model.login.LoginReq;
import com.authentication.gw.api.auth.model.login.LoginRes;
import com.authentication.gw.common.error.ApiException;
import com.authentication.gw.common.ldap.LdapUser;
import com.authentication.gw.common.ldap.LdapUserContextMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

@Service
@Slf4j
@RequiredArgsConstructor
public class LdapService implements LoginService {
    private final LdapTemplate ldapTemplate;
    private final AuthService authService;

    @Override
    @Transactional
    public Mono<LoginRes> login(LoginReq request) {
        return Mono.fromCallable(() -> authenticate(request.getUsername(), request.getPassword()))
                   .map(authenticated -> findUserByUsername(request.getUsername()))
                   .flatMap(findUserAndParse -> authService.findUserAndParse(findUserAndParse, request));
    }


    private boolean authenticate(String username, String password) {
        try {
            ldapTemplate.authenticate(query().where("uid")
                                             .is(username), password);
            return true;
        } catch (Exception e) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "비밀번호가 맞지 않습니다.", 4000);
        }
    }

    private LdapUser findUserByUsername(String username) {
        try {
            return ldapTemplate.searchForObject(query().where("uid")
                                                       .is(username), new LdapUserContextMapper());
        } catch (Exception e) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "유저 정보가 존재하지 않습니다.", 4001);

        }
    }

}
