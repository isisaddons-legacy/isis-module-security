package org.isisaddons.module.security.app.feature;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import com.google.common.collect.Lists;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.metamodel.services.appfeat.ApplicationFeature;
import org.apache.isis.core.metamodel.services.appfeat.ApplicationFeatureId;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ApplicationPackageTest {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    ApplicationPackage applicationPackage;

    public static class GetContents extends ApplicationPackageTest {

        @Mock
        ApplicationFeature mockApplicationFeature;

        SortedSet<ApplicationFeatureId> asViewModelsMembersArg;
        SortedSet<ApplicationFeatureId> requestedFeatures;
        List asViewModelsReturnValue;

        @Before
        public void setUp() throws Exception {

            requestedFeatures = new TreeSet<>();

            asViewModelsMembersArg = null;
            asViewModelsReturnValue = Lists.newArrayList();

            applicationPackage = new ApplicationPackage() {

                @Override
                ApplicationFeature getFeature() {
                    return mockApplicationFeature;
                }

                @Override
                <T extends ApplicationFeatureViewModel> List<T> asViewModels(SortedSet<ApplicationFeatureId> members) {
                    asViewModelsMembersArg = members;
                    return (List<T>) asViewModelsReturnValue;
                }
            };
        }


        @Test
        public void happyCase() throws Exception {

            context.checking(new Expectations() {{
                oneOf(mockApplicationFeature).getContents();
                will(returnValue(requestedFeatures));
            }});

            final List<ApplicationFeatureViewModel> contents = applicationPackage.getContents();
            assertThat(contents, is(asViewModelsReturnValue));
            assertThat(asViewModelsMembersArg, is(requestedFeatures));
        }
    }

    public static class HideContents extends ApplicationPackageTest {

        @Test
        public void whenPackage() throws Exception {
            applicationPackage = new ApplicationPackage(ApplicationFeatureId.newPackage("com.mycompany"));
            assertThat(applicationPackage.hideContents(), is(false));
        }

        @Test
        public void whenClass() throws Exception {
            applicationPackage = new ApplicationPackage(ApplicationFeatureId.newClass("com.mycompany.Bar"));
            assertThat(applicationPackage.hideContents(), is(true));
        }

        @Test
        public void whenMember() throws Exception {
            applicationPackage = new ApplicationPackage(ApplicationFeatureId.newMember("com.mycompany.Bar", "foo"));
            assertThat(applicationPackage.hideContents(), is(true));
        }

    }

}