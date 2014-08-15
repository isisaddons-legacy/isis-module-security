package org.isisaddons.module.security.integtests;

import java.util.List;
import javax.inject.Inject;
import org.isisaddons.module.security.fixture.dom.ExampleSecuredEntities;
import org.isisaddons.module.security.fixture.dom.ExampleSecuredEntity;
import org.isisaddons.module.security.fixture.scripts.ExampleSecuredEntitiesTearDownFixture;
import org.isisaddons.module.security.fixture.scripts.entities.Bar;
import org.isisaddons.module.security.fixture.scripts.entities.Baz;
import org.isisaddons.module.security.fixture.scripts.entities.Bip;
import org.isisaddons.module.security.fixture.scripts.entities.Bop;
import org.junit.Before;
import org.junit.Test;
import org.apache.isis.applib.services.jdosupport.IsisJdoSupport;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ExampleSecuredEntityTest extends ExampleSecuredEntitiesAppIntegTest {

    ExampleSecuredEntity entity;

    @Before
    public void setUpData() throws Exception {
        scenarioExecution().install(
                new ExampleSecuredEntitiesTearDownFixture(),
                new Bip(),
                new Bar(),
                new Baz(),
                new Bop()
                );
    }

    @Inject
    ExampleSecuredEntities exampleSecuredEntities;

    @Inject
    IsisJdoSupport isisJdoSupport;

    @Before
    public void setUp() throws Exception {
        final List<ExampleSecuredEntity> all = wrap(exampleSecuredEntities).listAll();
        assertThat(all.size(), is(4));

        entity = all.get(0);
    }

    public static class TODO extends ExampleSecuredEntityTest {

        @Test
        public void TODO() throws Exception {

        }

    }


}