package org.isisaddons.module.security.shiro;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;

class AuthenticationInfoForApplicationUser implements AuthenticationInfo {
    private final PrincipalForApplicationUser principal;
    private final String realmName;
    private final Object credentials;

    public AuthenticationInfoForApplicationUser(PrincipalForApplicationUser principal, String realmName, Object credentials) {
        this.principal = principal;
        this.realmName = realmName;
        this.credentials = credentials;
    }

    @Override
    public PrincipalCollection getPrincipals() {
        return new SimplePrincipalCollection(principal, realmName);
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }
}
