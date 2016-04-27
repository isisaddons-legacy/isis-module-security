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

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.services.appfeat.ApplicationFeatureRepository;
import org.apache.isis.applib.services.appfeat.ApplicationMemberType;
import org.apache.isis.core.metamodel.services.appfeat.ApplicationFeature;
import org.apache.isis.core.metamodel.services.appfeat.ApplicationFeatureId;
import org.apache.isis.core.metamodel.services.appfeat.ApplicationFeatureType;
import org.apache.isis.core.unittestsupport.comparable.ComparableContractTest_compareTo;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.isisaddons.module.security.dom.permission.ApplicationPermission;
import org.isisaddons.module.security.dom.permission.ApplicationPermissionMode;
import org.isisaddons.module.security.dom.permission.ApplicationPermissionRepository;
import org.isisaddons.module.security.dom.permission.ApplicationPermissionRule;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;

public class ApplicationRoleTest {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    ApplicationRole applicationRole;

    @Mock
    ApplicationPermissionRepository mockApplicationPermissionRepository;
    @Mock
    ApplicationFeatureRepository mockApplicationFeatureRepository;

    final ApplicationFeature pkg1 = new ApplicationFeature();
    final ApplicationFeature pkg2 = new ApplicationFeature();

    final ApplicationFeature cls1 = new ApplicationFeature();
    final ApplicationFeature cls2 = new ApplicationFeature();


    @Before
    public void setUp() throws Exception {
        applicationRole = new ApplicationRole();
        applicationRole.applicationPermissionRepository = mockApplicationPermissionRepository;
        applicationRole.applicationFeatureRepository = mockApplicationFeatureRepository;

        pkg1.setFeatureId(ApplicationFeatureId.newFeature(ApplicationFeatureType.PACKAGE, "com.mycompany"));
        pkg2.setFeatureId(ApplicationFeatureId.newFeature(ApplicationFeatureType.PACKAGE, "com.mycompany.foo"));

        cls1.setFeatureId(ApplicationFeatureId.newFeature(ApplicationFeatureType.CLASS, "com.mycompany.Bar"));
        cls2.setFeatureId(ApplicationFeatureId.newFeature(ApplicationFeatureType.CLASS, "com.mycompany.Baz"));
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

        public static class Action extends UpdateName {

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

        public static class Default0 extends UpdateName {

            @Test
            public void happyCase() throws Exception {

                applicationRole.setName("Original name");

                assertThat(applicationRole.default0UpdateName(), is("Original name"));
            }
        }
    }

    public static class UpdateDescription extends ApplicationRoleTest {

        public static class Action extends UpdateDescription {

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

        public static class Default0 extends ApplicationRoleTest {

            @Test
            public void happyCase() throws Exception {

                applicationRole.setDescription("Original descr");

                assertThat(applicationRole.default0UpdateDescription(), is("Original descr"));
            }
        }
    }

    public static class GetPermissions extends ApplicationRoleTest {

        @Test
        public void happyCase() throws Exception {

            final List<ApplicationPermission> result = Lists.newArrayList();
            context.checking(new Expectations() {{
                oneOf(mockApplicationPermissionRepository).findByRole(applicationRole);
                will(returnValue(result));
            }});

            assertThat(applicationRole.getPermissions(), is(result));
        }
    }

    public static class AddPackage extends ApplicationRoleTest {

        public static class ActionImpl extends AddPackage {

            @Test
            public void happyCase() throws Exception {

                context.checking(new Expectations() {{
                    oneOf(mockApplicationPermissionRepository).newPermission(applicationRole, ApplicationPermissionRule.ALLOW, ApplicationPermissionMode.CHANGING, ApplicationFeatureType.PACKAGE, "com.mycompany");
                }});

                final ApplicationRole role = applicationRole.addPackage(ApplicationPermissionRule.ALLOW, ApplicationPermissionMode.CHANGING, "com.mycompany");
                assertThat(role, sameInstance(applicationRole));
            }
        }

        public static class Default0 extends AddPackage {

            @Test
            public void happyCase() throws Exception {

                assertThat(applicationRole.default0AddPackage(), sameInstance(ApplicationPermissionRule.ALLOW));
            }
        }
        public static class Default1 extends AddPackage {

            @Test
            public void happyCase() throws Exception {

                assertThat(applicationRole.default1AddPackage(), sameInstance(ApplicationPermissionMode.CHANGING));
            }
        }

        public static class Choices2 extends AddPackage {

            @Test
            public void happyCase() throws Exception {
                context.checking(new Expectations() {{
                    allowing(mockApplicationFeatureRepository).packageNames();
                    will(returnValue(Lists.newArrayList("com.mycompany", "com.mycompany.foo")));
                }});
                final List<String> packageNames = applicationRole.choices2AddPackage();
                assertThat(packageNames, containsInAnyOrder("com.mycompany", "com.mycompany.foo"));
            }
        }
    }

    public static class AddClass extends ApplicationRoleTest {

        public static class ActionImpl extends AddClass {

            @Test
            public void happyCase() throws Exception {

                context.checking(new Expectations() {{
                    oneOf(mockApplicationPermissionRepository).newPermission(applicationRole, ApplicationPermissionRule.ALLOW, ApplicationPermissionMode.CHANGING, ApplicationFeatureType.CLASS, "com.mycompany.Bar");
                }});

                final ApplicationRole role = applicationRole.addClass(ApplicationPermissionRule.ALLOW, ApplicationPermissionMode.CHANGING, "com.mycompany", "Bar");
                assertThat(role, sameInstance(applicationRole));
            }
        }

        public static class Default0 extends AddClass {

            @Test
            public void happyCase() throws Exception {

                assertThat(applicationRole.default0AddClass(), sameInstance(ApplicationPermissionRule.ALLOW));
            }
        }

        public static class Default1 extends AddClass {

            @Test
            public void happyCase() throws Exception {

                assertThat(applicationRole.default1AddClass(), sameInstance(ApplicationPermissionMode.CHANGING));
            }
        }

        public static class Choices2 extends AddClass {

            @Test
            public void happyCase() throws Exception {

                context.checking(new Expectations() {{
                    allowing(mockApplicationFeatureRepository).packageNamesContainingClasses(null);
                    will(returnValue(Lists.newArrayList("com.mycompany", "com.mycompany.foo")));
                }});
                final List<String> packageNames = applicationRole.choices2AddClass();
                assertThat(packageNames, containsInAnyOrder("com.mycompany", "com.mycompany.foo"));
            }
        }

        public static class Choices3 extends AddClass {

            @Test
            public void happyCase() throws Exception {

                context.checking(new Expectations() {{
                    allowing(mockApplicationFeatureRepository).classNamesContainedIn("com.mycompany", null);
                    will(returnValue(Lists.newArrayList("Bar", "Baz")));
                }});
                final List<String> classNames =
                        applicationRole.choices3AddClass(
                                ApplicationPermissionRule.ALLOW,
                                ApplicationPermissionMode.CHANGING,
                                "com.mycompany");
                assertThat(classNames, containsInAnyOrder("Bar", "Baz"));
            }
        }
    }

    public static class AddAction_or_AddProperty_or_AddCollection extends ApplicationRoleTest {

        public static class ActionImpl extends AddAction_or_AddProperty_or_AddCollection {

            @Before
            public void setUp() throws Exception {
                super.setUp();
                applicationRole.applicationPermissionRepository = mockApplicationPermissionRepository;

                context.checking(new Expectations() {{
                    oneOf(mockApplicationPermissionRepository).newPermission(applicationRole, ApplicationPermissionRule.ALLOW, ApplicationPermissionMode.CHANGING, "com.mycompany", "Bar", "foo");
                }});
            }

            @Test
            public void forAction() throws Exception {

                final ApplicationRole role = applicationRole.addAction(ApplicationPermissionRule.ALLOW, ApplicationPermissionMode.CHANGING, "com.mycompany", "Bar", "foo");
                assertThat(role, sameInstance(applicationRole));
            }

            @Test
            public void forProperty() throws Exception {

                final ApplicationRole role = applicationRole.addProperty(ApplicationPermissionRule.ALLOW, ApplicationPermissionMode.CHANGING, "com.mycompany", "Bar", "foo");
                assertThat(role, sameInstance(applicationRole));
            }

            @Test
            public void forCollection() throws Exception {

                final ApplicationRole role = applicationRole.addCollection(ApplicationPermissionRule.ALLOW, ApplicationPermissionMode.CHANGING, "com.mycompany", "Bar", "foo");
                assertThat(role, sameInstance(applicationRole));
            }
        }

        public static class Default0 extends AddAction_or_AddProperty_or_AddCollection {

            @Test
            public void happyCase() throws Exception {

                assertThat(applicationRole.default0AddAction(), sameInstance(ApplicationPermissionRule.ALLOW));
                assertThat(applicationRole.default0AddProperty(), sameInstance(ApplicationPermissionRule.ALLOW));
                assertThat(applicationRole.default0AddCollection(), sameInstance(ApplicationPermissionRule.ALLOW));
            }
        }
        public static class Default1 extends AddAction_or_AddProperty_or_AddCollection {

            @Test
            public void happyCase() throws Exception {

                assertThat(applicationRole.default1AddAction(), sameInstance(ApplicationPermissionMode.CHANGING));
                assertThat(applicationRole.default1AddProperty(), sameInstance(ApplicationPermissionMode.CHANGING));
                assertThat(applicationRole.default1AddCollection(), sameInstance(ApplicationPermissionMode.CHANGING));
            }
        }

        public static class Choices2 extends AddAction_or_AddProperty_or_AddCollection {

            @Test
            public void happyCase() throws Exception {

                context.checking(new Expectations() {{
                    allowing(mockApplicationFeatureRepository).packageNamesContainingClasses(ApplicationMemberType.ACTION);
                    will(returnValue(Lists.newArrayList("com.mycompany", "com.mycompany.actions")));
                    allowing(mockApplicationFeatureRepository).packageNamesContainingClasses(ApplicationMemberType.PROPERTY);
                    will(returnValue(Lists.newArrayList("com.mycompany", "com.mycompany.properties")));
                    allowing(mockApplicationFeatureRepository).packageNamesContainingClasses(ApplicationMemberType.COLLECTION);
                    will(returnValue(Lists.newArrayList("com.mycompany", "com.mycompany.collections")));
                }});

                List<String> packageNames;
                packageNames = applicationRole.choices2AddAction();
                assertThat(packageNames, containsInAnyOrder("com.mycompany", "com.mycompany.actions"));
                packageNames = applicationRole.choices2AddProperty();
                assertThat(packageNames, containsInAnyOrder("com.mycompany", "com.mycompany.properties"));
                packageNames = applicationRole.choices2AddCollection();
                assertThat(packageNames, containsInAnyOrder("com.mycompany", "com.mycompany.collections"));
            }
        }


        public static class Choices3 extends AddAction_or_AddProperty_or_AddCollection {

            @Test
            public void forAll() throws Exception {

                context.checking(new Expectations() {{
                    allowing(mockApplicationFeatureRepository).classNamesContainedIn("com.mycompany", ApplicationMemberType.ACTION);
                    will(returnValue(Lists.newArrayList("Bar", "Baz")));
                    allowing(mockApplicationFeatureRepository).classNamesContainedIn("com.mycompany", ApplicationMemberType.PROPERTY);
                    will(returnValue(Lists.newArrayList("Fiz", "Foz")));
                    allowing(mockApplicationFeatureRepository).classNamesContainedIn("com.mycompany", ApplicationMemberType.COLLECTION);
                    will(returnValue(Lists.newArrayList("Qiz", "Qoz")));
                }});

                List<String> classNames;

                classNames = applicationRole.choices3AddAction(
                        ApplicationPermissionRule.ALLOW,
                        ApplicationPermissionMode.CHANGING,
                        "com.mycompany");
                assertThat(classNames, containsInAnyOrder("Bar", "Baz"));

                classNames =
                        applicationRole.choices3AddProperty(
                                ApplicationPermissionRule.ALLOW,
                                ApplicationPermissionMode.CHANGING,
                                "com.mycompany");
                assertThat(classNames, containsInAnyOrder("Fiz", "Foz"));

                classNames =
                        applicationRole.choices3AddCollection(
                                ApplicationPermissionRule.ALLOW,
                                ApplicationPermissionMode.CHANGING,
                                "com.mycompany");
                assertThat(classNames, containsInAnyOrder("Qiz", "Qoz"));
            }
        }

        public static class Choices4 extends AddAction_or_AddProperty_or_AddCollection {

            @Test
            public void forAll() throws Exception {

                context.checking(new Expectations() {{
                    allowing(mockApplicationFeatureRepository).memberNamesOf("com.mycompany", "Bar", ApplicationMemberType.ACTION);
                    will(returnValue(Lists.newArrayList("foo", "far")));
                    allowing(mockApplicationFeatureRepository).memberNamesOf("com.mycompany", "Bar", ApplicationMemberType.PROPERTY);
                    will(returnValue(Lists.newArrayList("boo", "bar")));
                    allowing(mockApplicationFeatureRepository).memberNamesOf("com.mycompany", "Bar", ApplicationMemberType.COLLECTION);
                    will(returnValue(Lists.newArrayList("coo", "car")));
                }});

                List<String> classNames;

                classNames = applicationRole.choices4AddAction(
                        ApplicationPermissionRule.ALLOW,
                        ApplicationPermissionMode.CHANGING,
                        "com.mycompany", "Bar");
                assertThat(classNames, containsInAnyOrder("foo", "far"));

                classNames = applicationRole.choices4AddProperty(
                        ApplicationPermissionRule.ALLOW,
                        ApplicationPermissionMode.CHANGING,
                        "com.mycompany", "Bar");
                assertThat(classNames, containsInAnyOrder("boo", "bar"));

                classNames = applicationRole.choices4AddCollection(
                        ApplicationPermissionRule.ALLOW,
                        ApplicationPermissionMode.CHANGING,
                        "com.mycompany", "Bar");
                assertThat(classNames, containsInAnyOrder("coo", "car"));
            }
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