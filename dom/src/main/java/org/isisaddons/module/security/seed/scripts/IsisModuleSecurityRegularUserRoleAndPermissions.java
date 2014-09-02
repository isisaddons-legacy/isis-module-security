package org.isisaddons.module.security.seed.scripts;

import org.isisaddons.module.security.app.MeService;
import org.isisaddons.module.security.dom.permission.ApplicationPermissionMode;
import org.isisaddons.module.security.dom.permission.ApplicationPermissionRule;
import org.isisaddons.module.security.dom.role.ApplicationRole;
import org.isisaddons.module.security.dom.user.ApplicationUser;

/**
 * Role for regular users of the security module, providing the ability to lookup their user account using the
 * {@link org.isisaddons.module.security.app.MeService}, and for viewing and maintaining their user details.
 */
public class IsisModuleSecurityRegularUserRoleAndPermissions extends AbstractRoleAndPermissionsFixtureScript {

    public static final String ROLE_NAME = "isis-module-security-regular-user";

    public IsisModuleSecurityRegularUserRoleAndPermissions() {
        super(ROLE_NAME, "Regular user of the security module");
    }

    @Override
    protected void execute(ExecutionContext executionContext) {

        newMemberPermissions(
                ApplicationPermissionRule.ALLOW,
                ApplicationPermissionMode.CHANGING,
                MeService.class,
                "me");

        newClassPermissions(
                ApplicationPermissionRule.ALLOW,
                ApplicationPermissionMode.VIEWING,
                ApplicationUser.class);
        newMemberPermissions(
                ApplicationPermissionRule.ALLOW,
                ApplicationPermissionMode.CHANGING,
                ApplicationUser.class,
                "updateName",
                "updatePassword",
                "updateEmailAddress",
                "updatePhoneNumber",
                "updateFaxNumber");
        newMemberPermissions(
                ApplicationPermissionRule.VETO,
                ApplicationPermissionMode.VIEWING,
                ApplicationUser.class,
                "resetPassword",
                "updateTenancy",
                "enable",
                "disable",
                "addRole",
                "removeRole");
        newMemberPermissions(
                ApplicationPermissionRule.ALLOW,
                ApplicationPermissionMode.VIEWING,
                ApplicationRole.class,
                "name",
                "description");
    }

}
