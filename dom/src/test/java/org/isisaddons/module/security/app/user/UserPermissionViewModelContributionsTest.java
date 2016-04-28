package org.isisaddons.module.security.app.user;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.core.metamodel.services.appfeat.ApplicationFeature;
import org.apache.isis.core.metamodel.services.appfeat.ApplicationFeatureRepositoryDefault;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.isisaddons.module.security.dom.user.ApplicationUser;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class UserPermissionViewModelContributionsTest {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    ApplicationUser_permissions applicationUserPermissions;


    @Mock
    ApplicationFeatureRepositoryDefault mockApplicationFeatureRepository;

    @Mock
    DomainObjectContainer mockContainer;
    ApplicationUser applicationUser;

    @Before
    public void setUp() throws Exception {
        applicationUser = new ApplicationUser();
    }

    public static class Permissions extends UserPermissionViewModelContributionsTest {

        private Iterable<ApplicationFeature> asViewModelsArgFeatures;

        @Before
        public void setUp() throws Exception {
            applicationUserPermissions = new ApplicationUser_permissions(applicationUser) {
                @Override
                List<UserPermissionViewModel> asViewModels(Iterable<ApplicationFeature> features) {
                    asViewModelsArgFeatures = features;
                    return Lists.newArrayList();
                }
            };
            applicationUserPermissions.applicationFeatureRepository = mockApplicationFeatureRepository;
        }


        @Test
        public void happyCase() throws Exception {
            final Collection<ApplicationFeature> result = Lists.newArrayList();
            context.checking(new Expectations() {{
                oneOf(mockApplicationFeatureRepository).allMembers();
                will(returnValue(result));
            }});
            applicationUserPermissions.$$();

            assertThat(asViewModelsArgFeatures, is((Iterable<ApplicationFeature>)result));
        }
    }
}