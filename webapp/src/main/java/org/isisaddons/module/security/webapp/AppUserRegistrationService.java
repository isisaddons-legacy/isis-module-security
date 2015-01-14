package org.isisaddons.module.security.webapp;

import java.util.Collections;
import java.util.Set;
import javax.inject.Inject;
import org.isisaddons.module.security.dom.role.ApplicationRole;
import org.isisaddons.module.security.dom.role.ApplicationRoles;
import org.isisaddons.module.security.fixture.scripts.roles.ExampleFixtureScriptsRoleAndPermissions;
import org.isisaddons.module.security.fixture.scripts.roles.ExampleRegularRoleAndPermissions;
import org.isisaddons.module.security.userreg.SecurityModuleAppUserRegistrationServiceAbstract;
import org.apache.isis.applib.annotation.DomainService;

/**
 * An override of the default impl of {@link org.apache.isis.applib.services.userreg.UserRegistrationService}
 * that uses {@link org.isisaddons.module.security.fixture.scripts.roles.ExampleFixtureScriptsRoleAndPermissions#ROLE_NAME}
 * as initial role
 */
@DomainService
public class AppUserRegistrationService extends SecurityModuleAppUserRegistrationServiceAbstract {

    @Override
    protected ApplicationRole getInitialRole() {
        return findRole(ExampleFixtureScriptsRoleAndPermissions.ROLE_NAME);
    }

    @Override
    protected Set<ApplicationRole> getAdditionalInitialRoles() {
        return Collections.singleton(findRole(ExampleRegularRoleAndPermissions.ROLE_NAME));
    }

    private ApplicationRole findRole(final String roleName) {
        return applicationRoles.findRoleByName(roleName);
    }


    @Inject
    private ApplicationRoles applicationRoles;
}
