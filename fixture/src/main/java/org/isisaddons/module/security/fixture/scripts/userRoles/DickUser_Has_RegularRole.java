package org.isisaddons.module.security.fixture.scripts.userRoles;

import org.isisaddons.module.security.fixture.scripts.roles.RegularRoleFixture;
import org.isisaddons.module.security.fixture.scripts.users.DickUserFixture;

public class DickUser_Has_RegularRole extends AbstractUserRoleFixture {
    public DickUser_Has_RegularRole() {
        super(DickUserFixture.USER_NAME, RegularRoleFixture.ROLE_NAME);
    }
}
