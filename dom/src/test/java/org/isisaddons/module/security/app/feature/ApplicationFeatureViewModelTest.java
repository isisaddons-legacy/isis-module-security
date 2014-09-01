package org.isisaddons.module.security.app.feature;

import java.util.List;
import org.hamcrest.CoreMatchers;
import org.isisaddons.module.security.dom.feature.*;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Rule;
import org.junit.Test;
import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class ApplicationFeatureViewModelTest {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock
    ApplicationFeatures mockApplicationFeatures;
    @Mock
    DomainObjectContainer mockContainer;

    public static class ViewModelRoundtrip extends ApplicationFeatureViewModelTest {

        @Test
        public void whenPackage() throws Exception {
            ApplicationPackage applicationPackage = new ApplicationPackage(ApplicationFeatureId.newPackage("com.mycompany"));

            final String str = applicationPackage.viewModelMemento();
            final ApplicationFeatureViewModel applicationFeatureVM2 = new ApplicationPackage();
            applicationFeatureVM2.viewModelInit(str);

            assertThat(applicationFeatureVM2.getType(), CoreMatchers.is(ApplicationFeatureType.PACKAGE));
            assertThat(applicationFeatureVM2.getPackageName(), is("com.mycompany"));
            assertThat(applicationFeatureVM2.getClassName(), is(nullValue()));
            assertThat(applicationFeatureVM2.getMemberName(), is(nullValue()));
        }

        @Test
        public void whenClass() throws Exception {
            ApplicationClass applicationClass = new ApplicationClass(ApplicationFeatureId.newClass("com.mycompany.Bar"));

            final String str = applicationClass.viewModelMemento();
            final ApplicationFeatureViewModel applicationFeatureVM2 = new ApplicationClass();
            applicationFeatureVM2.viewModelInit(str);

            assertThat(applicationFeatureVM2.getType(), is(ApplicationFeatureType.CLASS));
            assertThat(applicationFeatureVM2.getPackageName(), is("com.mycompany"));
            assertThat(applicationFeatureVM2.getClassName(), is("Bar"));
            assertThat(applicationFeatureVM2.getMemberName(), is(nullValue()));
        }

        @Test
        public void whenMember() throws Exception {
            ApplicationClassProperty applicationClassProperty = new ApplicationClassProperty(ApplicationFeatureId.newMember("com.mycompany.Bar", "foo"));

            final String str = applicationClassProperty.viewModelMemento();
            final ApplicationFeatureViewModel applicationFeatureVM2 = new ApplicationClassProperty();
            applicationFeatureVM2.viewModelInit(str);

            assertThat(applicationFeatureVM2.getType(), is(ApplicationFeatureType.MEMBER));
            assertThat(applicationFeatureVM2.getPackageName(), is("com.mycompany"));
            assertThat(applicationFeatureVM2.getClassName(), is("Bar"));
            assertThat(applicationFeatureVM2.getMemberName(), is("foo"));
        }
    }

    public static class GetContents extends ApplicationFeatureViewModelTest {

        @Test
        public void givenPackage_whenAddPackageAndClass() throws Exception {

            // given
            final ApplicationFeatureId applicationFeatureId = ApplicationFeatureId.newPackage("com.mycompany");
            final ApplicationFeatureId packageFeatureId = ApplicationFeatureId.newPackage("com.mycompany.flob");
            final ApplicationFeatureId classFeatureId = ApplicationFeatureId.newClass("com.mycompany.Bar");

            final ApplicationFeature applicationFeature = new ApplicationFeature(applicationFeatureId);

            applicationFeature.addToContents(packageFeatureId);
            applicationFeature.addToContents(classFeatureId);

            ApplicationPackage applicationFeatureVM = new ApplicationPackage(applicationFeatureId);
            applicationFeatureVM.applicationFeatures = mockApplicationFeatures;
            applicationFeatureVM.container = mockContainer;

            // then
            context.checking(new Expectations() {{
                allowing(mockApplicationFeatures).findFeature(applicationFeatureId);
                will(returnValue(applicationFeature));

                oneOf(mockContainer).newViewModelInstance(ApplicationFeatureViewModel.class, packageFeatureId.asEncodedString());
                will(returnValue(new ApplicationPackage()));

                oneOf(mockContainer).newViewModelInstance(ApplicationFeatureViewModel.class, classFeatureId.asEncodedString());
                will(returnValue(new ApplicationClass()));
            }});

            // when
            final List<ApplicationFeatureViewModel> contents = applicationFeatureVM.getContents();

            // then
            assertThat(contents.size(), is(2));
        }

    }

    public static class GetProperties extends ApplicationFeatureViewModelTest {

        @Test
        public void givenClass_whenAddMember() throws Exception {

            // given
            final ApplicationFeatureId applicationFeatureId = ApplicationFeatureId.newClass("com.mycompany.Bar");
            final ApplicationFeatureId memberFeatureId = ApplicationFeatureId.newMember("com.mycompany.Bar", "foo");
            final ApplicationFeatureId memberFeatureId2 = ApplicationFeatureId.newMember("com.mycompany.Bar", "boz");

            final ApplicationFeature applicationFeature = new ApplicationFeature(applicationFeatureId);

            applicationFeature.addToMembers(memberFeatureId, ApplicationMemberType.PROPERTY);
            applicationFeature.addToMembers(memberFeatureId2, ApplicationMemberType.PROPERTY);

            ApplicationClass applicationClass = new ApplicationClass(applicationFeatureId);
            applicationClass.applicationFeatures = mockApplicationFeatures;
            applicationClass.container = mockContainer;

            // then
            context.checking(new Expectations() {{
                allowing(mockApplicationFeatures).findFeature(applicationFeatureId);
                will(returnValue(applicationFeature));

                oneOf(mockContainer).newViewModelInstance(ApplicationClassProperty.class, memberFeatureId.asEncodedString());
                will(returnValue(new ApplicationClassProperty(memberFeatureId)));

                oneOf(mockContainer).newViewModelInstance(ApplicationClassAction.class, memberFeatureId2.asEncodedString());
                will(returnValue(new ApplicationClassAction(memberFeatureId2)));
            }});

            // when
            final List<ApplicationClassProperty> members = applicationClass.getProperties();

            // then
            assertThat(members.size(), is(2));
        }
    }
}