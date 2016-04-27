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
package org.isisaddons.module.security.dom.permission;

import java.util.Arrays;
import java.util.List;

import com.danhaywood.java.testsupport.coverage.PojoTester;
import com.danhaywood.java.testsupport.coverage.PrivateConstructorTester;
import com.danhaywood.java.testsupport.coverage.ValueTypeContractTestAbstract;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.services.appfeat.ApplicationMemberType;
import org.apache.isis.core.metamodel.services.appfeat.ApplicationFeature;
import org.apache.isis.core.metamodel.services.appfeat.ApplicationFeatureId;
import org.apache.isis.core.metamodel.services.appfeat.ApplicationFeatureRepositoryDefault;
import org.apache.isis.core.metamodel.services.appfeat.ApplicationFeatureType;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.isisaddons.module.security.dom.role.ApplicationRole;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.isisaddons.module.security.dom.FixtureDatumFactories.roles;
import static org.junit.Assert.assertThat;

public class ApplicationPermissionTest {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock
    DomainObjectContainer mockContainer;

    @Mock
    ApplicationFeatureRepositoryDefault mockApplicationFeatureRepository;

    ApplicationPermission applicationPermission;

    @Before
    public void setUp() throws Exception {
        applicationPermission = new ApplicationPermission();
        applicationPermission.container = mockContainer;
    }

    public static class GetFeature extends ApplicationPermissionTest {
        @Test
        public void happyCase() throws Exception {
            // given
            final ApplicationFeatureId applicationFeatureId = ApplicationFeatureId.newPackage("org.company");
            applicationPermission.setFeatureType(applicationFeatureId.getType());
            applicationPermission.setFeatureFqn(applicationFeatureId.getFullyQualifiedName());
            applicationPermission.applicationFeatureRepository = mockApplicationFeatureRepository;

            final ApplicationFeature applicationFeature = new ApplicationFeature();

            // then
            context.checking(new Expectations() {{
                oneOf(mockApplicationFeatureRepository).findFeature(applicationFeatureId);
                will(returnValue(applicationFeature));
            }});

            // when
            final ApplicationFeature feature = applicationPermission.getFeature();

            // then
            assertThat(feature, is(equalTo(applicationFeature)));
        }
        @Test
        public void whenNull() throws Exception {
            // given
            applicationPermission.setFeatureType(null);
            applicationPermission.setFeatureFqn(null);

            // then
            context.checking(new Expectations() {{
                never(mockContainer);
            }});

            // when
            final ApplicationFeature feature = applicationPermission.getFeature();

            // then
            assertThat(feature, is(nullValue()));
        }
    }

    public static class PrivateConstructors extends ApplicationPermissionTest {

        @Test
        public void forFunctions() throws Exception {
            new PrivateConstructorTester(ApplicationPermission.Functions.class).exercise();
        }

    }

    public static class BeanProperties extends ApplicationPermissionTest {

        @Ignore("intermittent failures... suspect an issue with PojoTester, not thread-safe or something?")
        @Test
        public void exercise() throws Exception {
            PojoTester.relaxed()
                    .withFixture(roles())
                    .exercise(new ApplicationPermission());
        }

    }

    public static class ValueTypeContractTest extends ValueTypeContractTestAbstract<ApplicationPermission> {
        private ApplicationRole role1;
        private ApplicationRole role2;

        @Before
        public void setUp() throws Exception {
            role1 = new ApplicationRole();
            role1.setName("role1");
            role2 = new ApplicationRole();
            role2.setName("role2");
        }

        @Override
        protected List<ApplicationPermission> getObjectsWithSameValue() {
            return Arrays.asList(
                    perm(role1, ApplicationFeatureType.MEMBER, "com.mycompany.Bar#foo", ApplicationPermissionMode.CHANGING),
                    perm(role1, ApplicationFeatureType.MEMBER, "com.mycompany.Bar#foo", ApplicationPermissionMode.CHANGING)
                    );
        }

        @Override
        protected List<ApplicationPermission> getObjectsWithDifferentValue() {
            return Arrays.asList(
                    perm(role2, ApplicationFeatureType.MEMBER, "com.mycompany.Bar#foo", ApplicationPermissionMode.CHANGING),
                    perm(role1, ApplicationFeatureType.CLASS, "com.mycompany.Bar#foo", ApplicationPermissionMode.CHANGING),
                    perm(role1, ApplicationFeatureType.MEMBER, "com.mycompany.Bar#foo2", ApplicationPermissionMode.CHANGING),
                    perm(role1, ApplicationFeatureType.MEMBER, "com.mycompany.Bar#foo", ApplicationPermissionMode.VIEWING)
            );
        }

        private static ApplicationPermission perm(ApplicationRole role, ApplicationFeatureType featureType, String fqn, ApplicationPermissionMode mode) {
            final ApplicationPermission permission = new ApplicationPermission();
            permission.setRole(role);
            permission.setFeatureType(featureType);
            permission.setFeatureFqn(fqn);
            permission.setMode(mode);
            return permission;
        }


    }

    public static class Viewing extends ApplicationPermissionTest {

        public static class ActionImpl extends Viewing {

            @Test
            public void happyCase() throws Exception {
                applicationPermission = new ApplicationPermission();

                applicationPermission.viewing();
                assertThat(applicationPermission.getMode(), is(ApplicationPermissionMode.VIEWING));
            }
        }

        public static class Disable extends ApplicationPermissionTest {

            @Before
            public void setUp() throws Exception {
                applicationPermission = new ApplicationPermission();
            }

            @Test
            public void whenChanging() throws Exception {
                applicationPermission.setMode(ApplicationPermissionMode.CHANGING);

                assertThat(applicationPermission.disableViewing(), is(nullValue()));
            }
            @Test
            public void whenViewing() throws Exception {
                applicationPermission.setMode(ApplicationPermissionMode.VIEWING);

                assertThat(applicationPermission.disableViewing(), is(not(nullValue())));
            }
        }

    }

    public static class Changing extends ApplicationPermissionTest {

        public static class ActionImpl extends Changing {

            @Test
            public void happyCase() throws Exception {
                applicationPermission = new ApplicationPermission();

                applicationPermission.changing();
                assertThat(applicationPermission.getMode(), is(ApplicationPermissionMode.CHANGING));
            }
        }

        public static class Disable extends Changing {

            @Before
            public void setUp() throws Exception {
                applicationPermission = new ApplicationPermission();
            }

            @Test
            public void whenChanging() throws Exception {
                applicationPermission.setMode(ApplicationPermissionMode.CHANGING);

                assertThat(applicationPermission.disableChanging(), is(not(nullValue())));
            }
            @Test
            public void whenViewing() throws Exception {
                applicationPermission.setMode(ApplicationPermissionMode.VIEWING);

                assertThat(applicationPermission.disableChanging(), is(nullValue()));
            }
        }


    }

    public static class Allow extends ApplicationPermissionTest {

        public static class ActionImpl extends Allow {

            @Test
            public void happyCase() throws Exception {
                applicationPermission = new ApplicationPermission();

                applicationPermission.allow();
                assertThat(applicationPermission.getRule(), is(ApplicationPermissionRule.ALLOW));
            }
        }

        public static class Disable extends Allow  {

            @Before
            public void setUp() throws Exception {
                applicationPermission = new ApplicationPermission();
            }

            @Test
            public void whenAllow() throws Exception {
                applicationPermission.setRule(ApplicationPermissionRule.ALLOW);

                assertThat(applicationPermission.disableAllow(), is(not(nullValue())));
            }
            @Test
            public void whenViewing() throws Exception {
                applicationPermission.setRule(ApplicationPermissionRule.VETO);

                assertThat(applicationPermission.disableAllow(), is(nullValue()));
            }
        }


    }

    public static class Veto extends ApplicationPermissionTest {

        public static class ActionImpl extends Veto {

            @Test
            public void happyCase() throws Exception {
                applicationPermission = new ApplicationPermission();

                applicationPermission.veto();
                assertThat(applicationPermission.getRule(), is(ApplicationPermissionRule.VETO));
            }
        }

        public static class Disable extends Veto {

            @Before
            public void setUp() throws Exception {
                applicationPermission = new ApplicationPermission();
            }

            @Test
            public void whenAllow() throws Exception {
                applicationPermission.setRule(ApplicationPermissionRule.ALLOW);

                assertThat(applicationPermission.disableVeto(), is(nullValue()));
            }

            @Test
            public void whenVeto() throws Exception {
                applicationPermission.setRule(ApplicationPermissionRule.VETO);

                assertThat(applicationPermission.disableVeto(), is(not(nullValue())));
            }

        }

    }

    public static class GetType extends ApplicationPermissionTest {

        @Mock
        ApplicationFeature mockApplicationFeature;

        @Before
        public void setUp() throws Exception {
            applicationPermission = new ApplicationPermission();
            applicationPermission.applicationFeatureRepository = mockApplicationFeatureRepository;
        }

        @Test
        public void whenPackage() throws Exception {
            applicationPermission.setFeatureType(ApplicationFeatureType.PACKAGE);

            assertThat(applicationPermission.getType(), is("PACKAGE"));
        }

        @Test
        public void whenClass() throws Exception {
            applicationPermission.setFeatureType(ApplicationFeatureType.CLASS);

            assertThat(applicationPermission.getType(), is("CLASS"));
        }

        @Test
        public void whenProperty_validFeature() throws Exception {
            final ApplicationFeatureId featureId = ApplicationFeatureId.newMember("com.mycompany.Foo#bar");
            applicationPermission.setFeatureType(featureId.getType());
            applicationPermission.setFeatureFqn(featureId.getFullyQualifiedName());

            context.checking(new Expectations() {{
                allowing(mockApplicationFeatureRepository).findFeature(featureId);
                will(returnValue(mockApplicationFeature));

                allowing(mockApplicationFeature).getMemberType();
                will(returnValue(ApplicationMemberType.PROPERTY));
            }});

            assertThat(applicationPermission.getType(), is("PROPERTY"));
        }

        @Test
        public void whenProperty_noSuchFeature() throws Exception {
            final ApplicationFeatureId featureId = ApplicationFeatureId.newMember("com.mycompany.Foo#bar");
            applicationPermission.setFeatureType(featureId.getType());
            applicationPermission.setFeatureFqn(featureId.getFullyQualifiedName());

            context.checking(new Expectations() {{
                allowing(mockApplicationFeatureRepository).findFeature(featureId);
                will(returnValue(null));
            }});

            assertThat(applicationPermission.getType(), is(nullValue()));
        }


    }

    public static class FunctionsTest extends ApplicationPermissionTest {

        @Test
        public void AS_VALUE() throws Exception {
            // given
            applicationPermission.setFeatureType(ApplicationFeatureType.MEMBER);
            applicationPermission.setFeatureFqn("com.mycompany.Foo#bar");
            applicationPermission.setRule(ApplicationPermissionRule.ALLOW);
            applicationPermission.setMode(ApplicationPermissionMode.CHANGING);

            // when, then
            assertThat(
                    ApplicationPermission.Functions.AS_VALUE.apply(applicationPermission),
                    is(new ApplicationPermissionValue(
                            ApplicationFeatureId.newMember("com.mycompany.Foo#bar"),
                            ApplicationPermissionRule.ALLOW,
                            ApplicationPermissionMode.CHANGING)));
        }

        @Test
        public void GET_FQN() throws Exception {
            applicationPermission.setFeatureFqn("com.mycompany.Foo#bar");

            assertThat(ApplicationPermission.Functions.GET_FQN.apply(applicationPermission), is("com.mycompany.Foo#bar"));
        }
    }

    public static class Title extends ApplicationPermissionTest {

        private ApplicationRole applicationRole;

        @Before
        public void setUp() throws Exception {
            applicationPermission = new ApplicationPermission();
            applicationRole = new ApplicationRole();
            applicationRole.setName("Role1");
            applicationPermission.setRole(applicationRole);
        }

        @Test
        public void whenPackage() throws Exception {
            applicationPermission.setRole(applicationRole);
            applicationPermission.setFeatureType(ApplicationFeatureType.PACKAGE);
            applicationPermission.setFeatureFqn("com.mycompany");
            applicationPermission.setMode(ApplicationPermissionMode.CHANGING);
            applicationPermission.setRule(ApplicationPermissionRule.VETO);

            assertThat(applicationPermission.title(), is("Role1: VETO CHANGING of com.mycompany"));
        }

        @Test
        public void whenClass() throws Exception {
            applicationPermission.setFeatureType(ApplicationFeatureType.CLASS);
            applicationPermission.setFeatureFqn("com.mycompany.Bar");
            applicationPermission.setMode(ApplicationPermissionMode.VIEWING);
            applicationPermission.setRule(ApplicationPermissionRule.ALLOW);

            assertThat(applicationPermission.title(), is("Role1: ALLOW VIEWING of com.mycompany.Bar"));
        }

        @Test
        public void whenClass_withLongFullyQualifiedClassName() throws Exception {
            applicationPermission.setFeatureType(ApplicationFeatureType.CLASS);
            applicationPermission.setFeatureFqn("com.mycompany.globaldivision.europe.netherlands.changemanagement.alphacentauri.PortfolioManager");
            applicationPermission.setMode(ApplicationPermissionMode.CHANGING);
            applicationPermission.setRule(ApplicationPermissionRule.ALLOW);

            assertThat(applicationPermission.title(), is("Role1: ALLOW CHANGING of PortfolioManager"));
        }

        @Test
        public void whenMember() throws Exception {
            applicationPermission.setFeatureType(ApplicationFeatureType.MEMBER);
            applicationPermission.setFeatureFqn("com.mycompany.globaldivision.europe.netherlands.changemanagement.alphacentauri.PortfolioManager#lookupPortfolio");
            applicationPermission.setMode(ApplicationPermissionMode.VIEWING);
            applicationPermission.setRule(ApplicationPermissionRule.VETO);

            assertThat(applicationPermission.title(), is("Role1: VETO VIEWING of PortfolioManager#lookupPortfolio"));
        }

    }

}
