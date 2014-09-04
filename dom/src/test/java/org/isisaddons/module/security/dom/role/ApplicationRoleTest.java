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
import com.danhaywood.java.testsupport.coverage.PojoTester;
import com.danhaywood.java.testsupport.coverage.PrivateConstructorTester;
import com.google.common.collect.Lists;
import org.isisaddons.module.security.dom.feature.ApplicationFeatureType;
import org.isisaddons.module.security.dom.permission.ApplicationPermission;
import org.isisaddons.module.security.dom.permission.ApplicationPermissionMode;
import org.isisaddons.module.security.dom.permission.ApplicationPermissionRule;
import org.isisaddons.module.security.dom.permission.ApplicationPermissions;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
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

    @Mock
    ApplicationPermissions mockApplicationPermissions;

    @Before
    public void setUp() throws Exception {
        applicationRole = new ApplicationRole();
        applicationRole.applicationPermissions = mockApplicationPermissions;
    }


    public static class Title extends ApplicationRoleTest {

        @Test
        public void whenMember() throws Exception {

            applicationRole.setName("Role1");

            assertThat(applicationRole.title(), is("Role1"));
        }

    }

    public static class BeanProperties extends ApplicationRoleTest {

        @Test
        public void exercise() throws Exception {
            PojoTester.relaxed()
                    .exercise(new ApplicationRole());
        }

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

    public static class Default0UpdateName extends ApplicationRoleTest {

        @Test
        public void happyCase() throws Exception {

            applicationRole.setName("Original name");

            assertThat(applicationRole.default0UpdateName(), is("Original name"));
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

    public static class Default0UpdateDescription extends ApplicationRoleTest {

        @Test
        public void happyCase() throws Exception {

            applicationRole.setDescription("Original descr");

            assertThat(applicationRole.default0UpdateDescription(), is("Original descr"));
        }
    }

    public static class GetPermissions extends ApplicationRoleTest {

        @Test
        public void happyCase() throws Exception {

            final List<ApplicationPermission> result = Lists.newArrayList();
            context.checking(new Expectations() {{
                oneOf(mockApplicationPermissions).findByRole(applicationRole);
                will(returnValue(result));
            }});

            assertThat(applicationRole.getPermissions(), is(result));
        }
    }

    public static class AddPackage extends ApplicationRoleTest {

//        applicationPermissions.newPermission(this, rule, mode, ApplicationFeatureType.PACKAGE, packageFqn);
//        return this;

        @Test
        public void happyCase() throws Exception {

            applicationRole.applicationPermissions = mockApplicationPermissions;

            context.checking(new Expectations() {{
                oneOf(mockApplicationPermissions).newPermission(applicationRole, ApplicationPermissionRule.ALLOW, ApplicationPermissionMode.CHANGING, ApplicationFeatureType.PACKAGE, "com.mycompany");
            }});

            applicationRole.addPackage(ApplicationPermissionRule.ALLOW, ApplicationPermissionMode.CHANGING, "com.mycompany");
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

    public static class PrivateConstructors {

        @Test
        public void forFunctions() throws Exception {
            new PrivateConstructorTester(ApplicationRole.Functions.class).exercise();
        }
    }

}