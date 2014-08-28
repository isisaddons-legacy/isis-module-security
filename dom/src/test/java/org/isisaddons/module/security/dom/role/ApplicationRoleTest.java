package org.isisaddons.module.security.dom.role;

import java.util.List;
import org.isisaddons.module.security.dom.actor.ApplicationRole;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.apache.isis.core.unittestsupport.comparable.ComparableContractTest_compareTo;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class ApplicationRoleTest {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    ApplicationRole applicationRole;

    @Before
    public void setUp() throws Exception {
        applicationRole = new ApplicationRole();
    }

    public static class UpdateName extends ApplicationRoleTest {

        @Test
        public void happyCase() throws Exception {

            // given
            applicationRole.setName("original name");

            // when
            applicationRole.updateName("New name");

            // then
            assertThat(applicationRole.getName(), is("New name"));
        }
    }

    public static class UpdateDescription extends ApplicationRoleTest {

        @Test
        public void happyCase() throws Exception {

            // given
            applicationRole.setDescription("original description");

            // when
            applicationRole.updateDescription("New description");

            // then
            assertThat(applicationRole.getDescription(), is("New description"));
        }

        @Test
        public void setToNull() throws Exception {

            // given
            applicationRole.setDescription("original description");

            // when
            applicationRole.updateDescription(null);

            // then
            assertThat(applicationRole.getDescription(), is(nullValue()));
        }
    }

    public static class CompareTo extends ComparableContractTest_compareTo<ApplicationRole> {

        @SuppressWarnings("unchecked")
        @Override
        protected List<List<ApplicationRole>> orderedTuples() {
            return listOf(
                    listOf(
                            newApplicationRole(null),
                            newApplicationRole("X"),
                            newApplicationRole("X"),
                            newApplicationRole("Y")
                    )
            );
        }

        private ApplicationRole newApplicationRole(
                String name) {
            final ApplicationRole applicationRole = new ApplicationRole();
            applicationRole.setName(name);
            return applicationRole;
        }

    }

}