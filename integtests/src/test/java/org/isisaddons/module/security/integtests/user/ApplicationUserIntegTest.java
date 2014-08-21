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

import javax.inject.Inject;
import org.isisaddons.module.security.dom.actor.ApplicationRole;
import org.isisaddons.module.security.dom.actor.ApplicationRoles;
import org.isisaddons.module.security.dom.actor.ApplicationUser;
import org.isisaddons.module.security.dom.actor.ApplicationUsers;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancies;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.isisaddons.module.security.fixture.scripts.SecurityModuleAppTearDown;
import org.isisaddons.module.security.fixture.scripts.roles.AdminRoleFixture;
import org.isisaddons.module.security.fixture.scripts.roles.AllRolesFixture;
import org.isisaddons.module.security.fixture.scripts.roles.RegularUserRoleFixture;
import org.isisaddons.module.security.fixture.scripts.tenancy.AllTenanciesFixture;
import org.isisaddons.module.security.fixture.scripts.tenancy.FranceTenancyFixture;
import org.isisaddons.module.security.fixture.scripts.tenancy.SwedenTenancyFixture;
import org.isisaddons.module.security.fixture.scripts.users.SvenUserFixture;
import org.isisaddons.module.security.integtests.SecurityModuleAppIntegTest;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.apache.isis.applib.services.wrapper.DisabledException;
import org.apache.isis.applib.services.wrapper.InvalidException;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.core.Is.is;

public class ApplicationUserIntegTest extends SecurityModuleAppIntegTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUpData() throws Exception {
        scenarioExecution().install(
                new SecurityModuleAppTearDown(),
                new SvenUserFixture()
        );
    }


    @Inject
    ApplicationUsers applicationUsers;

    ApplicationUser user;

    @Before
    public void setUp() throws Exception {
        user = wrap(applicationUsers.findUserByName(SvenUserFixture.USER_NAME));
        assertThat(unwrap(user).getRoles().size(), is(0));

        assertThat(user, is(not(nullValue())));
        assertThat(user.getName(), is(SvenUserFixture.USER_NAME));
    }

    public static class Name_and_UpdateName extends ApplicationUserIntegTest {

        public static class Name extends Name_and_UpdateName {

            @Test
            public void cannotModifyDirectly() throws Exception {

                // then
                expectedExceptions.expect(DisabledException.class);
                expectedExceptions.expectMessage("Reason: Always disabled. Identifier: org.isisaddons.module.security.dom.actor.ApplicationUser#name()");

                // when
                user.setName("fred");
            }

        }

        public static class UpdateName extends Name_and_UpdateName {

            @Test
            public void toNewValue() throws Exception {

                // when
                final ApplicationUser updatedUser = user.updateName("fred");

                // then
                assertThat(updatedUser, is(unwrap(user)));
                assertThat(updatedUser.getName(), is("fred"));
            }

            @Test
            public void cannotSetToNull() throws Exception {

                // then
                expectedExceptions.expect(InvalidException.class);
                expectedExceptions.expectMessage(allOf(
                            containsString("Invalid action argument."),
                            containsString("Reason: 'Name' is mandatory.")
                        ));

                // when
                user.updateName(null);
            }
        }

    }

    public static class Tenancy_and_UpdateTenancy extends ApplicationUserIntegTest {

        @Before
        public void setUpTenancies() throws Exception {
            scenarioExecution().install(
                    new AllTenanciesFixture()
            );
            // necessary to lookup again because above fixtures will be installed in a new xactn
            user = wrap(applicationUsers.findUserByName(SvenUserFixture.USER_NAME));

            swedenTenancy = applicationTenancies.findTenanciesByName(SwedenTenancyFixture.TENANCY_NAME);
            franceTenancy = applicationTenancies.findTenanciesByName(FranceTenancyFixture.TENANCY_NAME);

            assertThat(swedenTenancy, is(notNullValue()));
            assertThat(franceTenancy, is(notNullValue()));
        }

        @Inject
        ApplicationTenancies applicationTenancies;

        ApplicationTenancy swedenTenancy;
        ApplicationTenancy franceTenancy;

        public static class Tenancy extends Tenancy_and_UpdateTenancy {

            @Test
            public void cannotModifyDirectly() throws Exception {

                // then
                expectedExceptions.expect(DisabledException.class);
                expectedExceptions.expectMessage("Reason: Always disabled. Identifier: org.isisaddons.module.security.dom.actor.ApplicationUser#tenancy()");

                // when
                user.setTenancy(swedenTenancy);
            }

        }

        public static class UpdateTenancy extends Tenancy_and_UpdateTenancy {

            @Test
            public void fromNullToNewValue() throws Exception {

                // given
                assertThat(user.getTenancy(), is(nullValue()));

                // when
                final ApplicationUser updatedUser = user.updateTenancy(swedenTenancy);

                // then
                assertThat(updatedUser, is(unwrap(user)));
                assertThat(updatedUser.getTenancy(), is(swedenTenancy));
            }

            @Test
            public void fromValueToNewValue() throws Exception {

                // given
                user.updateTenancy(swedenTenancy);
                assertThat(user.getTenancy(), is(swedenTenancy));

                // when
                user.updateTenancy(franceTenancy);

                // then
                assertThat(user.getTenancy(), is(franceTenancy));
            }

            @Test
            public void fromValueToNull() throws Exception {

                // given
                user.updateTenancy(swedenTenancy);
                assertThat(user.getTenancy(), is(swedenTenancy));

                // when
                user.updateTenancy(null);

                // then
                assertThat(user.getTenancy(), is(nullValue()));
            }

        }

    }

    public static class Roles extends ApplicationUserIntegTest {

        @Before
        public void setUpRoles() throws Exception {
            scenarioExecution().install(
                    new AllRolesFixture()
            );

            // necessary to lookup again because above fixtures will be installed in a new xactn
            user = wrap(applicationUsers.findUserByName(SvenUserFixture.USER_NAME));

            adminRole = applicationRoles.findRoleByName(AdminRoleFixture.ROLE_NAME);
            userRole = applicationRoles.findRoleByName(RegularUserRoleFixture.ROLE_NAME);

            assertThat(adminRole, is(notNullValue()));
            assertThat(userRole, is(notNullValue()));
        }

        @Inject
        ApplicationRoles applicationRoles;

        ApplicationRole adminRole;
        ApplicationRole userRole;

        public static class AddRole extends Roles {

            @Test
            public void whenEmpty() throws Exception {

                // given
                assertThat(user.getRoles().size(), is(0));

                // when
                user.addRole(adminRole);

                // then
                assertThat(user.getRoles().size(), is(1));
                assertThat(user.getRoles(), containsInAnyOrder(adminRole));
            }

            @Test
            public void whenNotEmptyAndAddDifferent() throws Exception {
                // given
                user.addRole(adminRole);
                assertThat(user.getRoles(), containsInAnyOrder(adminRole));
                assertThat(user.getRoles().size(), is(1));

                // when
                user.addRole(userRole);

                // then
                assertThat(user.getRoles().size(), is(2));
                assertThat(user.getRoles(), containsInAnyOrder(adminRole, userRole));
            }

            @Test
            public void whenAlreadyContains() throws Exception {

                // given
                user.addRole(adminRole);
                assertThat(user.getRoles(), containsInAnyOrder(adminRole));
                assertThat(user.getRoles().size(), is(1));

                // when
                user.addRole(adminRole);

                // then
                assertThat(user.getRoles(), containsInAnyOrder(adminRole));
                assertThat(user.getRoles().size(), is(1));
            }
        }

        public static class RemoveRole extends Roles {

            @Test
            public void whenContains() throws Exception {

                // given
                user.addRole(adminRole);
                assertThat(user.getRoles().size(), is(1));

                // when
                user.removeRole(adminRole);

                // then
                assertThat(user.getRoles().size(), is(0));
            }

            @Test
            public void whenDoesNotContain() throws Exception {

                // given
                user.addRole(adminRole);
                assertThat(user.getRoles().size(), is(1));

                // when
                user.removeRole(userRole);

                // then
                assertThat(user.getRoles().size(), is(1));
            }

        }
    }
}