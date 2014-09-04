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

import com.danhaywood.java.testsupport.coverage.PojoTester;
import com.danhaywood.java.testsupport.coverage.PrivateConstructorTester;
import org.isisaddons.module.security.dom.feature.ApplicationFeature;
import org.isisaddons.module.security.dom.feature.ApplicationFeatureId;
import org.isisaddons.module.security.dom.feature.ApplicationFeatures;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.*;
import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import static org.hamcrest.CoreMatchers.*;
import static org.isisaddons.module.security.dom.FixtureDatumFactories.roles;

public class ApplicationPermissionTest {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock
    DomainObjectContainer mockContainer;

    @Mock
    ApplicationFeatures mockApplicationFeatures;

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
            applicationPermission.applicationFeatures = mockApplicationFeatures;

            final ApplicationFeature applicationFeature = new ApplicationFeature();

            // then
            context.checking(new Expectations() {{
                oneOf(mockApplicationFeatures).findFeature(applicationFeatureId);
                will(returnValue(applicationFeature));
            }});

            // when
            final ApplicationFeature feature = applicationPermission.getFeature();

            // then
            Assert.assertThat(feature, is(equalTo(applicationFeature)));
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
            Assert.assertThat(feature, is(nullValue()));
        }
    }

    public static class PrivateConstructors {

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


}
