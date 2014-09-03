/*
 *  Copyright 2014 Dan Haywood
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.isisaddons.module.security.dom.role;

import java.util.List;
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