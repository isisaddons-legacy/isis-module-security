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
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.Permission;

class AuthorizationInfoForApplicationUser implements AuthorizationInfo {
    private final PrincipalForApplicationUser urp;

    public AuthorizationInfoForApplicationUser(PrincipalForApplicationUser urp) {
        this.urp = urp;
    }

    @Override
    public Collection<String> getRoles() {
        return urp != null? urp.getRoles() : Collections.<String>emptyList();
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
                return urp.getPermissionSet().grants(pfm.getFeatureId(), pfm.getMode());
            }
        };
        return Collections.singleton(o);
    }
}
