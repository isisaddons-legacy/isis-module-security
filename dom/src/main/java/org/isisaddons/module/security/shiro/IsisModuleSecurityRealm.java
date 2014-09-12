/*
 *  Copyright 2014 Dan Haywood
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.isisaddons.module.security.shiro;

import javax.inject.Inject;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.CredentialsException;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthenticatingRealm;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import org.apache.isis.core.runtime.system.context.IsisContext;
import org.apache.isis.core.runtime.system.internal.InitialisationSession;
import org.apache.isis.core.runtime.system.persistence.PersistenceSession;
import org.apache.isis.core.runtime.system.transaction.IsisTransactionManager;
import org.apache.isis.core.runtime.system.transaction.TransactionalClosureWithReturn;
import org.apache.isis.core.runtime.system.transaction.TransactionalClosureWithReturnAbstract;

import org.isisaddons.module.security.dom.password.PasswordEncryptionService;
import org.isisaddons.module.security.dom.user.ApplicationUser;
import org.isisaddons.module.security.dom.user.ApplicationUsers;

public class IsisModuleSecurityRealm extends AuthorizingRealm {


    //region > constructor

    /**
     * Configures a {@link org.apache.shiro.authz.permission.PermissionResolver} that knows how to process the
     * permission strings that are provided by Isis'
     * {@link org.apache.isis.core.runtime.authorization.standard.Authorizor} for Shiro.
     */
    public IsisModuleSecurityRealm() {
        setPermissionResolver(new PermissionResolverForIsisShiroAuthorizor());
    }
    //endregion

    private AuthenticatingRealm delegateAuthenticationRealm;
    public AuthenticatingRealm getDelegateAuthenticationRealm() {
        return delegateAuthenticationRealm;
    }
    public void setDelegateAuthenticationRealm(AuthenticatingRealm delegateRealm) {
        this.delegateAuthenticationRealm = delegateRealm;
    }

    //region > doGetAuthenticationInfo, doGetAuthorizationInfo (Shiro API)

    
    /**
     * In order to provide an attacker with additional information, the exceptions thrown here deliberately have
     * few (or no) details in their exception message.  Similarly, the generic
     * {@link org.apache.shiro.authc.CredentialsException} is thrown for both a non-existent user and also an
     * invalid password.
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        
        AuthenticationInfo delegateAccount;
        AuthenticationException delegateEx;
        try {
            delegateAccount = hasDelegateRealm() ? delegateAuthenticationRealm.getAuthenticationInfo(token) : null;
            delegateEx = null;
        } catch(AuthenticationException ex) {
            delegateAccount = null;
            delegateEx = ex;
        }

        
        String username;
        char[] password;
        if(delegateAccount != null) {
            username = delegateAccount.getPrincipals().oneByType(String.class);
            password = null; // unused
        } else {
            if (!(token instanceof UsernamePasswordToken)) {
                throw new AuthenticationException();
            }
            
            final UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) token;
            username = usernamePasswordToken.getUsername();
            password = usernamePasswordToken.getPassword();
        }
        
        final PrincipalForApplicationUser principal = lookupPrincipal(username);
        if(principal == null) {
            // could occur if 'autoCreateUser' is disabled.
            throw new CredentialsException("Unknown user/password combination");
        }

        if(delegateAccount == null) {
            if(isCheckPasswords()) {
                final CheckPasswordResult result = checkPassword(password, principal.getEncryptedPassword());
                switch (result) {
                    case OK:
                        break;
                    case BAD_PASSWORD:
                        throw new CredentialsException("Unknown user/password combination");
                    case NO_PASSWORD_ENCRYPTION_SERVICE_CONFIGURED:
                        throw new AuthenticationException();
                    default:
                        throw new AuthenticationException();
                }
            }
        }
        
        if (principal.isDisabled()) {
            // if secondary realm, then doesn't prevent login but results in ZERO permissions.
            throw new DisabledAccountException();
        }
        
        final Object credentials = token.getCredentials();
        final String realmName = getName();
        return new AuthInfoForApplicationUser(principal, realmName, credentials);
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        final PrincipalForApplicationUser urp = principals.oneByType(PrincipalForApplicationUser.class);
        if (urp == null) {
            return null;
        }
        return urp;
    }

    //endregion

    //region > lookupPrincipal


    /**
     * @param username
     */
    private PrincipalForApplicationUser lookupPrincipal(final String username) {
        return execute(new TransactionalClosureWithReturnAbstract<PrincipalForApplicationUser>() {
            @Override
            public PrincipalForApplicationUser execute() {
                final ApplicationUser applicationUser = lookupUser();
                return PrincipalForApplicationUser.from(applicationUser);
            }

            private ApplicationUser lookupUser() {
                if (isAutoCreateUsers()) {
                    return applicationUsers.findOrCreateUserByUsername(username);
                }
                else {
                    return applicationUsers.findUserByUsername(username);
                }
            }

            @Inject
            private ApplicationUsers applicationUsers;
        });
    }

    //endregion

    //region > checkPassword

    private static enum CheckPasswordResult {
        OK,
        BAD_PASSWORD,
        NO_PASSWORD_ENCRYPTION_SERVICE_CONFIGURED
    }

    private CheckPasswordResult checkPassword(final char[] candidate, final String actualEncryptedPassword) {
        return execute(new TransactionalClosureWithReturnAbstract<CheckPasswordResult>() {
            @Override
            public CheckPasswordResult execute() {
                if (passwordEncryptionService == null) {
                    return CheckPasswordResult.NO_PASSWORD_ENCRYPTION_SERVICE_CONFIGURED;
                }
                return passwordEncryptionService.matches(new String(candidate), actualEncryptedPassword)
                        ? CheckPasswordResult.OK
                        : CheckPasswordResult.BAD_PASSWORD;
            }

            @Inject
            private PasswordEncryptionService passwordEncryptionService;
        });
    }

    //endregion

    //region > execute (Isis integration)

    <V> V execute(final TransactionalClosureWithReturn<V> closure) {
        try {
            IsisContext.openSession(new InitialisationSession());
            PersistenceSession persistenceSession = getPersistenceSession();
            persistenceSession.getServicesInjector().injectServicesInto(closure);
            return doExecute(closure);
        } finally {
            IsisContext.closeSession();
        }
    }

    <V> V doExecute(final TransactionalClosureWithReturn<V> closure) {
        final PersistenceSession persistenceSession = getPersistenceSession();
        final IsisTransactionManager transactionManager = getTransactionManager(persistenceSession);
        return transactionManager.executeWithinTransaction(closure);
    }

    //endregion

    //region > autoCreateUsers, checkPasswords, isPrimaryRealm

    
    /**
     * Whether to auto-create users when looking up.
     *
     * <p>
     *     Default implementation returns <code>true</code> if this realm is <b>NOT</b> the {@link #isPrimaryRealm() primary realm},
     *     for example if configured as a secondary realm with another realm (eg LDAP) performing the authentication.
     * </p>
     * <p>
     *     NOTE: This method has <code>public</code> visibility so that it can be easily overridden if required.
     * </p>
     */
    public boolean isAutoCreateUsers() {
        return !isPrimaryRealm() || hasDelegateRealm();
    }


    public boolean isPrimaryRealm() {
        return ShiroUtils.isPrimaryRealm();
    }
    
    private boolean hasDelegateRealm() {
        return delegateAuthenticationRealm != null;
    }

    //endregion

    //region > lookup dependencies from Isis runtime
    protected PersistenceSession getPersistenceSession() {
        return IsisContext.getPersistenceSession();
    }

    protected IsisTransactionManager getTransactionManager(PersistenceSession persistenceSession) {
        return persistenceSession.getTransactionManager();
    }

    //endregion

}
