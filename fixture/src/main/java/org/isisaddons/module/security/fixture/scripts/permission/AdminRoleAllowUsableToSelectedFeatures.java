package org.isisaddons.module.security.fixture.scripts.permission;

import javax.inject.Inject;
import org.isisaddons.module.security.dom.actor.ApplicationRole;
import org.isisaddons.module.security.dom.actor.ApplicationRoles;
import org.isisaddons.module.security.dom.permission.ApplicationPermissionMode;
import org.isisaddons.module.security.dom.permission.ApplicationPermissionRule;
import org.isisaddons.module.security.fixture.dom.ExampleSecuredEntity;
import org.isisaddons.module.security.fixture.scripts.roles.AdminRoleFixture;
import org.apache.isis.applib.fixturescripts.FixtureScript;

public class AdminRoleAllowUsableToSelectedFeatures extends FixtureScript {

    @Override
    protected void execute(ExecutionContext executionContext) {

        final ApplicationRole adminRole = applicationRoles.findRoleByName(AdminRoleFixture.ROLE_NAME);

        executionContext.add(this, adminRole.addPackage(ApplicationPermissionRule.ALLOW, ApplicationPermissionMode.CHANGING, "org"));
        executionContext.add(this, adminRole.addClassOrMember(ApplicationPermissionRule.ALLOW, ApplicationPermissionMode.CHANGING, ExampleSecuredEntity.class.getPackage().getName(), ExampleSecuredEntity.class.getSimpleName(), null));
        executionContext.add(this, adminRole.addClassOrMember(ApplicationPermissionRule.ALLOW, ApplicationPermissionMode.CHANGING, ExampleSecuredEntity.class.getPackage().getName(), ExampleSecuredEntity.class.getSimpleName(), "name"));

    }
    @Inject
    private ApplicationRoles applicationRoles;

}
