package org.isisaddons.module.security.fixture.scripts.userRoles;

import org.isisaddons.module.security.dom.actor.ApplicationRole;
import org.isisaddons.module.security.dom.actor.ApplicationRoles;
import org.isisaddons.module.security.dom.actor.ApplicationUser;
import org.isisaddons.module.security.dom.actor.ApplicationUsers;
import org.apache.isis.applib.fixturescripts.FixtureScript;

public abstract class AbstractUserRoleFixture extends FixtureScript {

    private final String userName;
    private final String roleName;

    public AbstractUserRoleFixture(
            final String userName,
            final String roleName) {
        this.userName = userName;
        this.roleName = roleName;
    }

    @Override
    protected void execute(ExecutionContext executionContext) {
        addUserToRole(userName, roleName, executionContext);
    }

    protected ApplicationUser addUserToRole(
            final String userName,
            final String roleName,
            final ExecutionContext executionContext) {
        final ApplicationUser user = applicationUsers.findByName(userName);
        final ApplicationRole applicationRole = applicationRoles.findByName(roleName);
        if(applicationRole != null) {
            user.addRole(applicationRole);
        }
        executionContext.add(this, roleName, applicationRole);
        return user;
    }

    @javax.inject.Inject
    private ApplicationUsers applicationUsers;
    @javax.inject.Inject
    private ApplicationRoles applicationRoles;

}
