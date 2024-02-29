package com.scaffold.boot3.api;

import com.scaffold.boot3.common.model.SessionUserDetails;
import com.scaffold.boot3.common.util.JWTUtil;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SampleController {

    @GetMapping("/tokens")
    public String getSampleServiceToken() {
        String userId = "SampleUser";
        List<String> authority = List.of("ROLE_USER");

        return JWTUtil.createToken(userId, authority);
    }

    @GetMapping("/greeting")
    public String greeting() {
        return "Hello world";
    }

    @Secured("ROLE_USER")
    @GetMapping("/greeting/user")
    public SessionUserDetails greeting(@AuthenticationPrincipal SessionUserDetails userDetail) {
        return userDetail;
    }
}
