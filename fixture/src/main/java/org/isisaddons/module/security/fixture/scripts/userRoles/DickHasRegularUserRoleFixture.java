package org.isisaddons.module.security.fixture.scripts.userRoles;

import org.isisaddons.module.security.fixture.scripts.roles.RegularUserRoleFixture;
import org.isisaddons.module.security.fixture.scripts.users.DickUserFixture;

public class DickHasRegularUserRoleFixture extends AbstractUserRoleFixture {
    public DickHasRegularUserRoleFixture() {
        super(DickUserFixture.USER_NAME, RegularUserRoleFixture.ROLE_NAME);
    }
}
