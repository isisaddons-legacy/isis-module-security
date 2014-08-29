package org.isisaddons.module.security.fixture.scripts.userRoles;

import org.isisaddons.module.security.fixture.scripts.roles.NoFixtureScriptsRoleFixture;
import org.isisaddons.module.security.fixture.scripts.users.BobUserFixture;

public class BobUser_Has_NoFixtureScriptsRole extends AbstractUserRoleFixture {
    public BobUser_Has_NoFixtureScriptsRole() {
        super(BobUserFixture.USER_NAME, NoFixtureScriptsRoleFixture.ROLE_NAME);
    }
}
