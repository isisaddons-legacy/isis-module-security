package org.isisaddons.module.security.fixture.scripts.userRoles;

import org.isisaddons.module.security.app.SeedSecurityModuleService;
import org.isisaddons.module.security.fixture.scripts.users.SvenUserFixture;

public class SvenUser_Has_AdminRole extends AbstractUserRoleFixture {
    public SvenUser_Has_AdminRole() {
        super(SvenUserFixture.USER_NAME, SeedSecurityModuleService.AdminRoleAndPermissions.ROLE_NAME);
    }
}
