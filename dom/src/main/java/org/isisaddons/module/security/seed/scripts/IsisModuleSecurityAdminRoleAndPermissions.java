package org.isisaddons.module.security.seed.scripts;

import org.isisaddons.module.security.dom.permission.ApplicationPermissionMode;
import org.isisaddons.module.security.dom.permission.ApplicationPermissionRule;

public class IsisModuleSecurityAdminRoleAndPermissions extends AbstractRoleAndPermissionsFixtureScript {

    public static final String ROLE_NAME = "isis-module-security-admin";

    public IsisModuleSecurityAdminRoleAndPermissions() {
        super(ROLE_NAME, "Administer security");
    }


    @Override
    protected void execute(ExecutionContext executionContext) {
        newPackagePermissions(
                ApplicationPermissionRule.ALLOW,
                ApplicationPermissionMode.CHANGING,
                "org.isisaddons.module.security.app",
                "org.isisaddons.module.security.dom");
    }
}
