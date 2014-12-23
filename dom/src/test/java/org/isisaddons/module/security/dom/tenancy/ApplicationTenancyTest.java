package org.isisaddons.module.security.dom.tenancy;

import com.danhaywood.java.testsupport.coverage.PojoTester;
import org.isisaddons.module.security.dom.FixtureDatumFactories;
import org.junit.Rule;
import org.junit.Test;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

public class ApplicationTenancyTest {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    public static class BeanProperties extends ApplicationTenancyTest {

        @Test
        public void exercise() throws Exception {
            PojoTester.relaxed()
                    .withFixture(FixtureDatumFactories.tenancies())
                    .exercise(new ApplicationTenancy());
        }
    }

}