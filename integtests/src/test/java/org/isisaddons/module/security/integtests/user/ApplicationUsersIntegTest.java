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
package org.isisaddons.module.security.integtests.user;

import java.util.List;
import javax.inject.Inject;
import javax.jdo.JDODataStoreException;
import org.isisaddons.module.security.dom.user.ApplicationUser;
import org.isisaddons.module.security.dom.user.ApplicationUsers;
import org.isisaddons.module.security.fixture.scripts.SecurityModuleAppTearDown;
import org.isisaddons.module.security.integtests.SecurityModuleAppIntegTest;
import org.isisaddons.module.security.integtests.ThrowableMatchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ApplicationUsersIntegTest extends SecurityModuleAppIntegTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUpData() throws Exception {
        scenarioExecution().install(new SecurityModuleAppTearDown());
    }

    @Inject
    ApplicationUsers applicationUsers;

    public static class NewUser extends ApplicationUsersIntegTest {

        @Test
        public void happyCase() throws Exception {

            // given
            final List<ApplicationUser> before = applicationUsers.allUsers();
            assertThat(before.size(), is(0));

            // when
            final ApplicationUser applicationUser = applicationUsers.newUser("fred");
            assertThat(applicationUser.getUsername(), is("fred"));

            // then
            final List<ApplicationUser> after = applicationUsers.allUsers();
            assertThat(after.size(), is(1));
        }

        @Test
        public void alreadyExists() throws Exception {

            // then
            expectedExceptions.expect(ThrowableMatchers.causalChainContains(JDODataStoreException.class));

            // given
            applicationUsers.newUser("fred");

            // when
            applicationUsers.newUser("fred");
        }
    }

    public static class FindByName extends ApplicationUsersIntegTest {

        @Test
        public void happyCase() throws Exception {

            // given
            applicationUsers.newUser("fred");
            applicationUsers.newUser("mary");

            // when
            final ApplicationUser fred = applicationUsers.findUserByUsername("fred");

            // then
            assertThat(fred, is(not(nullValue())));
            assertThat(fred.getUsername(), is("fred"));
        }

        @Test
        public void whenDoesntMatchWillAutoCreate() throws Exception {

            // given
            applicationUsers.newUser("fred");
            applicationUsers.newUser("mary");

            // when
            final ApplicationUser autoCreated = applicationUsers.findUserByUsername("bill");

            // then
            assertThat(autoCreated, is(not(nullValue())));
            assertThat(autoCreated.getUsername(), is("bill"));
        }

    }

    public static class AutoComplete extends ApplicationUsersIntegTest {

        @Test
        public void happyCase() throws Exception {

            // given
            applicationUsers.newUser("fred");
            applicationUsers.newUser("mary");
            applicationUsers.newUser("bill");

            // when
            final List<ApplicationUser> after = applicationUsers.autoComplete("r");

            // then
            assertThat(after.size(), is(2)); // fred and mary
        }
    }

    public static class AllTenancies extends ApplicationUsersIntegTest {

        @Test
        public void happyCase() throws Exception {

            // given
            applicationUsers.newUser("fred");
            applicationUsers.newUser("mary");

            // when
            final List<ApplicationUser> after = applicationUsers.allUsers();

            // then
            assertThat(after.size(), is(2));
        }
    }


}