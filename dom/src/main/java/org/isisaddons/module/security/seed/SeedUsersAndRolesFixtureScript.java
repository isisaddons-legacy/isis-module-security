package org.isisaddons.module.security.seed;

import java.util.List;
import javax.inject.Inject;
import org.isisaddons.module.security.dom.role.ApplicationRole;
import org.isisaddons.module.security.dom.role.ApplicationRoles;
import org.isisaddons.module.security.dom.user.ApplicationUser;
import org.isisaddons.module.security.dom.user.ApplicationUsers;
import org.isisaddons.module.security.seed.scripts.*;
import org.apache.isis.applib.fixturescripts.FixtureScript;

/**
 * This fixture script will be run automatically on start-up by virtue of the fact that the
 * {@link org.isisaddons.module.security.seed.SeedSecurityModuleService} is a
 * {@link org.apache.isis.applib.annotation.DomainService} and calls the setup during its
 * {@link org.isisaddons.module.security.seed.SeedSecurityModuleService#init() init} ({@link javax.annotation.PostConstruct}) method.
 */
public class SeedUsersAndRolesFixtureScript extends FixtureScript {

    @Override
    protected void execute(ExecutionContext executionContext) {

        // only run if there is no data.
        final List<ApplicationRole> roleList = applicationRoles.allRoles();
        if(!roleList.isEmpty()) {
            return;
        }
        final List<ApplicationUser> userList = applicationUsers.allUsers();
        if(!userList.isEmpty()) {
            return;
        }

        // security module
        execute(new IsisModuleSecurityAdminRoleAndPermissions(), executionContext);

        execute(new IsisModuleSecurityFixtureRoleAndPermissions(), executionContext);
        execute(new IsisModuleSecurityRegularUserRoleAndPermissions(), executionContext);

        execute(new IsisModuleSecurityAdminUser(), executionContext);

        // isis applib
        execute(new IsisApplibFixtureResultsRoleAndPermissions(), executionContext);
    }

    //region > injected
    @Inject
    ApplicationRoles applicationRoles;
    @Inject
    ApplicationUsers applicationUsers;
    //endregion
}
