package com.authentication.gw.common.security.methods;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import reactor.core.publisher.Mono;

import java.util.function.BiFunction;
import java.util.function.Function;

@Slf4j
@Configuration
public class AuthorizeFunctionConfig {
    @Bean
    public Function<Authentication, Mono<Boolean>> authorizedProcess() {

        return authentication -> {
            String name = authentication.getName();
            log.info("Authorize with method {}", name);
            return Mono.just(true);
        };
    }

    @Bean
    public BiFunction<Authentication, String, Mono<Boolean>> authorizedProcessWithParam() {

        return (authentication, name) -> {
            String authenticationName = authentication.getName();
            if (authenticationName.equals(name)) {
                log.info("Authorize with method {}", name);
                return Mono.just(true);
            } else {
                log.info("Authorize Name Not matched {} : {}", authenticationName, name);
                return Mono.just(false);
            }
        };
    }
}
