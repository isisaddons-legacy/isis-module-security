package org.isisaddons.module.security.app.feature;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.metamodel.services.appfeat.ApplicationFeature;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ApplicationClassPropertyTest {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    ApplicationClassProperty applicationClassProperty;


    @Mock
    ApplicationFeature mockApplicationFeature;


    @Before
    public void setUp() throws Exception {

        applicationClassProperty = new ApplicationClassProperty() {
            @Override
            ApplicationFeature getFeature() {
                return mockApplicationFeature;
            }
        };
    }

    public static class GetReturnType extends ApplicationClassPropertyTest {

        @Test
        public void happyCase() throws Exception {

            context.checking(new Expectations() {{
                oneOf(mockApplicationFeature).getReturnTypeName();
                will(returnValue("String"));
            }});

            assertThat(applicationClassProperty.getReturnType(), is("String"));
        }
    }

    public static class IsDerived extends ApplicationClassPropertyTest {

        @Test
        public void happyCase() throws Exception {

            context.checking(new Expectations() {{
                oneOf(mockApplicationFeature).isDerived();
                will(returnValue(true));
            }});

            assertThat(applicationClassProperty.isDerived(), is(true));
        }
    }

    public static class MaxLength extends ApplicationClassPropertyTest {

        @Test
        public void happyCase() throws Exception {

            context.checking(new Expectations() {{
                oneOf(mockApplicationFeature).getPropertyMaxLength();
                will(returnValue(50));
            }});

            assertThat(applicationClassProperty.getMaxLength(), is(50));
        }
    }

    public static class HideMaxLength extends ApplicationClassPropertyTest {

        @Test
        public void whenString() throws Exception {

            context.checking(new Expectations() {{
                oneOf(mockApplicationFeature).getReturnTypeName();
                will(returnValue("String"));
            }});

            assertThat(applicationClassProperty.hideMaxLength(), is(false));
        }

        @Test
        public void whenNotString() throws Exception {

            context.checking(new Expectations() {{
                oneOf(mockApplicationFeature).getReturnTypeName();
                will(returnValue("ApplicationUser"));
            }});

            assertThat(applicationClassProperty.hideMaxLength(), is(true));
        }
    }

    public static class TypicalLength extends ApplicationClassPropertyTest {

        @Test
        public void happyCase() throws Exception {

            context.checking(new Expectations() {{
                oneOf(mockApplicationFeature).getPropertyTypicalLength();
                will(returnValue(25));
            }});

            assertThat(applicationClassProperty.getTypicalLength(), is(25));
        }
    }

    public static class HideTypicalLength extends ApplicationClassPropertyTest {

        @Test
        public void whenString() throws Exception {

            context.checking(new Expectations() {{
                oneOf(mockApplicationFeature).getReturnTypeName();
                will(returnValue("String"));
            }});

            assertThat(applicationClassProperty.hideTypicalLength(), is(false));
        }

        @Test
        public void whenNotString() throws Exception {

            context.checking(new Expectations() {{
                oneOf(mockApplicationFeature).getReturnTypeName();
                will(returnValue("ApplicationUser"));
            }});

            assertThat(applicationClassProperty.hideTypicalLength(), is(true));
        }
    }


}