package org.isisaddons.module.security.fixture.scripts.entities;

import org.isisaddons.module.security.fixture.dom.ExampleSecuredEntities;
import org.isisaddons.module.security.fixture.dom.ExampleSecuredEntity;
import org.apache.isis.applib.fixturescripts.FixtureScript;

public abstract class AbstractEntityFixture extends FixtureScript {

    protected ExampleSecuredEntity create(
            final String name,
            final ExecutionContext executionContext) {
        final ExampleSecuredEntity entity = exampleSecuredEntities.create(name);
        executionContext.add(this, name, entity);
        return entity;
    }

    @javax.inject.Inject
    private ExampleSecuredEntities exampleSecuredEntities;

}
