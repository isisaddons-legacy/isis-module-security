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
package org.isisaddons.module.security.integtests.feature;

import java.util.Arrays;
import java.util.Collection;

import javax.inject.Inject;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.SelfDescribing;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.apache.isis.core.metamodel.services.appfeat.ApplicationFeature;
import org.apache.isis.core.metamodel.services.appfeat.ApplicationFeatureId;
import org.apache.isis.core.metamodel.services.appfeat.ApplicationFeatureRepositoryDefault;

import org.isisaddons.module.security.fixture.scripts.SecurityModuleAppTearDown;
import org.isisaddons.module.security.integtests.SecurityModuleAppIntegTest;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class ApplicationFeaturesIntegTest extends SecurityModuleAppIntegTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUpData() throws Exception {
        scenarioExecution().install(new SecurityModuleAppTearDown());
    }

    @Inject
    ApplicationFeatureRepositoryDefault applicationFeatureRepository;

    public static class AllPackages extends ApplicationFeaturesIntegTest {

        @Test
        public void happyCase() throws Exception {

            // when
            final Collection<ApplicationFeature> packages = applicationFeatureRepository.allPackages();

            // then
            assertThat(packages.size(), greaterThan(0));

            assertThat(packages, transformedBy(ApplicationFeature.Functions.GET_ID, containsAtLeast(
                    ApplicationFeatureId.newPackage("org")
                    ,
                    ApplicationFeatureId.newPackage("org.apache"),
                    ApplicationFeatureId.newPackage("org.apache.isis"),
                    ApplicationFeatureId.newPackage("org.apache.isis.applib"),
                    ApplicationFeatureId.newPackage("org.isisaddons"),
                    ApplicationFeatureId.newPackage("org.isisaddons.module"),
                    ApplicationFeatureId.newPackage("org.isisaddons.module.security"),
                    ApplicationFeatureId.newPackage("org.isisaddons.module.security.app.feature"),
                    ApplicationFeatureId.newPackage("org.isisaddons.module.security.app.user"),
                    ApplicationFeatureId.newPackage("org.isisaddons.module.security.dom"),
                    ApplicationFeatureId.newPackage("org.isisaddons.module.security.dom.role"),
                    ApplicationFeatureId.newPackage("org.isisaddons.module.security.dom.tenancy"),
                    ApplicationFeatureId.newPackage("org.isisaddons.module.security.dom.user"),
                    ApplicationFeatureId.newPackage("org.isisaddons.module.security.fixture.dom")
            )));
        }

    }

    public static class FindPackage extends ApplicationFeaturesIntegTest {

        @Test
        public void whenExistsAndContainsOnlyPackages() throws Exception {

            // when
            final ApplicationFeature pkg = applicationFeatureRepository.findPackage(ApplicationFeatureId.newPackage("org"));

            // then
            assertThat(pkg, is(notNullValue()));
            assertThat(pkg.getContents(), containsAtLeast(
                    ApplicationFeatureId.newPackage("org.apache"),
                    ApplicationFeatureId.newPackage("org.isisaddons")
            ));

        }

        @Test
        public void whenExistsAndContainsClasses() throws Exception {

            // when
            final ApplicationFeature pkg = applicationFeatureRepository.findPackage(ApplicationFeatureId.newPackage("org.isisaddons.module.security.dom.role"));

            // then
            assertThat(pkg, is(notNullValue()));
            assertThat(pkg.getContents(), containsAtLeast(
                    ApplicationFeatureId.newClass("org.isisaddons.module.security.dom.role.ApplicationRole"),
                    ApplicationFeatureId.newClass("org.isisaddons.module.security.dom.role.ApplicationRoleMenu")
            ));
        }

        @Test
        public void whenDoesNotExist() throws Exception {

            // when
            final ApplicationFeature pkg = applicationFeatureRepository.findPackage(ApplicationFeatureId.newPackage("org.nonExistent"));

            // then
            assertThat(pkg, is(nullValue()));
        }
    }

    //region > matcher helpers

    static <T,V> Matcher<? super Collection<T>> transformedBy(final Function<T, V> function, final Matcher<Collection<V>> underlying) {
        return new TypeSafeMatcher<Collection<T>>() {
            @Override
            protected boolean matchesSafely(final Collection<T> item) {
                return underlying.matches(
                        Lists.newArrayList(Iterables.transform(item, function)));
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("transformed by ");
                if(function instanceof SelfDescribing) {
                    SelfDescribing selfDescribingFunction = (SelfDescribing) function;
                    description.appendDescriptionOf(selfDescribingFunction);
                } else {
                    description.appendText("function ");
                }
                description.appendDescriptionOf(underlying);
            }
        };
    }

    static <T> Matcher<Collection<T>> containsAtLeast(final T... elements) {
        return new TypeSafeMatcher<Collection<T>>() {
            @Override
            protected boolean matchesSafely(Collection<T> candidate) {
                for (T element : elements) {
                    if(!candidate.contains(element)) {
                        return false;
                    }
                }
                return true;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("contains at least " + Arrays.asList(elements));
            }
        };
    }
    //endregion



}