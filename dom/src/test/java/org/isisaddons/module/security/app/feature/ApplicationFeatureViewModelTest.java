/*
 *  Copyright 2014 Dan Haywood
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
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

                oneOf(mockContainer).newViewModelInstance(ApplicationPackage.class, packageFeatureId.asEncodedString());
                will(returnValue(new ApplicationPackage()));

                oneOf(mockContainer).newViewModelInstance(ApplicationClass.class, classFeatureId.asEncodedString());
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
            final ApplicationFeatureId classId = ApplicationFeatureId.newClass("com.mycompany.Bar");
            final ApplicationFeatureId propertyId = ApplicationFeatureId.newMember("com.mycompany.Bar", "foo");
            final ApplicationFeatureId actionId = ApplicationFeatureId.newMember("com.mycompany.Bar", "boz");

            final ApplicationFeature classFeature = new ApplicationFeature(classId);
            final ApplicationFeature propertyFeature = new ApplicationFeature(propertyId);
            propertyFeature.setMemberType(ApplicationMemberType.PROPERTY);
            final ApplicationFeature actionFeature = new ApplicationFeature(actionId);
            actionFeature.setMemberType(ApplicationMemberType.ACTION);

            classFeature.addToMembers(propertyId, ApplicationMemberType.PROPERTY);
            classFeature.addToMembers(actionId, ApplicationMemberType.ACTION);

            ApplicationClass applicationClass = new ApplicationClass(classId);
            applicationClass.applicationFeatures = mockApplicationFeatures;
            applicationClass.container = mockContainer;

            // then
            context.checking(new Expectations() {{
                allowing(mockApplicationFeatures).findFeature(classId);
                will(returnValue(classFeature));

                allowing(mockApplicationFeatures).findFeature(propertyId);
                will(returnValue(propertyFeature));

                allowing(mockApplicationFeatures).findFeature(actionId);
                will(returnValue(actionFeature));

                oneOf(mockContainer).newViewModelInstance(ApplicationClassProperty.class, propertyId.asEncodedString());
                will(returnValue(new ApplicationClassProperty(propertyId)));

                oneOf(mockContainer).newViewModelInstance(ApplicationClassAction.class, actionId.asEncodedString());
                will(returnValue(new ApplicationClassAction(actionId)));
            }});

            // when
            final List<ApplicationClassProperty> properties = applicationClass.getProperties();

            // then
            assertThat(properties.size(), is(1));

            // when
            final List<ApplicationClassAction> actions = applicationClass.getActions();

            // then
            assertThat(actions.size(), is(1));
        }
    }
}