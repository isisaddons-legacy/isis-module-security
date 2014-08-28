package org.isisaddons.module.security.fixture.scripts.roles;

import org.isisaddons.module.security.dom.actor.ApplicationRole;
import org.isisaddons.module.security.dom.actor.ApplicationRoles;
import org.apache.isis.applib.fixturescripts.FixtureScript;

public abstract class AbstractRoleFixture extends FixtureScript {

    protected ApplicationRole create(
            final String name,
            final ExecutionContext executionContext) {
        final ApplicationRole entity = applicationRoles.newRole(name, null);
        executionContext.add(this, name, entity);
        return entity;
    }

    @javax.inject.Inject
    private ApplicationRoles applicationRoles;

}
