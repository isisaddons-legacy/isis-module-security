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

import java.util.Set;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.isisaddons.module.security.dom.permission.ApplicationPermissionValueSet;
import org.isisaddons.module.security.dom.role.ApplicationRole;
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
 */
class PrincipalForApplicationUser {

    public static PrincipalForApplicationUser from(ApplicationUser applicationUser) {
        if(applicationUser == null) {
            return null;
        }
        final String username = applicationUser.getName();
        final String encryptedPassword = applicationUser.getEncryptedPassword();
        final Set<String> roles = Sets.newTreeSet(Lists.newArrayList(Iterables.transform(applicationUser.getRoles(), ApplicationRole.Functions.GET_NAME)));
        final ApplicationPermissionValueSet permissionSet = applicationUser.getPermissionSet();
        return new PrincipalForApplicationUser(username, encryptedPassword, roles, applicationUser.getStatus(), permissionSet);
    }

    private final Set<String> roles;
    private final ApplicationUserStatus status;
    private final String username;
    private final String encryptedPassword;
    private final ApplicationPermissionValueSet permissionSet;

    PrincipalForApplicationUser(
            final String username,
            final String encryptedPassword,
            final Set<String> roles,
            final ApplicationUserStatus status,
            final ApplicationPermissionValueSet applicationPermissionValueSet) {
        this.username = username;
        this.encryptedPassword = encryptedPassword;
        this.roles = roles;
        this.status = status;
        this.permissionSet = applicationPermissionValueSet;
    }

    public boolean isDisabled() {
        return getStatus() == ApplicationUserStatus.DISABLED;
    }

    Set<String> getRoles() {
        return roles;
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
}
