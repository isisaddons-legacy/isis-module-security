package org.isisaddons.module.security.seed;

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

        // security module
        execute(new IsisModuleSecurityAdminRoleAndPermissions(), executionContext);

        execute(new IsisModuleSecurityFixtureRoleAndPermissions(), executionContext);
        execute(new IsisModuleSecurityRegularUserRoleAndPermissions(), executionContext);

        execute(new IsisModuleSecurityAdminUser(), executionContext);

        // isis applib
        execute(new IsisApplibFixtureResultsRoleAndPermissions(), executionContext);
    }
}
