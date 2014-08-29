package org.isisaddons.module.security.fixture.scripts.permission;

import javax.inject.Inject;
import org.isisaddons.module.security.dom.permission.ApplicationPermissionMode;
import org.isisaddons.module.security.dom.permission.ApplicationPermissionRule;
import org.isisaddons.module.security.dom.role.ApplicationRole;
import org.isisaddons.module.security.dom.role.ApplicationRoles;
import org.isisaddons.module.security.fixture.scripts.roles.GuestRoleFixture;
import org.apache.isis.applib.fixturescripts.FixtureScript;

public class GuestRole_AllowViewing_ExampleDomPackage extends FixtureScript {

    @Override
    protected void execute(ExecutionContext executionContext) {

        final ApplicationRole role = applicationRoles.findRoleByName(GuestRoleFixture.ROLE_NAME);

        executionContext.add(this, role.addPackage(ApplicationPermissionRule.ALLOW, ApplicationPermissionMode.VIEWING, "org.isisaddons.module.security.fixture.dom"));
    }

    @Inject
    private ApplicationRoles applicationRoles;

}
