package org.isisaddons.module.security.fixture.scripts.userRoles;

import org.isisaddons.module.security.fixture.scripts.roles.GuestRoleFixture;
import org.isisaddons.module.security.fixture.scripts.users.GuestUserFixture;

public class GuestUser_Has_GuestRole extends AbstractUserRoleFixture {
    public GuestUser_Has_GuestRole() {
        super(GuestUserFixture.USER_NAME, GuestRoleFixture.ROLE_NAME);
    }
}
