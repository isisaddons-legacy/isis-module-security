/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
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
import java.util.SortedSet;
import javax.inject.Inject;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.isisaddons.module.security.dom.feature.ApplicationFeature;
import org.isisaddons.module.security.dom.feature.ApplicationFeatures;
import org.isisaddons.module.security.fixture.scripts.SecurityModuleAppTearDownFixture;
import org.isisaddons.module.security.integtests.SecurityModuleAppIntegTest;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class ApplicationFeaturesIntegTest extends SecurityModuleAppIntegTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUpData() throws Exception {
        scenarioExecution().install(new SecurityModuleAppTearDownFixture());
    }

    @Inject
    ApplicationFeatures applicationFeatures;

    //region > matcher helpers

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

    public static class AllPackages extends ApplicationFeaturesIntegTest {

        @Test
        public void happyCase() throws Exception {

            // when
            final SortedSet<ApplicationFeature> packages = applicationFeatures.allPackages();

            // then
            assertThat(packages.size(), greaterThan(0));

            assertThat(packages, containsAtLeast(
                    ApplicationFeature.newPackage("org"),
                    ApplicationFeature.newPackage("org.apache"),
                    ApplicationFeature.newPackage("org.apache.isis"),
                    ApplicationFeature.newPackage("org.apache.isis.applib"),
                    ApplicationFeature.newPackage("org.isisaddons"),
                    ApplicationFeature.newPackage("org.isisaddons.module"),
                    ApplicationFeature.newPackage("org.isisaddons.module.security"),
                    ApplicationFeature.newPackage("org.isisaddons.module.security.app"),
                    ApplicationFeature.newPackage("org.isisaddons.module.security.dom"),
                    ApplicationFeature.newPackage("org.isisaddons.module.security.dom.actor"),
                    ApplicationFeature.newPackage("org.isisaddons.module.security.dom.feature"),
                    ApplicationFeature.newPackage("org.isisaddons.module.security.dom.tenancy"),
                    ApplicationFeature.newPackage("org.isisaddons.module.security.fixture.dom")
            ));

//            for (ApplicationFeature pkg : packages) {
//                System.out.println(pkg.toString());
//            }
//            System.out.flush();
        }
    }

    public static class FindPackage extends ApplicationFeaturesIntegTest {

        @Test
        public void whenExistsAndContainsOnlyPackages() throws Exception {

            // when
            final ApplicationFeature pkg = applicationFeatures.findPackage(ApplicationFeature.newPackage("org"));

            // then
            assertThat(pkg, is(notNullValue()));
            assertThat(pkg.getContents(), containsAtLeast(
                    ApplicationFeature.newPackage("org.apache"),
                    ApplicationFeature.newPackage("org.isisaddons")
            ));

        }

        @Test
        public void whenExistsAndContainsClasses() throws Exception {

            // when
            final ApplicationFeature pkg = applicationFeatures.findPackage(ApplicationFeature.newPackage("org.isisaddons.module.security.dom.actor"));

            // then
            assertThat(pkg, is(notNullValue()));
            assertThat(pkg.getContents(), containsAtLeast(
                    ApplicationFeature.newClass("org.isisaddons.module.security.dom.actor.ApplicationRole"),
                    ApplicationFeature.newClass("org.isisaddons.module.security.dom.actor.ApplicationRoles"),
                    ApplicationFeature.newClass("org.isisaddons.module.security.dom.actor.ApplicationUser"),
                    ApplicationFeature.newClass("org.isisaddons.module.security.dom.actor.ApplicationUsers")
            ));
        }

        @Test
        public void returnsValueEquivalentToTheCanonicalInstance() throws Exception {

            // given
            final ApplicationFeature canonicalParent = applicationFeatures.findPackage(ApplicationFeature.newPackage("org.isisaddons.module.security.dom"));
            final ApplicationFeature pkg = applicationFeatures.findPackage(ApplicationFeature.newPackage("org.isisaddons.module.security.dom.actor"));

            // when
            final ApplicationFeature parent = pkg.getParentPackage();

            // then
            assertThat(canonicalParent, is(notNullValue()));
            assertThat(pkg, is(notNullValue()));

            // is a value equaivalent to canonical...
            assertThat(parent, is(equalTo(canonicalParent)));
            // ... but is not the same instance
            assertThat(parent, is(not(sameInstance(canonicalParent))));

            // ... and only the canonical is wired up with contents
            assertThat(parent.getContents().size(), is(0));
            assertThat(canonicalParent.getContents().size(), greaterThan(0));
        }

        @Test
        public void whenDoesNotExist() throws Exception {

            // when
            final ApplicationFeature pkg = applicationFeatures.findPackage(ApplicationFeature.newPackage("org.nonExistent"));

            // then
            assertThat(pkg, is(nullValue()));
        }

    }



}