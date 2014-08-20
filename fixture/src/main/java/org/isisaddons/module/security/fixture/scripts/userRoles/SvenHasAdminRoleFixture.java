package org.isisaddons.module.security.fixture.scripts.userRoles;

import org.isisaddons.module.security.fixture.scripts.roles.AdminRoleFixture;
import org.isisaddons.module.security.fixture.scripts.users.SvenUserFixture;

public class SvenHasAdminRoleFixture extends AbstractUserRoleFixture {
    public SvenHasAdminRoleFixture() {
        super(SvenUserFixture.USER_NAME, AdminRoleFixture.ROLE_NAME);
    }
}
