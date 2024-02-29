package com.scaffold.boot3.common.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.authentication.AuthenticationManagerBeanDefinitionParser;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {

    public SecurityConfig(RestDeniedHandler restDeniedHandler, RestAuthenticationEntryPoint restAuthenticationEntryPoint, AuthenticationTokenProcessingFilter authenticationTokenProcessingFilter) {
        this.restDeniedHandler = restDeniedHandler;
        this.restAuthenticationEntryPoint = restAuthenticationEntryPoint;
        this.authenticationTokenProcessingFilter = authenticationTokenProcessingFilter;
    }

    private final RestDeniedHandler restDeniedHandler;
    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    private final AuthenticationTokenProcessingFilter authenticationTokenProcessingFilter;

    /*
    // RESTful + Header 기반 인증이므로 필요하지 않을 듯.
    @Bean
    public CookieCsrfTokenRepository csrfTokenRepository() {
        CookieCsrfTokenRepository csrfRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();
        csrfRepository.setHeaderName("X-Csrf-token");
        csrfRepository.setCookieName("X-Csrf-token");
        return csrfRepository;
    }*/

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http/*, CookieCsrfTokenRepository csrfTokenRepository*/) throws Exception {
        http
            .httpBasic(AbstractHttpConfigurer::disable)
            .csrf(AbstractHttpConfigurer::disable)
            //.ignoringRequestMatchers("/api/users/signup", "/api/users/signin")
            //.csrfTokenRepository(csrfTokenRepository).and() // CSRF 활성 시 (createCSRF 확인)
            // XSRF-TOKEN 응답 헤더를 보관했다가 X-XSRF-TOKEN Header로 요청에 실어 보내야 한다. (logging : org.springframework.security.web.csrf DEBUG)
            .sessionManagement((sm) -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(eh -> eh.authenticationEntryPoint(restAuthenticationEntryPoint).accessDeniedHandler(restDeniedHandler))
            .headers(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
//            .authorizeHttpRequests(auth -> auth
//                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
//                .requestMatchers("/api/users/signup"... 기타 등등).permitAll()
//                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
//                .anyRequest().authenticated())
            .formLogin(AbstractHttpConfigurer::disable)
            .addFilterBefore(authenticationTokenProcessingFilter, UsernamePasswordAuthenticationFilter.class)
            .authenticationProvider(new AuthenticationManagerBeanDefinitionParser.NullAuthenticationProvider());
        return http.build();
    }
}
