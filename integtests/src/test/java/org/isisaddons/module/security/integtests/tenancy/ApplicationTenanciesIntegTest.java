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
import javax.jdo.JDODataStoreException;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancies;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.isisaddons.module.security.fixture.scripts.SecurityModuleAppTearDown;
import org.isisaddons.module.security.integtests.SecurityModuleAppIntegTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class ApplicationTenanciesIntegTest extends SecurityModuleAppIntegTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUpData() throws Exception {
        scenarioExecution().install(new SecurityModuleAppTearDown());
    }

    @Inject
    ApplicationTenancies applicationTenancies;


    public static class NewTenancy extends ApplicationTenanciesIntegTest {

        @Test
        public void happyCase() throws Exception {

            // given
            final List<ApplicationTenancy> before = applicationTenancies.allTenancies();
            assertThat(before.size(), is(0));

            // when
            final ApplicationTenancy applicationTenancy = applicationTenancies.newTenancy("uk");
            assertThat(applicationTenancy.getName(), is("uk"));

            // then
            final List<ApplicationTenancy> after = applicationTenancies.allTenancies();
            assertThat(after.size(), is(1));
        }

        @Test
        public void alreadyExists() throws Exception {

            // given
            applicationTenancies.newTenancy("uk");

            // when
            applicationTenancies.newTenancy("uk");
            
            //then
            assertThat(applicationTenancies.allTenancies().size(), is(1));
        }
    }

    public static class FindByName extends ApplicationTenanciesIntegTest {

        @Test
        public void happyCase() throws Exception {

            // given
            applicationTenancies.newTenancy("portugal");
            applicationTenancies.newTenancy("uk");
            applicationTenancies.newTenancy("zambia");

            // when
            final ApplicationTenancy uk = applicationTenancies.findTenancyByName("uk");

            // then
            Assert.assertThat(uk, is(not(nullValue())));
            Assert.assertThat(uk.getName(), is("uk"));
        }

        @Test
        public void whenDoesntMatch() throws Exception {

            // given
            applicationTenancies.newTenancy("portugal");
            applicationTenancies.newTenancy("uk");

            // when
            final ApplicationTenancy nonExistent = applicationTenancies.findTenancyByName("france");

            // then
            Assert.assertThat(nonExistent, is(nullValue()));
        }
    }


    public static class AllTenancies extends ApplicationTenanciesIntegTest {

        @Test
        public void happyCase() throws Exception {

            // given
            applicationTenancies.newTenancy("uk");
            applicationTenancies.newTenancy("portugal");

            // when
            final List<ApplicationTenancy> after = applicationTenancies.allTenancies();

            // then
            Assert.assertThat(after.size(), is(2));
        }
    }


}