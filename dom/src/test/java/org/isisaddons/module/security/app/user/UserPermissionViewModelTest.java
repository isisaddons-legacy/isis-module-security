package org.isisaddons.module.security.app.user;

import com.danhaywood.java.testsupport.coverage.PrivateConstructorTester;

import org.hamcrest.Description;
import org.jmock.Expectations;
import org.jmock.api.Action;
import org.jmock.api.Invocation;
import org.jmock.auto.Mock;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.ViewModel;
import org.apache.isis.core.metamodel.services.appfeat.ApplicationFeatureId;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.isisaddons.module.security.dom.permission.ApplicationPermission;
import org.isisaddons.module.security.dom.permission.ApplicationPermissionMode;
import org.isisaddons.module.security.dom.permission.ApplicationPermissionRepository;
import org.isisaddons.module.security.dom.permission.ApplicationPermissionRule;
import org.isisaddons.module.security.dom.permission.ApplicationPermissionValue;
import org.isisaddons.module.security.dom.permission.ApplicationPermissionValueSet;
import org.isisaddons.module.security.dom.role.ApplicationRole;
import org.isisaddons.module.security.dom.user.ApplicationUser;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

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


    public static class ViewModelRoundtrip extends UserPermissionViewModelTest {

        @Mock
        private ApplicationUser mockApplicationUser;
        @Mock
        private DomainObjectContainer mockContainer;
        @Mock
        ApplicationPermissionRepository mockApplicationPermissionRepository;

        @Test
        public void happyCase() throws Exception {

            // given
            final ApplicationFeatureId targetFeatureId = ApplicationFeatureId.newPackage("com.mycompany.Bar#foo");


            // can view because of ALLOW:CHANGING on com.mycompany
            final ApplicationFeatureId viewingFeatureId = ApplicationFeatureId.newPackage("com.mycompany");
            final ApplicationPermissionRule viewingRule = ApplicationPermissionRule.ALLOW;
            final ApplicationPermissionMode viewingMode = ApplicationPermissionMode.CHANGING;

            final ApplicationRole viewingRole = new ApplicationRole();
            viewingRole.setName("allowChangingComMycompany");
            final ApplicationPermission viewingPermission = new ApplicationPermission();
            viewingPermission.setRole(viewingRole);
            viewingPermission.setRule(viewingRule);
            viewingPermission.setMode(viewingMode);
            viewingPermission.setFeatureType(viewingFeatureId.getType());
            viewingPermission.setFeatureFqn(viewingFeatureId.getFullyQualifiedName());

            // cannot change because of VETO:CHANGING on com.mycompany.Bar
            final ApplicationFeatureId changingFeatureId = ApplicationFeatureId.newClass("com.mycompany.Bar");
            final ApplicationPermissionRule changingRule = ApplicationPermissionRule.VETO;
            final ApplicationPermissionMode changingMode = ApplicationPermissionMode.CHANGING;

            final ApplicationRole changingRole = new ApplicationRole();
            changingRole.setName("vetoChangingComMycompanyBar");
            final ApplicationPermission changingPermission = new ApplicationPermission();
            changingPermission.setRole(changingRole);
            changingPermission.setRule(changingRule);
            changingPermission.setMode(changingMode);
            changingPermission.setFeatureType(changingFeatureId.getType());
            changingPermission.setFeatureFqn(changingFeatureId.getFullyQualifiedName());


            context.checking(new Expectations() {{
                allowing(mockApplicationPermissionRepository).findByUserAndPermissionValue("fred", new ApplicationPermissionValue(viewingFeatureId, viewingRule, viewingMode));
                will(returnValue(viewingPermission));
                allowing(mockApplicationPermissionRepository).findByUserAndPermissionValue("fred", new ApplicationPermissionValue(changingFeatureId, changingRule, changingMode));
                will(returnValue(changingPermission));
            }});


            // and given
            context.checking(new Expectations() {{
                allowing(mockApplicationUser).getUsername();
                will(returnValue("fred"));

                allowing(mockContainer).newViewModelInstance(with(any(Class.class)), with(any(String.class)));
                will(returnInstantiatedAndInitializedViewModel());

            }});
            ApplicationPermissionValueSet.Evaluation viewingEvaluation = new ApplicationPermissionValueSet.Evaluation(
                    new ApplicationPermissionValue(viewingFeatureId, viewingRule, viewingMode), true);
            ApplicationPermissionValueSet.Evaluation changingEvaluation = new ApplicationPermissionValueSet.Evaluation(
                    new ApplicationPermissionValue(changingFeatureId, changingRule, changingMode), false);
            UserPermissionViewModel upvm = UserPermissionViewModel.newViewModel(targetFeatureId, mockApplicationUser, viewingEvaluation, changingEvaluation, mockContainer);


            // when
            final String str = upvm.viewModelMemento();
            final UserPermissionViewModel upvm2 = new UserPermissionViewModel();
            upvm2.applicationPermissionRepository = mockApplicationPermissionRepository;
            upvm2.viewModelInit(str);

            // then
            assertThat(upvm2.getFeatureId(), is(targetFeatureId));
            assertThat(upvm2.getViewingPermission().getFeatureType(), is(viewingFeatureId.getType()));
            assertThat(upvm2.getViewingPermission().getFeatureFqn(), is(viewingFeatureId.getFullyQualifiedName()));
            assertThat(upvm2.getViewingPermission().getRule(), is(viewingRule));
            assertThat(upvm2.getViewingPermission().getMode(), is(viewingMode));
            assertThat(upvm2.getChangingPermission().getFeatureType(), is(changingFeatureId.getType()));
            assertThat(upvm2.getChangingPermission().getFeatureFqn(), is(changingFeatureId.getFullyQualifiedName()));
            assertThat(upvm2.getChangingPermission().getRule(), is(changingRule));
            assertThat(upvm2.getChangingPermission().getMode(), is(changingMode));
        }

        private Action returnInstantiatedAndInitializedViewModel() {
            return new Action() {
                @Override
                public Object invoke(Invocation invocation) throws Throwable {
                    final Class<?> cls = (Class<?>) invocation.getParameter(0);
                    final String memento = (String) invocation.getParameter(1);
                    final ViewModel viewModelInstance = (ViewModel) cls.newInstance();
                    viewModelInstance.viewModelInit(memento);
                    if(viewModelInstance instanceof UserPermissionViewModel) {
                        ((UserPermissionViewModel) viewModelInstance).applicationPermissionRepository = mockApplicationPermissionRepository;
                    }
                    return viewModelInstance;
                }

                @Override
                public void describeTo(Description description) {
                    description.appendText("return instantiated and initialized view model ");
                }
            };
        }

    }


}