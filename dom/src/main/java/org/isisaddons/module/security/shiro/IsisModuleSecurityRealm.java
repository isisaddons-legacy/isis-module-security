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

import org.apache.isis.core.runtime.system.context.IsisContext;
import org.apache.isis.core.runtime.system.persistence.PersistenceSession;
import org.apache.isis.core.runtime.system.session.IsisSessionFactory;
import org.apache.isis.core.runtime.system.transaction.IsisTransactionManager;
import org.apache.isis.core.runtime.system.transaction.TransactionalClosureWithReturn;
import org.apache.isis.core.runtime.system.transaction.TransactionalClosureWithReturnAbstract;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthenticatingRealm;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.isisaddons.module.security.dom.password.PasswordEncryptionService;
import org.isisaddons.module.security.dom.user.AccountType;
import org.isisaddons.module.security.dom.user.ApplicationUser;
import org.isisaddons.module.security.dom.user.ApplicationUserRepository;

import javax.inject.Inject;
import java.util.concurrent.Callable;

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


    //region > doGetAuthenticationInfo, doGetAuthorizationInfo (Shiro API)


    /**
     * In order to provide an attacker with additional information, the exceptions thrown here deliberately have
     * few (or no) details in their exception message.  Similarly, the generic
     * {@link org.apache.shiro.authc.CredentialsException} is thrown for both a non-existent user and also an
     * invalid password.
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {

        if (!(token instanceof UsernamePasswordToken)) {
            throw new AuthenticationException();
        }

        final UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) token;
        String username = usernamePasswordToken.getUsername();
        char[] password = usernamePasswordToken.getPassword();

        // lookup from database, for roles/perms, but also
        // determine how to authenticate (delegate or local), whether disabled
        final PrincipalForApplicationUser principal = lookupPrincipal(username,
                (hasDelegateAuthenticationRealm() && getAutoCreateUser()));
        if (principal == null) {
            // if no delegate authentication
            throw new CredentialsException("Unknown user/password combination");
        }

        if (principal.isDisabled()) {
            // this is the default if delegated account and automatically created
            throw new DisabledAccountException();
        }

        if(principal.getAccountType() == AccountType.DELEGATED) {
            AuthenticationInfo delegateAccount = null;
            if (hasDelegateAuthenticationRealm()) {
                try {
                    delegateAccount = delegateAuthenticationRealm.getAuthenticationInfo(token);
                } catch (AuthenticationException ex) {
                    // fall through
                }
            }
            if(delegateAccount == null) {
                throw new CredentialsException("Unknown user/password combination");
            }
        } else {
            final CheckPasswordResult result = checkPassword(password, principal.getEncryptedPassword());
            switch (result) {
                case OK:
                    break;
                case BAD_PASSWORD:
                    throw new CredentialsException("Unknown user/password combination");
                case NO_PASSWORD_ENCRYPTION_SERVICE_CONFIGURED:
                    throw new AuthenticationException("No password encryption service is installed");
                default:
                    throw new AuthenticationException();
            }
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
     * @param autoCreateUser
     */
    private PrincipalForApplicationUser lookupPrincipal(final String username, final boolean autoCreateUser) {
        return execute(new TransactionalClosureWithReturnAbstract<PrincipalForApplicationUser>() {
            @Override
            public PrincipalForApplicationUser execute() {
                final ApplicationUser applicationUser = lookupUser();
                return PrincipalForApplicationUser.from(applicationUser);
            }

            private ApplicationUser lookupUser() {
                if (autoCreateUser) {
                    return applicationUserRepository.findOrCreateUserByUsername(username);
                } else {
                    return applicationUserRepository.findByUsername(username);
                }
            }

            @Inject
            private ApplicationUserRepository applicationUserRepository;
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

    //region > delegateRealm


    private AuthenticatingRealm delegateAuthenticationRealm;
    public AuthenticatingRealm getDelegateAuthenticationRealm() {
        return delegateAuthenticationRealm;
    }
    public void setDelegateAuthenticationRealm(AuthenticatingRealm delegateRealm) {
        this.delegateAuthenticationRealm = delegateRealm;
    }

    public boolean hasDelegateAuthenticationRealm() {
        return delegateAuthenticationRealm != null;
    }

    //endregion

    //region > autoCreateUser

    private boolean autoCreateUser = true;

    public boolean getAutoCreateUser() {
        return autoCreateUser;
    }

    public void setAutoCreateUser(boolean autoCreateUser) {
        this.autoCreateUser = autoCreateUser;
    }

    //endregion

    //region > execute (Isis integration)

    <V> V execute(final TransactionalClosureWithReturn<V> closure) {
        return getSessionFactory().doInSession(
                new Callable<V>() {
                    @Override
                    public V call() {
                        PersistenceSession persistenceSession = getPersistenceSession();
                        persistenceSession.getServicesInjector().injectServicesInto(closure);
                        return doExecute(closure);
                    }
                }
        );
    }

    <V> V doExecute(final TransactionalClosureWithReturn<V> closure) {
        final PersistenceSession persistenceSession = getPersistenceSession();
        final IsisTransactionManager transactionManager = getTransactionManager(persistenceSession);
        return transactionManager.executeWithinTransaction(closure);
    }

    //endregion

    //region > lookup dependencies from Isis runtime
    protected PersistenceSession getPersistenceSession() {
        return getSessionFactory().getCurrentSession().getPersistenceSession();
    }

    protected IsisTransactionManager getTransactionManager(PersistenceSession persistenceSession) {
        return persistenceSession.getTransactionManager();
    }

    protected IsisSessionFactory getSessionFactory() {
        return IsisContext.getSessionFactory();
    }
    //endregion

}
