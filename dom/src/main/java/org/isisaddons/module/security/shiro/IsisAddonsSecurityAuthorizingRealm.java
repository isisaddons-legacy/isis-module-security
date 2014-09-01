package org.isisaddons.module.security.shiro;

import java.util.*;
import javax.inject.Inject;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.permission.PermissionResolver;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.isisaddons.module.security.dom.feature.ApplicationFeatureId;
import org.isisaddons.module.security.dom.permission.ApplicationPermissionMode;
import org.isisaddons.module.security.dom.permission.ApplicationPermissionValueSet;
import org.isisaddons.module.security.dom.role.ApplicationRole;
import org.isisaddons.module.security.dom.user.ApplicationUser;
import org.isisaddons.module.security.dom.user.ApplicationUserStatus;
import org.isisaddons.module.security.dom.user.ApplicationUsers;
import org.apache.isis.core.runtime.system.context.IsisContext;
import org.apache.isis.core.runtime.system.internal.InitialisationSession;
import org.apache.isis.core.runtime.system.persistence.PersistenceSession;
import org.apache.isis.core.runtime.system.transaction.IsisTransactionManager;
import org.apache.isis.core.runtime.system.transaction.TransactionalClosureWithReturn;
import org.apache.isis.core.runtime.system.transaction.TransactionalClosureWithReturnAbstract;

public class IsisAddonsSecurityAuthorizingRealm extends AuthorizingRealm {

    //region > constructor

    /**
     * Configures a {@link org.apache.shiro.authz.permission.PermissionResolver} that knows how to process the
     * permission strings that are provided by Isis'
     * {@link org.apache.isis.core.runtime.authorization.standard.Authorizor} for Shiro.
     */
    public IsisAddonsSecurityAuthorizingRealm() {
        setPermissionResolver(new PermissionResolverForIsisShiroAuthorizor());
    }
    //endregion

    //region > doGetAuthenticationInfo, doGetAuthorizationInfo (Shiro API)

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        if (!(token instanceof UsernamePasswordToken)) {
            throw new UnknownAccountException("Unable to authenticate the provided token");
        }

        String username = ((UsernamePasswordToken) token).getUsername();
        final Object credentials = token.getCredentials();
        final UsernameAndRolesAndPermissions userPermissions = lookupUserPerms(username);
        if (userPermissions.isDisabled()) {
            throw new DisabledAccountException();
        }
        if (userPermissions == null) {
            throw new UnknownAccountException("Invalid user/password");
        }
        return new AuthenticationInfo() {
            @Override
            public PrincipalCollection getPrincipals() {
                return new SimplePrincipalCollection(userPermissions, getName());
            }

            @Override
            public Object getCredentials() {
                return credentials;
            }
        };
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        final UsernameAndRolesAndPermissions urp = principals.oneByType(UsernameAndRolesAndPermissions.class);
        if (urp == null) {
            return null;
        }
        return new AuthorizationInfo() {
            @Override
            public Collection<String> getRoles() {
                return urp != null? urp.roles : Collections.<String>emptyList();
            }

            @Override
            public Collection<String> getStringPermissions() {
                return Collections.emptyList();
            }

            @Override
            public Collection<Permission> getObjectPermissions() {
                final Permission o = new Permission() {
                    @Override
                    public boolean implies(Permission p) {
                        if (!(p instanceof PermissionForMember)) {
                            return false;
                        }
                        final PermissionForMember pfm = (PermissionForMember) p;
                        return urp.permissionSet.grants(pfm.featureId, pfm.mode);
                    }
                };
                return Collections.singleton(o);
            }
        };
    }
    //endregion

    //region > UsernameAndRolesAndPermissions 

    /**
     * Acts as the Principal for this Realm, meaning that it is returned from
     * {@link #doGetAuthenticationInfo(org.apache.shiro.authc.AuthenticationToken) authentication}, and passed into
     * {@link #doGetAuthorizationInfo(org.apache.shiro.subject.PrincipalCollection) authorization}.
     *
     * <p>
     *     To minimize database lookups, holds the user, corresponding roles and the full set of permissions
     *     (all as value objects).  The permissions are eagerly looked up during
     *     {@link #doGetAuthenticationInfo(org.apache.shiro.authc.AuthenticationToken) authentication} and so the
     *     {@link #doGetAuthorizationInfo(org.apache.shiro.subject.PrincipalCollection) authorization} merely involves
     *     creating an adapter object for the appropriate Shiro API.
     * </p>
     */
    static class UsernameAndRolesAndPermissions  {

        public static UsernameAndRolesAndPermissions from(ApplicationUser applicationUser) {
            if(applicationUser == null) {
                return null;
            }
            final Set<String> roles = setOf(Lists.newArrayList(Iterables.transform(applicationUser.getRoles(), ApplicationRole.Functions.GET_NAME)));
            final ApplicationPermissionValueSet permissionSet = applicationUser.getPermissionSet();
            return new UsernameAndRolesAndPermissions(roles, applicationUser.getStatus(), permissionSet);
        }

        private final Set<String> roles;
        private final ApplicationUserStatus status;
        private final ApplicationPermissionValueSet permissionSet;

        UsernameAndRolesAndPermissions(
                final Set<String> roles,
                final ApplicationUserStatus status,
                final ApplicationPermissionValueSet applicationPermissionValueSet) {
            this.permissionSet = applicationPermissionValueSet;
            this.roles = roles;
            this.status = status;
        }

        public boolean isDisabled() {
            return status == ApplicationUserStatus.DISABLED;
        }
    }


    /**
     * @param username
     */
    private UsernameAndRolesAndPermissions lookupUserPerms(final String username) {
        return execute(new TransactionalClosureWithReturnAbstract<UsernameAndRolesAndPermissions>() {
            @Override
            public UsernameAndRolesAndPermissions execute() {
                final ApplicationUser applicationUser = lookupUser();
                return UsernameAndRolesAndPermissions.from(applicationUser);
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

    //region > autoCreateUsers (property)

    private boolean autoCreateUsers;

    /**
     * Whether to auto-create users when looking up.
     *
     * <p>
     *     If this realm is being used as a secondary realm (eg to LDAP), then should enable so will automatically sync.
     *     By default, is off.
     * </p>
     */
    public boolean isAutoCreateUsers() {
        return autoCreateUsers;
    }

    public void setAutoCreateUsers(boolean autoCreateUsers) {
        this.autoCreateUsers = autoCreateUsers;
    }
    //endregion

    //region > PermissionResolver & Permission implementations (Shiro integration)

    static class PermissionResolverForIsisShiroAuthorizor implements PermissionResolver {

        /**
         * Expects in format <code>package:className:methodName:r|w</code>
         */
        @Override
        public Permission resolvePermission(String permissionString) {
            return new PermissionForMember(permissionString);
        }
    }

    static class PermissionForMember implements org.apache.shiro.authz.Permission {

        private final ApplicationFeatureId featureId;
        private final ApplicationPermissionMode mode;

        /**
         * Expects in format <code>package:className:methodName:r|w</code>
         */
        public PermissionForMember(String permissionString) {
            final String[] split = permissionString.split("\\:");
            if(split.length == 4) {
                String packageName = split[0];
                String className = split[1];
                String memberName = split[2];
                this.featureId = ApplicationFeatureId.newMember(packageName + "." + className, memberName);

                ApplicationPermissionMode mode = modeFrom(split[3]);
                if(mode != null) {
                    this.mode = mode;
                    return;
                }
            }
            throw new IllegalArgumentException("Invalid format for permission: " + permissionString + "; expected 'packageName:className:methodName:r|w");
        }

        private static ApplicationPermissionMode modeFrom(String s) {
            if("r".equals(s)) {
                return ApplicationPermissionMode.VIEWING;
            }
            if("w".equals(s)) {
                return ApplicationPermissionMode.CHANGING;
            }
            return null;
        }

        /**
         */
        @Override
        public boolean implies(Permission p) {
            return false;
        }
    }
    //endregion

    //region > helpers
    private static Set<String> setOf(String... strings) {
        final List<String> elements = Arrays.asList(strings);
        return setOf(elements);
    }

    private static Set<String> setOf(List<String> strings) {
        return Sets.newTreeSet(strings);
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
