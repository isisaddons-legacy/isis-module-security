package org.isisaddons.module.security.app.feature;

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

public class ApplicationClassCollectionTest {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    ApplicationClassCollection applicationClassCollection;

    @Mock
    ApplicationFeature mockApplicationFeature;


    @Before
    public void setUp() throws Exception {

        applicationClassCollection = new ApplicationClassCollection() {
            @Override
            ApplicationFeature getFeature() {
                return mockApplicationFeature;
            }
        };
    }

    public static class GetFeatureId extends ApplicationClassCollectionTest {

        @Override
        @Before
        public void setUp() throws Exception {
            applicationClassCollection = new ApplicationClassCollection(ApplicationFeatureId.newMember("com.mycompany.Bar#foo")) {
            };
        }

        @Test
        public void happyCase() throws Exception {
            assertThat(applicationClassCollection.getFeatureId(), is(ApplicationFeatureId.newMember("com.mycompany.Bar#foo")));
        }
    }

    public static class GetElementType extends ApplicationClassCollectionTest {

        @Test
        public void happyCase() throws Exception {

            context.checking(new Expectations() {{
                oneOf(mockApplicationFeature).getReturnTypeName();
                will(returnValue("String"));
            }});

            assertThat(applicationClassCollection.getElementType(), is("String"));
        }
    }

    public static class IsDerived extends ApplicationClassCollectionTest {

        @Test
        public void happyCase() throws Exception {

            context.checking(new Expectations() {{
                oneOf(mockApplicationFeature).isDerived();
                will(returnValue(true));
            }});

            assertThat(applicationClassCollection.isDerived(), is(true));
        }
    }


}