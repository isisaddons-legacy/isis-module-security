package org.isisaddons.module.security.app.user;

import java.util.Collection;
import java.util.List;
import com.google.common.collect.Lists;
import org.isisaddons.module.security.dom.feature.ApplicationFeature;
import org.isisaddons.module.security.dom.feature.ApplicationFeatures;
import org.isisaddons.module.security.dom.user.ApplicationUser;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class UserPermissionViewModelContributionsTest {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    UserPermissionViewModelContributions userPermissionViewModelContributions;


    @Mock
    ApplicationFeatures mockApplicationFeatures;

    @Mock
    DomainObjectContainer mockContainer;
    ApplicationUser applicationUser;

    @Before
    public void setUp() throws Exception {
        applicationUser = new ApplicationUser();
    }

    public static class Permissions extends UserPermissionViewModelContributionsTest {

        private ApplicationUser asViewModelsUser;
        private Collection<ApplicationFeature> asViewModelsArgFeatures;

        @Before
        public void setUp() throws Exception {
            userPermissionViewModelContributions = new UserPermissionViewModelContributions() {
                @Override
                List<UserPermissionViewModel> asViewModels(ApplicationUser user, Collection<ApplicationFeature> features) {
                    asViewModelsUser = user;
                    asViewModelsArgFeatures = features;
                    return Lists.newArrayList();
                }
            };
            userPermissionViewModelContributions.applicationFeatures = mockApplicationFeatures;
        }


        @Test
        public void happyCase() throws Exception {
            final Collection<ApplicationFeature> result = Lists.newArrayList();
            context.checking(new Expectations() {{
                oneOf(mockApplicationFeatures).allMembers();
                will(returnValue(result));
            }});
            userPermissionViewModelContributions.permissions(applicationUser);

            assertThat(asViewModelsUser, is(applicationUser));
            assertThat(asViewModelsArgFeatures, is(result));
        }
    }
}