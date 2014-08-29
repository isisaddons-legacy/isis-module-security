package org.isisaddons.module.security.fixture.scripts.userRoles;

import org.isisaddons.module.security.fixture.scripts.roles.AdminRoleFixture;
import org.isisaddons.module.security.fixture.scripts.users.SvenUserFixture;

public class SvenUser_Has_AdminRole extends AbstractUserRoleFixture {
    public SvenUser_Has_AdminRole() {
        super(SvenUserFixture.USER_NAME, AdminRoleFixture.ROLE_NAME);
    }
}
