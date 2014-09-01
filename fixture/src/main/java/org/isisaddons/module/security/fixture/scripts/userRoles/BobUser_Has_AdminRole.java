package org.isisaddons.module.security.fixture.scripts.userRoles;

import org.isisaddons.module.security.app.SeedSecurityModuleService;
import org.isisaddons.module.security.fixture.scripts.users.BobUserFixture;

public class BobUser_Has_AdminRole extends AbstractUserRoleFixture {
    public BobUser_Has_AdminRole() {
        super(BobUserFixture.USER_NAME, SeedSecurityModuleService.AdminRoleAndPermissions.ROLE_NAME);
    }
}
