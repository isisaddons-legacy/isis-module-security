package org.isisaddons.module.security.fixture.scripts.userRoles;

import org.isisaddons.module.security.fixture.scripts.roles.GuestRoleFixture;
import org.isisaddons.module.security.fixture.scripts.users.JoeUserFixture;

public class JoeUser_Has_ReadOnlyRole extends AbstractUserRoleFixture {
    public JoeUser_Has_ReadOnlyRole() {
        super(JoeUserFixture.USER_NAME, GuestRoleFixture.ROLE_NAME);
    }
}
