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
package org.isisaddons.module.security.integtests.tenancy;

import java.util.List;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyRepository;
import org.isisaddons.module.security.fixture.scripts.SecurityModuleAppTearDown;
import org.isisaddons.module.security.integtests.SecurityModuleAppIntegTest;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class ApplicationTenancyRepositoryIntegTest extends SecurityModuleAppIntegTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Inject
    ApplicationTenancyRepository applicationTenancyRepository;

    ApplicationTenancy globalTenancy;

    @Before
    public void setUpData() throws Exception {
        scenarioExecution().install(new SecurityModuleAppTearDown());

        globalTenancy = applicationTenancyRepository.findByPathCached("/");
    }

    public static class NewTenancy extends ApplicationTenancyRepositoryIntegTest {

        @Test
        public void happyCase() throws Exception {

            // given
            final List<ApplicationTenancy> before = applicationTenancyRepository.allTenancies();
            assertThat(before.size(), is(0));
            nextSession();

            // when
            final ApplicationTenancy applicationTenancy = applicationTenancyRepository.newTenancy("uk", "/uk", globalTenancy);
            assertThat(applicationTenancy.getName(), is("uk"));
            nextSession();

            // then
            final List<ApplicationTenancy> after = applicationTenancyRepository.allTenancies();
            assertThat(after.size(), is(1));
        }

        @Test
        public void alreadyExists() throws Exception {

            // given
            applicationTenancyRepository.newTenancy("UK", "/uk", globalTenancy);
            nextSession();

            // when
            applicationTenancyRepository.newTenancy("UK", "/uk", globalTenancy);
            nextSession();

            //then
            assertThat(applicationTenancyRepository.allTenancies().size(), is(1));
        }
    }

    public static class FindByName extends ApplicationTenancyRepositoryIntegTest {

        @Test
        public void happyCase() throws Exception {

            // given
            applicationTenancyRepository.newTenancy("portugal", "/po", globalTenancy);
            applicationTenancyRepository.newTenancy("uk", "/uk", globalTenancy);
            applicationTenancyRepository.newTenancy("zambia", "/za", globalTenancy);
            nextSession();

            // when
            final ApplicationTenancy uk = applicationTenancyRepository.findByNameCached("uk");

            // then
            Assert.assertThat(uk, is(not(nullValue())));
            Assert.assertThat(uk.getName(), is("uk"));
        }

        @Test
        public void whenDoesntMatch() throws Exception {

            // given
            applicationTenancyRepository.newTenancy("portugal", "/po", globalTenancy);
            applicationTenancyRepository.newTenancy("uk", "/uk", globalTenancy);

            // when
            final ApplicationTenancy nonExistent = applicationTenancyRepository.findByNameCached("france");

            // then
            Assert.assertThat(nonExistent, is(nullValue()));
        }
    }


    public static class FindByNameOrPathMatching extends ApplicationTenancyRepositoryIntegTest {

        @Test
        public void happyCase() throws Exception {

            // given
            applicationTenancyRepository.newTenancy("portugal", "/po", globalTenancy);
            applicationTenancyRepository.newTenancy("uk", "/uk", globalTenancy);
            applicationTenancyRepository.newTenancy("zambia", "/za", globalTenancy);

            // when, then
            Assert.assertThat(applicationTenancyRepository.findByNameOrPathMatchingCached("*").size(), is(3));
            Assert.assertThat(applicationTenancyRepository.findByNameOrPathMatchingCached("u").size(), is(2));
            Assert.assertThat(applicationTenancyRepository.findByNameOrPathMatchingCached("k").size(), is(1));
        }

        @Test
        public void whenDoesntMatch() throws Exception {

            // given
            applicationTenancyRepository.newTenancy("portugal", "/po", globalTenancy);
            applicationTenancyRepository.newTenancy("uk", "/uk", globalTenancy);

            // when
            final List<ApplicationTenancy> results = applicationTenancyRepository.findByNameOrPathMatchingCached(
                    "goat");

            // then
            Assert.assertThat(results.size(), is(0));
        }
    }


    public static class AllTenancyRepository extends ApplicationTenancyRepositoryIntegTest {

        @Test
        public void happyCase() throws Exception {

            // given
            applicationTenancyRepository.newTenancy("portugal", "/po", globalTenancy);
            applicationTenancyRepository.newTenancy("uk", "/uk", globalTenancy);

            // when
            final List<ApplicationTenancy> after = applicationTenancyRepository.allTenancies();

            // then
            Assert.assertThat(after.size(), is(2));
        }
    }


}