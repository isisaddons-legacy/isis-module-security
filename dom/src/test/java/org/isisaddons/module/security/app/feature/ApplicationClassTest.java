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

public class ApplicationClassTest {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    ApplicationClass applicationClass;

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

        applicationClass = new ApplicationClass() {

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

    public static class GetActions extends ApplicationClassTest {

        @Test
        public void happyCase() throws Exception {

            context.checking(new Expectations() {{
                oneOf(mockApplicationFeature).getActions();
                will(returnValue(requestedFeatures));
            }});

            final List<ApplicationClassAction> actions = applicationClass.getActions();
            assertThat(actions, is(asViewModelsReturnValue));
            assertThat(asViewModelsMembersArg, is(requestedFeatures));
        }
    }

    public static class GetProperties extends ApplicationClassTest {

        @Test
        public void happyCase() throws Exception {

            context.checking(new Expectations() {{
                oneOf(mockApplicationFeature).getProperties();
                will(returnValue(requestedFeatures));
            }});

            final List<ApplicationClassProperty> properties = applicationClass.getProperties();
            assertThat(properties, is(asViewModelsReturnValue));
            assertThat(asViewModelsMembersArg, is(requestedFeatures));
        }
    }

    public static class GetCollections extends ApplicationClassTest {

        @Test
        public void happyCase() throws Exception {

            context.checking(new Expectations() {{
                oneOf(mockApplicationFeature).getCollections();
                will(returnValue(requestedFeatures));
            }});

            final List<ApplicationClassCollection> collections = applicationClass.getCollections();
            assertThat(collections, is(asViewModelsReturnValue));
            assertThat(asViewModelsMembersArg, is(requestedFeatures));
        }
    }


}