package org.isisaddons.module.security.dom.permission;

import org.isisaddons.module.security.app.feature.ApplicationFeatureViewModel;
import org.isisaddons.module.security.dom.feature.ApplicationFeature;
import org.isisaddons.module.security.dom.feature.ApplicationFeatureId;
import org.isisaddons.module.security.dom.feature.ApplicationFeatures;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import static org.hamcrest.CoreMatchers.*;

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
}
