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
package org.isisaddons.module.security.integtests.role;

import java.util.List;
import javax.inject.Inject;
import javax.jdo.JDODataStoreException;
import org.isisaddons.module.security.dom.actor.ApplicationRole;
import org.isisaddons.module.security.dom.actor.ApplicationRoles;
import org.isisaddons.module.security.fixture.scripts.SecurityModuleAppTearDownFixture;
import org.isisaddons.module.security.integtests.SecurityModuleAppIntegTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;

public class ApplicationRolesIntegTest extends SecurityModuleAppIntegTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUpData() throws Exception {
        scenarioExecution().install(new SecurityModuleAppTearDownFixture());
    }

    @Inject
    ApplicationRoles applicationRoles;


    public static class NewRole extends ApplicationRolesIntegTest {

        @Test
        public void happyCase() throws Exception {

            // given
            final List<ApplicationRole> before = applicationRoles.allRoles();
            Assert.assertThat(before.size(), is(0));

            // when
            final ApplicationRole applicationRole = applicationRoles.newRole("fred");
            Assert.assertThat(applicationRole.getName(), is("fred"));

            // then
            final List<ApplicationRole> after = applicationRoles.allRoles();
            Assert.assertThat(after.size(), is(1));
        }

        @Test
        public void alreadyExists() throws Exception {

            // then
            expectedExceptions.expect(JDODataStoreException.class);

            // given
            applicationRoles.newRole("guest");

            // when
            applicationRoles.newRole("guest");
        }
    }

    public static class FindByName extends ApplicationRolesIntegTest {

        @Before
        public void setUpData() throws Exception {
            scenarioExecution().install(new SecurityModuleAppTearDownFixture());
        }

        @Test
        public void happyCase() throws Exception {

            // given
            applicationRoles.newRole("guest");
            applicationRoles.newRole("root");

            // when
            final ApplicationRole guest = applicationRoles.findByName("guest");

            // then
            Assert.assertThat(guest, is(not(nullValue())));
            Assert.assertThat(guest.getName(), is("guest"));
        }

        @Test
        public void whenDoesntMatch() throws Exception {

            // given
            applicationRoles.newRole("guest");
            applicationRoles.newRole("root");

            // when
            final ApplicationRole nonExistent = applicationRoles.findByName("admin");

            // then
            Assert.assertThat(nonExistent, is(nullValue()));
        }
    }

    public static class AutoComplete extends ApplicationRolesIntegTest {

        @Test
        public void happyCase() throws Exception {

            // given
            applicationRoles.newRole("guest");
            applicationRoles.newRole("root");
            applicationRoles.newRole("admin");

            // when
            final List<ApplicationRole> after = applicationRoles.autoComplete("t");

            // then
            Assert.assertThat(after.size(), is(2)); // guest and root
        }
    }


    public static class AllTenancies extends ApplicationRolesIntegTest {

        @Test
        public void happyCase() throws Exception {

            // given
            applicationRoles.newRole("guest");
            applicationRoles.newRole("root");

            // when
            final List<ApplicationRole> after = applicationRoles.allRoles();

            // then
            Assert.assertThat(after.size(), is(2));
        }
    }


}