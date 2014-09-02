package org.isisaddons.module.security.seed.scripts;

import java.util.Arrays;

public class IsisModuleSecurityAdminUser extends AbstractUserAndRolesFixtureScript {

    public static final String USER_NAME = "admin";
    public static final String PASSWORD = "pass";

    public IsisModuleSecurityAdminUser() {
        super(USER_NAME, PASSWORD, Arrays.asList(IsisModuleSecurityAdminRoleAndPermissions.ROLE_NAME, IsisModuleSecurityFixtureRoleAndPermissions.ROLE_NAME));
    }
}
