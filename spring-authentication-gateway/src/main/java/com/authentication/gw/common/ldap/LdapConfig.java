package com.authentication.gw.common.ldap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

@Configuration
public class LdapConfig {

    @Value("${ldap.url}")
    private String url;

    @Value("${ldap.base}")
    private String base;

    @Value("${ldap.username}")
    private String username;

    @Value("${ldap.password}")
    private String password;

    @Bean
    public LdapTemplate ldapTemplate() {
        return new LdapTemplate(contextSource());
    }

    @Bean
    public LdapContextSource contextSource() {
        LdapContextSource contextSource = new LdapContextSource();
        contextSource.setUserDn(username);
        contextSource.setPassword(password);
        contextSource.setUrl(url);
        contextSource.setBase(base);
        return contextSource;
    }
}
