package org.isisaddons.module.security.fixture.scripts.roles;

import org.isisaddons.module.security.dom.role.ApplicationRole;
import org.isisaddons.module.security.dom.role.ApplicationRoles;
import org.apache.isis.applib.fixturescripts.FixtureScript;

public abstract class AbstractRoleFixture extends FixtureScript {

    protected ApplicationRole create(
            final String name,
            String description,
            final ExecutionContext executionContext) {
        final ApplicationRole entity = applicationRoles.newRole(name, description);
        executionContext.add(this, name, entity);
        return entity;
    }

    @javax.inject.Inject
    private ApplicationRoles applicationRoles;

}
