package org.isisaddons.module.security.shiro;

import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.permission.PermissionResolver;

class PermissionResolverForIsisShiroAuthorizor implements PermissionResolver {

    /**
     * Expects in format <code>package:className:methodName:r|w</code>
     */
    @Override
    public Permission resolvePermission(String permissionString) {
        return new PermissionForMember(permissionString);
    }
}
