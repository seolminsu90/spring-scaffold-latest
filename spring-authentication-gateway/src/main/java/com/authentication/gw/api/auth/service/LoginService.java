package com.authentication.gw.api.auth.service;

import com.authentication.gw.api.auth.model.login.LoginReq;
import com.authentication.gw.api.auth.model.login.LoginRes;
import reactor.core.publisher.Mono;

public interface LoginService {
    Mono<LoginRes> login(LoginReq request);
}
