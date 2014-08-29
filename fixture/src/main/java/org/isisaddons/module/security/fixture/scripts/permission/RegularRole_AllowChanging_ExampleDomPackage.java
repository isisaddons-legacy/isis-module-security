package org.isisaddons.module.security.fixture.scripts.permission;

import javax.inject.Inject;
import org.isisaddons.module.security.dom.permission.ApplicationPermissionMode;
import org.isisaddons.module.security.dom.permission.ApplicationPermissionRule;
import org.isisaddons.module.security.dom.role.ApplicationRole;
import org.isisaddons.module.security.dom.role.ApplicationRoles;
import org.isisaddons.module.security.fixture.scripts.roles.RegularRoleFixture;
import org.apache.isis.applib.fixturescripts.FixtureScript;

public class RegularRole_AllowChanging_ExampleDomPackage extends FixtureScript {

    @Override
    protected void execute(ExecutionContext executionContext) {

        final ApplicationRole role = applicationRoles.findRoleByName(RegularRoleFixture.ROLE_NAME);

        executionContext.add(this, role.addPackage(ApplicationPermissionRule.ALLOW, ApplicationPermissionMode.CHANGING, "org.isisaddons.module.security.fixture.dom"));
    }

    @Inject
    private ApplicationRoles applicationRoles;

}
