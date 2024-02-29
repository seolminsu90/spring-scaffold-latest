package com.scaffold.boot3.common.security;

import com.scaffold.boot3.common.model.SessionUserDetails;
import com.scaffold.boot3.common.util.JWTUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class AuthenticationTokenProcessingFilter extends OncePerRequestFilter {

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        String authenticationStr = getAuthentication(request);

        if (authenticationStr != null) {
            SessionUserDetails user = JWTUtil.checkToken(authenticationStr);

            if (user != null) {
                setNearExpirationToken(user, response);

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null,
                        user.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        chain.doFilter(request, response);
    }

    private void setNearExpirationToken(SessionUserDetails user, HttpServletResponse response) {
        if (user.getToken() != null) {
            response.setHeader("X-RENEWAL-TOKEN", user.getToken());
        }
    }

    private String getAuthentication(HttpServletRequest httpRequest) {
        return httpRequest.getHeader("X-SERVICE-TOKEN");
    }
}