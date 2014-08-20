package org.isisaddons.module.security.fixture.scripts.userRoles;

import org.isisaddons.module.security.fixture.scripts.roles.WriteOnlyRoleFixture;
import org.isisaddons.module.security.fixture.scripts.users.DickUserFixture;

public class DickHasWriteOnlyRoleFixture extends AbstractUserRoleFixture {
    public DickHasWriteOnlyRoleFixture() {
        super(DickUserFixture.USER_NAME, WriteOnlyRoleFixture.ROLE_NAME);
    }
}
