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

import java.util.List;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.isisaddons.module.security.dom.role.ApplicationRole;
import org.isisaddons.module.security.dom.role.ApplicationRoleRepository;
import org.isisaddons.module.security.fixture.scripts.SecurityModuleAppTearDown;
import org.isisaddons.module.security.integtests.SecurityModuleAppIntegTest;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ApplicationRoleRepositoryIntegTest extends SecurityModuleAppIntegTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUpData() throws Exception {
        scenarioExecution().install(new SecurityModuleAppTearDown());
    }

    @Inject
    ApplicationRoleRepository applicationRoleRepository;


    public static class NewRole extends ApplicationRoleRepositoryIntegTest {

        @Test
        public void happyCase() throws Exception {

            // given
            final List<ApplicationRole> before = applicationRoleRepository.allRoles();
            assertThat(before.size(), is(0));

            // when
            final ApplicationRole applicationRole = applicationRoleRepository.newRole("fred", null);
            assertThat(applicationRole.getName(), is("fred"));

            // then
            final List<ApplicationRole> after = applicationRoleRepository.allRoles();
            assertThat(after.size(), is(1));
        }

        @Test
        public void alreadyExists() throws Exception {
            // given
            applicationRoleRepository.newRole("guest", null);

            // when
            applicationRoleRepository.newRole("guest", null);
            
            // then
            assertThat(applicationRoleRepository.allRoles().size(), is(1));
        }

    }

    public static class FindByName extends ApplicationRoleRepositoryIntegTest {

        @Before
        public void setUpData() throws Exception {
            scenarioExecution().install(new SecurityModuleAppTearDown());
        }

        @Test
        public void happyCase() throws Exception {

            // given
            applicationRoleRepository.newRole("guest", null);
            applicationRoleRepository.newRole("root", null);

            // when
            nextSession();
            final ApplicationRole guest = applicationRoleRepository.findByNameCached("guest");

            // then
            assertThat(guest, is(not(nullValue())));
            assertThat(guest.getName(), is("guest"));
        }

        @Test
        public void whenDoesntMatch() throws Exception {

            // given
            applicationRoleRepository.newRole("guest", null);
            applicationRoleRepository.newRole("root", null);
            nextSession();

            // when
            final ApplicationRole nonExistent = applicationRoleRepository.findByNameCached("admin");

            // then
            assertThat(nonExistent, is(nullValue()));
        }
    }

    public static class Find extends ApplicationRoleRepositoryIntegTest {

        @Before
        public void setUpData() throws Exception {
            scenarioExecution().install(new SecurityModuleAppTearDown());
        }

        @Test
        public void happyCase() throws Exception {

            // given
            applicationRoleRepository.newRole("guest", null);
            applicationRoleRepository.newRole("root", null);

            // when
            nextSession();
            final List<ApplicationRole> result = applicationRoleRepository.findNameContaining("t");

            // then
            assertThat(result.size(), is(2));
            //assertThat(guest.getName(), is("guest"));
        }

        @Test
        public void whenDoesntMatch() throws Exception {

            // given
            applicationRoleRepository.newRole("guest", null);
            applicationRoleRepository.newRole("root", null);
            nextSession();

            // when
            final List<ApplicationRole> result = applicationRoleRepository.findNameContaining("a");

            // then
            assertThat(result.size(), is(0));
            //assertThat(guest.getName(), is("guest"));
        }
    }


    public static class AllTenancies extends ApplicationRoleRepositoryIntegTest {

        @Test
        public void happyCase() throws Exception {

            // given
            applicationRoleRepository.newRole("guest", null);
            applicationRoleRepository.newRole("root", null);

            // when
            final List<ApplicationRole> after = applicationRoleRepository.allRoles();

            // then
            assertThat(after.size(), is(2));
        }
    }


}