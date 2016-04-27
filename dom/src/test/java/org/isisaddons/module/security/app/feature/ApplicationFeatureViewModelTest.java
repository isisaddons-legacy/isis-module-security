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

import java.util.Arrays;
import java.util.List;

import com.danhaywood.java.testsupport.coverage.PrivateConstructorTester;
import com.google.common.collect.Lists;

import org.hamcrest.CoreMatchers;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.services.appfeat.ApplicationMemberType;
import org.apache.isis.core.metamodel.services.appfeat.ApplicationFeature;
import org.apache.isis.core.metamodel.services.appfeat.ApplicationFeatureId;
import org.apache.isis.core.metamodel.services.appfeat.ApplicationFeatureRepositoryDefault;
import org.apache.isis.core.metamodel.services.appfeat.ApplicationFeatureType;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.value.ValueTypeContractTestAbstract;

import org.isisaddons.module.security.dom.permission.ApplicationPermission;
import org.isisaddons.module.security.dom.permission.ApplicationPermissionRepository;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class ApplicationFeatureViewModelTest {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    ApplicationFeatureViewModel applicationFeatureViewModel;

    @Mock
    ApplicationFeatureRepositoryDefault mockApplicationFeatureRepository;

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

    public static class Title extends ApplicationFeatureViewModelTest {

        @Test
        public void happyCase() throws Exception {

            applicationFeatureViewModel = new ApplicationFeatureViewModel(ApplicationFeatureId.newMember("com.mycompany.Bar#foo")) {
            };
            assertThat(applicationFeatureViewModel.title(), is("com.mycompany.Bar#foo"));
        }
    }

    public static class IconName extends ApplicationFeatureViewModelTest {

        @Test
        public void happyCase() throws Exception {

            applicationFeatureViewModel = new ApplicationFeatureViewModel() {
            };
            assertThat(applicationFeatureViewModel.iconName(), is("applicationFeature"));
        }
    }

    public static class PrivateConstructors {

        @Test
        public void forFunctions() throws Exception {
            new PrivateConstructorTester(ApplicationFeatureViewModel.Functions.class).exercise();
        }
    }

    public static abstract class ValueTypeContractTest extends ValueTypeContractTestAbstract<ApplicationFeatureViewModel> {

        static ApplicationFeatureViewModel pkg(ApplicationFeatureId applicationFeatureId) {
            return new ApplicationPackage(applicationFeatureId);
        }

        static ApplicationFeatureViewModel cls(ApplicationFeatureId applicationFeatureId) {
            return new ApplicationClass(applicationFeatureId);
        }

        static ApplicationFeatureViewModel prop(ApplicationFeatureId applicationFeatureId) {
            return new ApplicationClassProperty(applicationFeatureId);
        }

        static ApplicationFeatureViewModel coll(ApplicationFeatureId applicationFeatureId) {
            return new ApplicationClassCollection(applicationFeatureId);
        }

        static ApplicationFeatureViewModel act(ApplicationFeatureId applicationFeatureId) {
            return new ApplicationClassAction(applicationFeatureId);
        }


        public static class PackageFeatures extends ValueTypeContractTest {

            @Override
            protected List<ApplicationFeatureViewModel> getObjectsWithSameValue() {
                return Arrays.asList(
                        pkg(ApplicationFeatureId.newPackage("com.mycompany")),
                        pkg(ApplicationFeatureId.newPackage("com.mycompany")));
            }

            @Override
            protected List<ApplicationFeatureViewModel> getObjectsWithDifferentValue() {
                return Arrays.asList(
                        pkg(ApplicationFeatureId.newPackage("com.mycompany2")),
                        cls(ApplicationFeatureId.newClass("com.mycompany.Foo")),
                        prop(ApplicationFeatureId.newMember("com.mycompany.Foo#bar")));
            }
        }

        public static class ClassFeatures extends ValueTypeContractTest {

            @Override
            protected List<ApplicationFeatureViewModel> getObjectsWithSameValue() {
                return Arrays.asList(
                        cls(ApplicationFeatureId.newClass("com.mycompany.Foo")),
                        cls(ApplicationFeatureId.newClass("com.mycompany.Foo")));
            }

            @Override
            protected List<ApplicationFeatureViewModel> getObjectsWithDifferentValue() {
                return Arrays.asList(
                        pkg(ApplicationFeatureId.newPackage("com.mycompany")),
                        cls(ApplicationFeatureId.newClass("com.mycompany.Foo2")),
                        act(ApplicationFeatureId.newMember("com.mycompany.Foo#bar")));
            }
        }

        public static class PropertyFeatures extends ValueTypeContractTest {

            @Override
            protected List<ApplicationFeatureViewModel> getObjectsWithSameValue() {
                return Arrays.asList(
                        prop(ApplicationFeatureId.newMember("com.mycompany.Foo#bar")),
                        prop(ApplicationFeatureId.newMember("com.mycompany.Foo#bar")));
            }

            @Override
            protected List<ApplicationFeatureViewModel> getObjectsWithDifferentValue() {
                return Arrays.asList(
                        pkg(ApplicationFeatureId.newPackage("com.mycompany")),
                        cls(ApplicationFeatureId.newClass("com.mycompany.Foo")),
                        prop(ApplicationFeatureId.newMember("com.mycompany.Foo#bar2")));
            }
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
            applicationFeatureVM.applicationFeatureRepository = mockApplicationFeatureRepository;
            applicationFeatureVM.container = mockContainer;

            // then
            context.checking(new Expectations() {{
                allowing(mockApplicationFeatureRepository).findFeature(applicationFeatureId);
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
            applicationClass.applicationFeatureRepository = mockApplicationFeatureRepository;
            applicationClass.container = mockContainer;

            // then
            context.checking(new Expectations() {{
                allowing(mockApplicationFeatureRepository).findFeature(classId);
                will(returnValue(classFeature));

                allowing(mockApplicationFeatureRepository).findFeature(propertyId);
                will(returnValue(propertyFeature));

                allowing(mockApplicationFeatureRepository).findFeature(actionId);
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

    public static class GetFullyQualifiedClassName extends ApplicationFeatureViewModelTest {

        @Test
        public void happyCase() throws Exception {

            applicationFeatureViewModel = new ApplicationFeatureViewModel(ApplicationFeatureId.newMember("com.mycompany.Bar#foo")) {
            };
            assertThat(applicationFeatureViewModel.getFullyQualifiedName(), is("com.mycompany.Bar#foo"));
       }
    }

    public static class GetType extends ApplicationFeatureViewModelTest {

        @Test
        public void whenPackage() throws Exception {
            applicationFeatureViewModel = new ApplicationFeatureViewModel(ApplicationFeatureId.newPackage("com.mycompany")) {
            };
            assertThat(applicationFeatureViewModel.getType(), is(ApplicationFeatureType.PACKAGE));
        }
        @Test
        public void whenClass() throws Exception {
            applicationFeatureViewModel = new ApplicationFeatureViewModel(ApplicationFeatureId.newClass("com.mycompany.Bar")) {
            };
            assertThat(applicationFeatureViewModel.getType(), is(ApplicationFeatureType.CLASS));
        }
        @Test
        public void whenMember() throws Exception {
            applicationFeatureViewModel = new ApplicationFeatureViewModel(ApplicationFeatureId.newMember("com.mycompany.Bar#foo")) {
            };
            assertThat(applicationFeatureViewModel.getType(), is(ApplicationFeatureType.MEMBER));
        }
    }

    public static class HideClassName extends ApplicationFeatureViewModelTest {

        @Test
        public void whenPackage() throws Exception {
            applicationFeatureViewModel = new ApplicationFeatureViewModel(ApplicationFeatureId.newPackage("com.mycompany")) {
            };
            assertThat(applicationFeatureViewModel.hideClassName(), is(true));
        }
        @Test
        public void whenClass() throws Exception {
            applicationFeatureViewModel = new ApplicationFeatureViewModel(ApplicationFeatureId.newClass("com.mycompany.Bar")) {
            };
            assertThat(applicationFeatureViewModel.hideClassName(), is(false));
        }
        @Test
        public void whenMember() throws Exception {
            applicationFeatureViewModel = new ApplicationFeatureViewModel(ApplicationFeatureId.newMember("com.mycompany.Bar#foo")) {
            };
            assertThat(applicationFeatureViewModel.hideClassName(), is(false));
        }
    }

    public static class HideMemberName extends ApplicationFeatureViewModelTest {

        @Test
        public void whenPackage() throws Exception {
            applicationFeatureViewModel = new ApplicationFeatureViewModel(ApplicationFeatureId.newPackage("com.mycompany")) {
            };
            assertThat(applicationFeatureViewModel.hideMemberName(), is(true));
        }
        @Test
        public void whenClass() throws Exception {
            applicationFeatureViewModel = new ApplicationFeatureViewModel(ApplicationFeatureId.newClass("com.mycompany.Bar")) {
            };
            assertThat(applicationFeatureViewModel.hideMemberName(), is(true));
        }
        @Test
        public void whenMember() throws Exception {
            applicationFeatureViewModel = new ApplicationFeatureViewModel(ApplicationFeatureId.newMember("com.mycompany.Bar#foo")) {
            };
            assertThat(applicationFeatureViewModel.hideMemberName(), is(false));
        }
    }

    public static class GetContributed extends ApplicationFeatureViewModelTest {

        public static class PropertyImpl extends GetContributed {

            @Mock
            ApplicationFeature mockApplicationFeature;

            @Test
            public void delegatesToUnderlyingFeature() throws Exception {
                // given
                final ApplicationFeatureId featureId = ApplicationFeatureId.newMember("com.mycompany.Bar#foo");
                applicationFeatureViewModel = new ApplicationFeatureViewModel(featureId) {
                };
                applicationFeatureViewModel.applicationFeatureRepository = mockApplicationFeatureRepository;

                // then
                context.checking(new Expectations() {{
                    oneOf(mockApplicationFeatureRepository).findFeature(featureId);
                    will(returnValue(mockApplicationFeature));

                    oneOf(mockApplicationFeature).isContributed();
                    will(returnValue(true));
                }});

                // when
                assertThat(applicationFeatureViewModel.isContributed(), is(true));
            }
        }

        public static class Hide extends GetContributed {

            @Test
            public void whenPackage() throws Exception {
                applicationFeatureViewModel = new ApplicationFeatureViewModel(ApplicationFeatureId.newPackage("com.mycompany")) {
                };
                assertThat(applicationFeatureViewModel.hideContributed(), is(true));
            }
            @Test
            public void whenClass() throws Exception {
                applicationFeatureViewModel = new ApplicationFeatureViewModel(ApplicationFeatureId.newClass("com.mycompany.Bar")) {
                };
                assertThat(applicationFeatureViewModel.hideContributed(), is(true));
            }
            @Test
            public void whenMember() throws Exception {
                applicationFeatureViewModel = new ApplicationFeatureViewModel(ApplicationFeatureId.newMember("com.mycompany.Bar#foo")) {
                };
                assertThat(applicationFeatureViewModel.hideContributed(), is(false));
            }
        }

    }

    public static class Parent extends ApplicationFeatureViewModelTest {

        @Mock
        ApplicationFeature mockApplicationFeature;
        private ApplicationFeatureViewModel parent;

        @Before
        public void setUp() throws Exception {
            applicationFeatureViewModel = new ApplicationFeatureViewModel() {
            };
            applicationFeatureViewModel.applicationFeatureRepository = mockApplicationFeatureRepository;
            applicationFeatureViewModel.container = mockContainer;

            parent = new ApplicationFeatureViewModel() {
            };
        }

        @Test
        public void whenPackage() throws Exception {

            // given
            final ApplicationFeatureId featureId = ApplicationFeatureId.newPackage("com.mycompany");
            final ApplicationFeatureId parentId = ApplicationFeatureId.newPackage("com");
            applicationFeatureViewModel.setFeatureId(featureId);

            // then
            context.checking(new Expectations() {{
                allowing(mockApplicationFeatureRepository).findFeature(parentId);
                will(returnValue(mockApplicationFeature));

                oneOf(mockContainer).newViewModelInstance(ApplicationPackage.class, parentId.asEncodedString());
                will(returnValue(parent));
            }});

            // when
            assertThat(applicationFeatureViewModel.getParent(), is(parent));
        }

        @Test
        public void whenPackageTopLevel() throws Exception {

            // given
            final ApplicationFeatureId featureId = ApplicationFeatureId.newPackage("com");
            applicationFeatureViewModel.setFeatureId(featureId);

            // then
            context.checking(new Expectations() {{
                never(mockApplicationFeatureRepository);

                never(mockContainer);
            }});

            // when
            assertThat(applicationFeatureViewModel.getParent(), is(nullValue()));
        }

        // should this instead fail-fast, given that the parent should always exist if the child does?
        @Test
        public void whenParentNonExistent() throws Exception {

            // given
            final ApplicationFeatureId featureId = ApplicationFeatureId.newPackage("com.mycompany");
            final ApplicationFeatureId parentId = ApplicationFeatureId.newPackage("com");
            applicationFeatureViewModel.setFeatureId(featureId);

            // then
            context.checking(new Expectations() {{
                allowing(mockApplicationFeatureRepository).findFeature(parentId);
                will(returnValue(null));

                never(mockContainer);
            }});

            // when
            assertThat(applicationFeatureViewModel.getParent(), is(nullValue()));
        }

        @Test
        public void whenClass() throws Exception {

            // given
            final ApplicationFeatureId featureId = ApplicationFeatureId.newClass("com.mycompany.Bar");
            final ApplicationFeatureId parentId = ApplicationFeatureId.newPackage("com.mycompany");
            applicationFeatureViewModel.setFeatureId(featureId);

            // then
            context.checking(new Expectations() {{
                allowing(mockApplicationFeatureRepository).findFeature(with(equalTo(parentId)));
                will(returnValue(mockApplicationFeature));

                oneOf(mockContainer).newViewModelInstance(ApplicationPackage.class, parentId.asEncodedString());
                will(returnValue(parent));
            }});

            // when
            assertThat(applicationFeatureViewModel.getParent(), is(parent));
        }
        @Test
        public void whenMember() throws Exception {
            // given
            final ApplicationFeatureId featureId = ApplicationFeatureId.newMember("com.mycompany.Bar#foo");
            final ApplicationFeatureId parentId = ApplicationFeatureId.newClass("com.mycompany.Bar");
            applicationFeatureViewModel.setFeatureId(featureId);

            // then
            context.checking(new Expectations() {{
                allowing(mockApplicationFeatureRepository).findFeature(parentId);
                will(returnValue(mockApplicationFeature));

                oneOf(mockContainer).newViewModelInstance(ApplicationClass.class, parentId.asEncodedString());
                will(returnValue(parent));
            }});

            // when
            assertThat(applicationFeatureViewModel.getParent(), is(parent));
        }
    }

    public static class GetPermissions extends ApplicationFeatureViewModelTest {

        @Mock
        ApplicationPermissionRepository mockApplicationPermissionRepository;

        @Test
        public void delegatesToUnderlyingRepo() throws Exception {
            // given
            final ApplicationFeatureId featureId = ApplicationFeatureId.newMember("com.mycompany.Bar#foo");
            applicationFeatureViewModel = new ApplicationFeatureViewModel(featureId) {
            };
            applicationFeatureViewModel.applicationPermissionRepository = mockApplicationPermissionRepository;


            // then
            final List<ApplicationPermission> result = Lists.newArrayList();
            context.checking(new Expectations() {{
                oneOf(mockApplicationPermissionRepository).findByFeatureCached(featureId);
                will(returnValue(result));
            }});

            // when
            assertThat(applicationFeatureViewModel.getPermissions(), is(result));
        }
    }



}