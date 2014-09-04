package org.isisaddons.module.security.app.user;

import com.danhaywood.java.testsupport.coverage.PrivateConstructorTester;
import org.junit.Rule;
import org.junit.Test;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

public class UserPermissionViewModelTest {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    private UserPermissionViewModel userPermissionViewModel;


    public static class PrivateConstructors {

        @Test
        public void forFunctions() throws Exception {
            new PrivateConstructorTester(UserPermissionViewModel.Functions.class).exercise();
        }
    }

}