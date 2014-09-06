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
package org.isisaddons.module.security.integtests.role;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.isisaddons.module.security.dom.role.ApplicationRole;
import org.isisaddons.module.security.dom.role.ApplicationRoles;
import org.isisaddons.module.security.fixture.scripts.SecurityModuleAppTearDown;
import org.isisaddons.module.security.integtests.SecurityModuleAppIntegTest;

public class ApplicationRolesIntegTest extends SecurityModuleAppIntegTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUpData() throws Exception {
        scenarioExecution().install(new SecurityModuleAppTearDown());
    }

    @Inject
    ApplicationRoles applicationRoles;


    public static class NewRole extends ApplicationRolesIntegTest {

        @Test
        public void happyCase() throws Exception {

            // given
            final List<ApplicationRole> before = applicationRoles.allRoles();
            assertThat(before.size(), is(0));

            // when
            final ApplicationRole applicationRole = applicationRoles.newRole("fred", null);
            assertThat(applicationRole.getName(), is("fred"));

            // then
            final List<ApplicationRole> after = applicationRoles.allRoles();
            assertThat(after.size(), is(1));
        }

        @Test
        public void alreadyExists() throws Exception {
            // given
            applicationRoles.newRole("guest", null);

            // when
            applicationRoles.newRole("guest", null);
            
            // then
            assertThat(applicationRoles.allRoles().size(), is(1));
        }

    }

    public static class FindByName extends ApplicationRolesIntegTest {

        @Before
        public void setUpData() throws Exception {
            scenarioExecution().install(new SecurityModuleAppTearDown());
        }

        @Test
        public void happyCase() throws Exception {

            // given
            applicationRoles.newRole("guest", null);
            applicationRoles.newRole("root", null);

            // when
            final ApplicationRole guest = applicationRoles.findRoleByName("guest");

            // then
            assertThat(guest, is(not(nullValue())));
            assertThat(guest.getName(), is("guest"));
        }

        @Test
        public void whenDoesntMatch() throws Exception {

            // given
            applicationRoles.newRole("guest", null);
            applicationRoles.newRole("root", null);

            // when
            final ApplicationRole nonExistent = applicationRoles.findRoleByName("admin");

            // then
            assertThat(nonExistent, is(nullValue()));
        }
    }



    public static class AllTenancies extends ApplicationRolesIntegTest {

        @Test
        public void happyCase() throws Exception {

            // given
            applicationRoles.newRole("guest", null);
            applicationRoles.newRole("root", null);

            // when
            final List<ApplicationRole> after = applicationRoles.allRoles();

            // then
            assertThat(after.size(), is(2));
        }
    }


}