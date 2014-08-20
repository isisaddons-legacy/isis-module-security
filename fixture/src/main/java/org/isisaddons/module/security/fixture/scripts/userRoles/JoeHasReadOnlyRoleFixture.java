package org.isisaddons.module.security.fixture.scripts.userRoles;

import org.isisaddons.module.security.fixture.scripts.roles.ReadOnlyRoleFixture;
import org.isisaddons.module.security.fixture.scripts.users.JoeUserFixture;

public class JoeHasReadOnlyRoleFixture extends AbstractUserRoleFixture {
    public JoeHasReadOnlyRoleFixture() {
        super(JoeUserFixture.USER_NAME, ReadOnlyRoleFixture.ROLE_NAME);
    }
}
