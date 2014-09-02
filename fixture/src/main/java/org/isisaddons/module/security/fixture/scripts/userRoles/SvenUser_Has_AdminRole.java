package org.isisaddons.module.security.fixture.scripts.userRoles;

import org.isisaddons.module.security.seed.scripts.IsisModuleSecurityAdminRoleAndPermissions;
import org.isisaddons.module.security.fixture.scripts.users.SvenUserFixture;

public class SvenUser_Has_AdminRole extends AbstractUserRoleFixture {
    public SvenUser_Has_AdminRole() {
        super(SvenUserFixture.USER_NAME, IsisModuleSecurityAdminRoleAndPermissions.ROLE_NAME);
    }
}
