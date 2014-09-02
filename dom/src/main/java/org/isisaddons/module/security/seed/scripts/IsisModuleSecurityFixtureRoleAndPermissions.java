package org.isisaddons.module.security.seed.scripts;

import org.isisaddons.module.security.dom.permission.ApplicationPermissionMode;
import org.isisaddons.module.security.dom.permission.ApplicationPermissionRule;

/**
 * Role to run in the prototype fixture scripts for the example webapp for the security module.
 */
public class IsisModuleSecurityFixtureRoleAndPermissions extends AbstractRoleAndPermissionsFixtureScript {

    public static final String ROLE_NAME = "isis-module-security-fixtures";

    public IsisModuleSecurityFixtureRoleAndPermissions() {
        super(ROLE_NAME, "Security module fixtures");
    }

    @Override
    protected void execute(ExecutionContext executionContext) {
        newPackagePermissions(
                ApplicationPermissionRule.ALLOW,
                ApplicationPermissionMode.CHANGING,
                "org.isisaddons.module.security.fixture");
    }
}
