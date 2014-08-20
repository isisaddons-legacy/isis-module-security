package org.isisaddons.module.security.fixture.scripts.tenancy;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancies;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.apache.isis.applib.fixturescripts.FixtureScript;

public abstract class AbstractTenancyFixture extends FixtureScript {

    protected ApplicationTenancy create(
            final String name,
            final ExecutionContext executionContext) {
        final ApplicationTenancy tenancy = applicationTenancies.newTenancy(name);
        executionContext.add(this, name, tenancy);
        return tenancy;
    }

    @javax.inject.Inject
    private ApplicationTenancies applicationTenancies;

}
