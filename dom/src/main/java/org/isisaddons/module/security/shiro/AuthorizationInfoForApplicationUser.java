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
