package org.isisaddons.module.security.app.feature;

import org.junit.Rule;
import org.junit.Test;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ApplicationFeatureViewModelsTest {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    ApplicationFeatureViewModels applicationFeatureViewModels;

    public static class IconName extends ApplicationFeatureViewModelsTest {

        @Test
        public void happyCase() throws Exception {

            applicationFeatureViewModels = new ApplicationFeatureViewModels();
            assertThat(applicationFeatureViewModels.iconName(), is("applicationFeature"));
        }
    }

}