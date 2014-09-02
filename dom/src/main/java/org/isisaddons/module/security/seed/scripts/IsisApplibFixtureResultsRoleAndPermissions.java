package org.isisaddons.module.security.seed.scripts;

import org.isisaddons.module.security.dom.permission.ApplicationPermissionMode;
import org.isisaddons.module.security.dom.permission.ApplicationPermissionRule;
import org.apache.isis.applib.fixturescripts.FixtureResult;

public class IsisApplibFixtureResultsRoleAndPermissions extends AbstractRoleAndPermissionsFixtureScript {

    public static final String ROLE_NAME = "isis-applib-fixtureresults";

    public IsisApplibFixtureResultsRoleAndPermissions() {
        super(ROLE_NAME, "Access results of running Fixture Scripts");
    }

    @Override
    protected void execute(ExecutionContext executionContext) {
        newClassPermissions(
                ApplicationPermissionRule.ALLOW,
                ApplicationPermissionMode.CHANGING,
                FixtureResult.class);
    }
}
