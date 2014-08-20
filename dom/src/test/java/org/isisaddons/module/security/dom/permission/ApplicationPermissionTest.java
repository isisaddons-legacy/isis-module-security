package org.isisaddons.module.security.dom.permission;

import org.isisaddons.module.security.dom.feature.ApplicationFeatureViewModel;
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
            final String featureStr = "applicationFeatureMementoStr";
            applicationPermission.setFeatureStr(featureStr);

            // then
            final ApplicationFeatureViewModel applicationFeatureViewModel = new ApplicationFeatureViewModel();
            context.checking(new Expectations() {{
                oneOf(mockContainer).newViewModelInstance(ApplicationFeatureViewModel.class, featureStr);
                will(returnValue(applicationFeatureViewModel));
            }});

            // when
            final ApplicationFeatureViewModel feature = applicationPermission.getFeature();

            // then
            Assert.assertThat(feature, is(equalTo(applicationFeatureViewModel)));
        }
        @Test
        public void whenNull() throws Exception {
            // given
            applicationPermission.setFeatureStr(null);

            // then
            context.checking(new Expectations() {{
                never(mockContainer);
            }});

            // when
            final ApplicationFeatureViewModel feature = applicationPermission.getFeature();

            // then
            Assert.assertThat(feature, is(nullValue()));
        }
    }
}
