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

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.Permission;

import org.isisaddons.module.security.dom.permission.ApplicationPermissionValueSet;
import org.isisaddons.module.security.dom.role.ApplicationRole;
import org.isisaddons.module.security.dom.user.AccountType;
import org.isisaddons.module.security.dom.user.ApplicationUser;
import org.isisaddons.module.security.dom.user.ApplicationUserStatus;

/**
 * Acts as the Principal for the {@link IsisModuleSecurityRealm}, meaning that it is returned from
 * {@link IsisModuleSecurityRealm#doGetAuthenticationInfo(org.apache.shiro.authc.AuthenticationToken) authentication}, and passed into
 * {@link IsisModuleSecurityRealm#doGetAuthorizationInfo(org.apache.shiro.subject.PrincipalCollection) authorization}.
 *
 * <p>
 *     To minimize database lookups, holds the user, corresponding roles and the full set of permissions
 *     (all as value objects).  The permissions are eagerly looked up during
 *     {@link IsisModuleSecurityRealm#doGetAuthenticationInfo(org.apache.shiro.authc.AuthenticationToken) authentication} and so the
 *     {@link IsisModuleSecurityRealm#doGetAuthorizationInfo(org.apache.shiro.subject.PrincipalCollection) authorization} merely involves
 *     creating an adapter object for the appropriate Shiro API.
 * </p>
 *
 * TODO: this should probably implement java.security.Principal so that it doesn't get wrapped in a
 * ShiroHttpServletRequest.ObjectPrincipal.  Such a change would need some testing to avoid regressions, though.
 */
class PrincipalForApplicationUser implements AuthorizationInfo {

    public static PrincipalForApplicationUser from(ApplicationUser applicationUser) {
        if(applicationUser == null) {
            return null;
        }
        final String username = applicationUser.getName();
        final String encryptedPassword = applicationUser.getEncryptedPassword();
        final AccountType accountType = applicationUser.getAccountType();
        final Set<String> roles = Sets.newTreeSet(Lists.newArrayList(Iterables.transform(applicationUser.getRoles(), ApplicationRole.Functions.GET_NAME)));
        final ApplicationPermissionValueSet permissionSet = applicationUser.getPermissionSet();
        return new PrincipalForApplicationUser(username, encryptedPassword, accountType, applicationUser.getStatus(), roles, permissionSet);
    }

    private final String username;
    private final Set<String> roles;
    private final String encryptedPassword;
    private final ApplicationUserStatus status;
    private final AccountType accountType;
    private final ApplicationPermissionValueSet permissionSet;

    PrincipalForApplicationUser(
            final String username,
            final String encryptedPassword,
            final AccountType accountType,
            final ApplicationUserStatus status,
            final Set<String> roles,
            final ApplicationPermissionValueSet applicationPermissionValueSet) {
        this.username = username;
        this.encryptedPassword = encryptedPassword;
        this.accountType = accountType;
        this.roles = roles;
        this.status = status;
        this.permissionSet = applicationPermissionValueSet;
    }

    public boolean isDisabled() {
        return getStatus() == ApplicationUserStatus.DISABLED;
    }

    @Override
    public Set<String> getRoles() {
        return roles;
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
                return getPermissionSet().grants(pfm.getFeatureId(), pfm.getMode());
            }
        };
        return Collections.singleton(o);
    }

    ApplicationUserStatus getStatus() {
        return status;
    }

    String getUsername() {
        return username;
    }

    String getEncryptedPassword() {
        return encryptedPassword;
    }

    ApplicationPermissionValueSet getPermissionSet() {
        return permissionSet;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    /**
     * When wrapped by ShiroHttpServletRequest.ObjectPrincipal, the principal's name is derived by calling toString().
     *
     *  TODO: this should probably implement java.security.Principal so that it doesn't get wrapped in a
     *  ShiroHttpServletRequest.ObjectPrincipal.  Such a change would need some testing to avoid regressions, though.
     *
     */
    @Override
    public String toString() {
        return getUsername();
    }
}
