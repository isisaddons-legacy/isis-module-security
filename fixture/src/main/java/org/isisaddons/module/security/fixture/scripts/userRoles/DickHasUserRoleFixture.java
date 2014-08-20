package org.isisaddons.module.security.fixture.scripts.userRoles;

import org.isisaddons.module.security.fixture.scripts.roles.UserRoleFixture;
import org.isisaddons.module.security.fixture.scripts.users.DickUserFixture;

public class DickHasUserRoleFixture extends AbstractUserRoleFixture {
    public DickHasUserRoleFixture() {
        super(DickUserFixture.USER_NAME, UserRoleFixture.ROLE_NAME);
    }
}
