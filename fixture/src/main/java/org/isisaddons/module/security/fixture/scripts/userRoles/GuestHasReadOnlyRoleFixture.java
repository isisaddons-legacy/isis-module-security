package org.isisaddons.module.security.fixture.scripts.userRoles;

import org.isisaddons.module.security.fixture.scripts.roles.ReadOnlyRoleFixture;
import org.isisaddons.module.security.fixture.scripts.users.GuestUserFixture;

public class GuestHasReadOnlyRoleFixture extends AbstractUserRoleFixture {
    public GuestHasReadOnlyRoleFixture() {
        super(GuestUserFixture.USER_NAME, ReadOnlyRoleFixture.ROLE_NAME);
    }
}
