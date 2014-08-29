package org.isisaddons.module.security.fixture.scripts.exampleEntities;

import org.isisaddons.module.security.fixture.dom.ExampleEntities;
import org.isisaddons.module.security.fixture.dom.ExampleEntity;
import org.apache.isis.applib.fixturescripts.FixtureScript;

public abstract class AbstractEntityFixture extends FixtureScript {

    protected ExampleEntity create(
            final String name,
            final ExecutionContext executionContext) {
        final ExampleEntity entity = exampleEntities.create(name);
        executionContext.add(this, name, entity);
        return entity;
    }

    @javax.inject.Inject
    private ExampleEntities exampleEntities;

}
