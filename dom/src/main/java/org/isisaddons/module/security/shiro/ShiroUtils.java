package org.isisaddons.module.security.shiro;

import java.util.Collection;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.UnavailableSecurityManagerException;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.realm.Realm;

public final class ShiroUtils {

    private ShiroUtils() {
    }

    public static synchronized RealmSecurityManager getSecurityManager() {
        org.apache.shiro.mgt.SecurityManager securityManager;
        try {
            securityManager = SecurityUtils.getSecurityManager();
        } catch(UnavailableSecurityManagerException ex) {
            throw new AuthenticationException(ex);
        }
        if(!(securityManager instanceof RealmSecurityManager)) {
            throw new AuthenticationException();
        }
        return (RealmSecurityManager) securityManager;
    }

    public static boolean isPrimaryRealm() {
        final RealmSecurityManager securityManager = getSecurityManager();
        final Collection<Realm> realms = securityManager.getRealms();
        if (realms.isEmpty()) {
            return false;
        }

        final Realm firstRealm = realms.iterator().next();
        return IsisModuleSecurityRealm.class.isAssignableFrom(firstRealm.getClass());
    }

}
