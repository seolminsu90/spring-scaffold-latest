package com.authentication.gw.common.ldap;

import org.springframework.ldap.NamingException;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextAdapter;

import javax.naming.directory.Attributes;

public class LdapUserContextMapper implements ContextMapper<LdapUser> {
    @Override
    public LdapUser mapFromContext(Object ctx) throws NamingException, javax.naming.NamingException {
        Attributes attributes = ((DirContextAdapter) ctx).getAttributes();
        LdapUser user = new LdapUser();
        user.setUid(get(attributes, "uid"));
        user.setName(get(attributes, "name"));
        user.setTitle(get(attributes, "title"));
        user.setEmail(get(attributes, "mail"));

        return user;
    }

    private String get(Attributes attributes, String key) throws javax.naming.NamingException {
        return String.valueOf(attributes.get(key).get(0));
    }
}

