package org.isisaddons.module.security.webapp;

import org.apache.isis.applib.annotation.DomainService;
import org.isisaddons.module.security.dom.role.ApplicationRole;
import org.isisaddons.module.security.dom.role.ApplicationRoles;
import org.isisaddons.module.security.fixture.scripts.roles.ExampleFixtureScriptsRoleAndPermissions;
import org.isisaddons.module.security.userreg.SecurityModuleAppUserRegistrationServiceAbstract;

import javax.inject.Inject;

/**
 * An override of the default impl of {@link org.apache.isis.applib.services.userreg.UserRegistrationService}
 * that uses {@link org.isisaddons.module.security.fixture.scripts.roles.ExampleFixtureScriptsRoleAndPermissions#ROLE_NAME}
 * as initial role
 */
@DomainService
public class AppUserRegistrationService extends SecurityModuleAppUserRegistrationServiceAbstract {

    @Override
    protected ApplicationRole getInitialRole() {
        ApplicationRole role = applicationRoles.findRoleByName(ExampleFixtureScriptsRoleAndPermissions.ROLE_NAME);
        return role;
    }

    @Inject
    private ApplicationRoles applicationRoles;
}
