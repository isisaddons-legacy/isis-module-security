package org.isisaddons.module.security.fixture.scripts.permission;

import javax.inject.Inject;
import org.isisaddons.module.security.dom.permission.ApplicationPermissionMode;
import org.isisaddons.module.security.dom.permission.ApplicationPermissionRule;
import org.isisaddons.module.security.dom.role.ApplicationRole;
import org.isisaddons.module.security.dom.role.ApplicationRoles;
import org.isisaddons.module.security.fixture.scripts.roles.NoFixtureScriptsRoleFixture;
import org.apache.isis.applib.fixturescripts.FixtureScript;

public class NoFixtureScriptsRole_VetoViewing_FixtureScriptsPackage extends FixtureScript {

    @Override
    protected void execute(ExecutionContext executionContext) {

        final ApplicationRole role = applicationRoles.findRoleByName(NoFixtureScriptsRoleFixture.ROLE_NAME);

        executionContext.add(this, role.addPackage(ApplicationPermissionRule.VETO, ApplicationPermissionMode.VIEWING, "org.apache.isis.applib.fixturescripts"));
    }

    @Inject
    private ApplicationRoles applicationRoles;

}
