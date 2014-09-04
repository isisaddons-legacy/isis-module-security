package org.isisaddons.module.security.app.user;

import org.junit.Rule;
import org.junit.Test;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MeServiceTest {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    MeService meService;

    public static class IconName extends MeServiceTest {

        @Test
        public void happyCase() throws Exception {

            meService = new MeService();
            assertThat(meService.iconName(), is("applicationUser"));
        }
    }


}