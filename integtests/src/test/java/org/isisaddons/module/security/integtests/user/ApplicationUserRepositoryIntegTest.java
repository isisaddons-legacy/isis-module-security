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
package org.isisaddons.module.security.integtests.user;

import java.util.List;

import javax.inject.Inject;
import javax.jdo.JDODataStoreException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.isisaddons.module.security.dom.user.ApplicationUser;
import org.isisaddons.module.security.dom.user.ApplicationUserMenu;
import org.isisaddons.module.security.dom.user.ApplicationUserRepository;
import org.isisaddons.module.security.fixture.scripts.SecurityModuleAppTearDown;
import org.isisaddons.module.security.integtests.SecurityModuleAppIntegTest;
import org.isisaddons.module.security.integtests.ThrowableMatchers;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ApplicationUserRepositoryIntegTest extends SecurityModuleAppIntegTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUpData() throws Exception {
        scenarioExecution().install(new SecurityModuleAppTearDown());
    }

    @Inject
    ApplicationUserMenu applicationUserMenu;
    @Inject
    ApplicationUserRepository applicationUserRepository;

    public static class NewUser extends ApplicationUserRepositoryIntegTest {

        @Test
        public void happyCase() throws Exception {

            // given
            final List<ApplicationUser> before = applicationUserMenu.allUsers();
            assertThat(before.size(), is(0));

            // when
            final ApplicationUser applicationUser = applicationUserMenu.newDelegateUser("fred", null, true);
            assertThat(applicationUser.getUsername(), is("fred"));

            // then
            final List<ApplicationUser> after = applicationUserMenu.allUsers();
            assertThat(after.size(), is(1));
        }

        @Test
        public void alreadyExists() throws Exception {

            // then
            expectedExceptions.expect(ThrowableMatchers.causalChainContains(JDODataStoreException.class));

            // given
            applicationUserMenu.newDelegateUser("fred", null, true);

            // when
            applicationUserMenu.newDelegateUser("fred", null, true);
        }
    }

    public static class FindByName extends ApplicationUserRepositoryIntegTest {

        @Test
        public void happyCase() throws Exception {

            // given
            applicationUserMenu.newDelegateUser("fred", null, true);
            applicationUserMenu.newDelegateUser("mary", null, true);

            // when
            final ApplicationUser fred = applicationUserRepository.findOrCreateUserByUsername("fred");

            // then
            assertThat(fred, is(not(nullValue())));
            assertThat(fred.getUsername(), is("fred"));
        }

        @Test
        public void whenDoesntMatchWillAutoCreate() throws Exception {

            // given
            applicationUserMenu.newDelegateUser("fred", null, true);
            applicationUserMenu.newDelegateUser("mary", null, true);

            // when
            final ApplicationUser autoCreated = applicationUserRepository.findOrCreateUserByUsername("bill");

            // then
            assertThat(autoCreated, is(not(nullValue())));
            assertThat(autoCreated.getUsername(), is("bill"));
        }

    }

    public static class Find extends ApplicationUserRepositoryIntegTest {

        @Test
        public void happyCase() throws Exception {

            // given
            applicationUserMenu.newDelegateUser("fred", null, true);
            final ApplicationUser mary = applicationUserMenu.newDelegateUser("mary", null, true);
            mary.setEmailAddress("mary@example.com");

            // when, then
            assertThat(applicationUserRepository.find("r").size(), is(2));
            assertThat(applicationUserRepository.find("x").size(), is(1));
        }
    }



        public static class AutoComplete extends ApplicationUserRepositoryIntegTest {

        @Test
        public void happyCase() throws Exception {

            // given
            applicationUserMenu.newDelegateUser("fred", null, true);
            applicationUserMenu.newDelegateUser("mary", null, true);
            applicationUserMenu.newDelegateUser("bill", null, true);

            // when
            final List<ApplicationUser> after = applicationUserRepository.autoComplete("r");

            // then
            assertThat(after.size(), is(2)); // fred and mary
        }
    }

    public static class AllTenancies extends ApplicationUserRepositoryIntegTest {

        @Test
        public void happyCase() throws Exception {

            // given
            applicationUserMenu.newDelegateUser("fred", null, true);
            applicationUserMenu.newDelegateUser("mary", null, true);

            // when
            final List<ApplicationUser> after = applicationUserMenu.allUsers();

            // then
            assertThat(after.size(), is(2));
        }
    }


}