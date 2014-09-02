package org.isisaddons.module.security.shiro;

import javax.inject.Inject;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.isisaddons.module.security.dom.password.PasswordEncryptionService;
import org.isisaddons.module.security.dom.user.ApplicationUser;
import org.isisaddons.module.security.dom.user.ApplicationUsers;
import org.apache.isis.core.runtime.system.context.IsisContext;
import org.apache.isis.core.runtime.system.internal.InitialisationSession;
import org.apache.isis.core.runtime.system.persistence.PersistenceSession;
import org.apache.isis.core.runtime.system.transaction.IsisTransactionManager;
import org.apache.isis.core.runtime.system.transaction.TransactionalClosureWithReturn;
import org.apache.isis.core.runtime.system.transaction.TransactionalClosureWithReturnAbstract;

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

        final PrincipalForApplicationUser principal = lookupPrincipal(username);
        if(principal == null) {
            // could occur if 'autoCreateUser' is disabled.
            throw new CredentialsException("Unknown user/password combination");
        }
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
        if (principal.isDisabled()) {
            // if secondary realm, then doesn't prevent login but results in ZERO permissions.
            throw new DisabledAccountException();
        }

        final Object credentials = token.getCredentials();
        final String realmName = getName();
        return new AuthenticationInfoForApplicationUser(principal, realmName, credentials);
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        final PrincipalForApplicationUser urp = principals.oneByType(PrincipalForApplicationUser.class);
        if (urp == null) {
            return null;
        }
        return new AuthorizationInfoForApplicationUser(urp);
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
                    return applicationUsers.findUserByUsername(username);
                }
                else {
                    return applicationUsers.findUserByUsernameNoAutocreate(username);
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
        return !isPrimaryRealm();
    }

    /**
     * Whether to check password when authenticating.
     *
     * <p>
     *     Default implementation returns <code>true</code> if this realm is the {@link #isPrimaryRealm() primary realm}.
     * </p>
     *
     * <p>
     *     NOTE: This method has <code>public</code> visibility so that it can be easily overridden if required.
     * </p>
     */
    public boolean isCheckPasswords() {
        return isPrimaryRealm();
    }

    /**
     * Whether this realm is the primary realm, and thus, determining how password authentication and
     * user synchronization is performed.
     *
     * <p>
     *     If this realm is primary, then passwords are checked.  An implementation of
     *     {@link org.isisaddons.module.security.dom.password.PasswordEncryptionService} must be registered.
     * </p>
     * <p>
     *     If this realm is secondary, then passwords are NOT checked, but instead any user to be validated
     *     will be automatically created (in disabled status, with no roles).
     * </p>
     */
    public boolean isPrimaryRealm() {
        return ShiroUtils.isPrimaryRealm();
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
