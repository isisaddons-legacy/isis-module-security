package org.isisaddons.module.security.app.feature;

import org.isisaddons.module.security.dom.feature.ApplicationFeature;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ApplicationClassActionTest {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    ApplicationClassAction applicationClassAction;

    @Mock
    ApplicationFeature mockApplicationFeature;


    @Before
    public void setUp() throws Exception {

        applicationClassAction = new ApplicationClassAction() {
            @Override
            ApplicationFeature getFeature() {
                return mockApplicationFeature;
            }
        };
    }

    public static class GetReturnType extends ApplicationClassActionTest {

        @Test
        public void happyCase() throws Exception {

            context.checking(new Expectations() {{
                oneOf(mockApplicationFeature).getReturnTypeName();
                will(returnValue("String"));
            }});

            assertThat(applicationClassAction.getReturnType(), is("String"));
        }
    }

    public static class GetActionSemantics extends ApplicationClassActionTest {

        @Test
        public void happyCase() throws Exception {

            context.checking(new Expectations() {{
                oneOf(mockApplicationFeature).getActionSemantics();
                will(returnValue(ActionSemantics.Of.IDEMPOTENT));
            }});

            assertThat(applicationClassAction.getActionSemantics(), is(ActionSemantics.Of.IDEMPOTENT));
        }
    }

}