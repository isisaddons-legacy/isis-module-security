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

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.apache.isis.applib.services.wrapper.DisabledException;
import org.apache.isis.applib.services.wrapper.InvalidException;

import org.isisaddons.module.security.dom.role.ApplicationRole;
import org.isisaddons.module.security.dom.role.ApplicationRoleRepository;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyRepository;
import org.isisaddons.module.security.dom.user.ApplicationUser;
import org.isisaddons.module.security.dom.user.ApplicationUserMenu;
import org.isisaddons.module.security.dom.user.ApplicationUserRepository;
import org.isisaddons.module.security.fixture.scripts.SecurityModuleAppTearDown;
import org.isisaddons.module.security.fixture.scripts.roles.AllExampleRolesAndPermissions;
import org.isisaddons.module.security.fixture.scripts.roles.ExampleRegularRoleAndPermissions;
import org.isisaddons.module.security.fixture.scripts.tenancy.AllTenancies;
import org.isisaddons.module.security.fixture.scripts.tenancy.FranceTenancy;
import org.isisaddons.module.security.fixture.scripts.tenancy.SwedenTenancy;
import org.isisaddons.module.security.fixture.scripts.users.SvenUser;
import org.isisaddons.module.security.integtests.SecurityModuleAppIntegTest;
import org.isisaddons.module.security.seed.scripts.IsisModuleSecurityAdminRoleAndPermissions;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
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
                new IsisModuleSecurityAdminRoleAndPermissions(),
                new SvenUser()
        );
    }


    @Inject
    ApplicationUserMenu applicationUserMenu;
    @Inject
    ApplicationUserRepository applicationUserRepository;

    ApplicationUser user;

    @Before
    public void setUp() throws Exception {
        user = wrap(applicationUserRepository.findOrCreateUserByUsername(SvenUser.USER_NAME));
        assertThat(unwrap(user).getRoles().size(), is(0));

        assertThat(user, is(not(nullValue())));
        assertThat(user.getUsername(), is(SvenUser.USER_NAME));
    }

    public static class Username_and_UpdateUsername extends ApplicationUserIntegTest {

        public static class Username extends Username_and_UpdateUsername {

            @Test
            public void cannotModifyDirectly() throws Exception {

                // then
                expectedExceptions.expect(DisabledException.class);
                expectedExceptions.expectMessage("Reason: Always disabled. Identifier: org.isisaddons.module.security.dom.user.ApplicationUser#username()");

                // when
                user.setUsername("fred");
            }

        }

        public static class UpdateUsername extends Username_and_UpdateUsername {

            @Test
            public void toNewValue() throws Exception {

                // when
                final ApplicationUser updatedUser = user.updateUsername("fred");

                // then
                assertThat(updatedUser, is(unwrap(user)));
                assertThat(updatedUser.getUsername(), is("fred"));
            }

            @Test
            public void cannotSetToNull() throws Exception {

                // then
                expectedExceptions.expect(InvalidException.class);
                expectedExceptions.expectMessage(allOf(
                            containsString("Invalid action argument."),
                            containsString("Reason: 'Username' is mandatory.")
                        ));

                // when
                user.updateUsername(null);
            }
        }

    }

    public static class AtPath_and_UpdateAtPath extends ApplicationUserIntegTest {

        @Before
        public void setUpTenancies() throws Exception {
            scenarioExecution().install(
                    new AllTenancies()
            );
            // necessary to lookup again because above fixtures will be installed in a new xactn
            user = wrap(applicationUserRepository.findOrCreateUserByUsername(SvenUser.USER_NAME));

            swedenTenancy = applicationTenancyRepository.findByNameCached(SwedenTenancy.TENANCY_NAME);
            franceTenancy = applicationTenancyRepository.findByNameCached(FranceTenancy.TENANCY_NAME);

            assertThat(swedenTenancy, is(notNullValue()));
            assertThat(franceTenancy, is(notNullValue()));
        }

        @Inject
        ApplicationTenancyRepository applicationTenancyRepository;

        ApplicationTenancy swedenTenancy;
        ApplicationTenancy franceTenancy;

        public static class AtPath extends AtPath_and_UpdateAtPath {

            @Test
            public void cannotModifyDirectly() throws Exception {

                // then
                expectedExceptions.expect(DisabledException.class);
                expectedExceptions.expectMessage("Reason: Always disabled. Identifier: org.isisaddons.module.security.dom.user.ApplicationUser#atPath()");

                // when
                user.setAtPath(swedenTenancy.getPath());
            }

        }

        public static class UpdateAtPath extends AtPath_and_UpdateAtPath {

            @Test
            public void fromNullToNewValue() throws Exception {

                // given
                assertThat(user.getAtPath(), is(nullValue()));

                // when
                final ApplicationUser updatedUser = user.updateAtPath(swedenTenancy.getPath());

                // then
                assertThat(updatedUser, is(unwrap(user)));
                assertThat(updatedUser.getAtPath(), is(swedenTenancy.getPath()));
            }

            @Test
            public void fromValueToNewValue() throws Exception {

                // given
                user.updateAtPath(swedenTenancy.getPath());
                assertThat(user.getAtPath(), is(swedenTenancy.getPath()));

                // when
                user.updateAtPath(franceTenancy.getPath());

                // then
                assertThat(user.getAtPath(), is(franceTenancy.getPath()));
            }

            @Test
            public void fromValueToNull() throws Exception {

                // given
                user.updateAtPath(swedenTenancy.getPath());
                assertThat(user.getAtPath(), is(swedenTenancy.getPath()));

                // when
                user.updateAtPath(null);

                // then
                assertThat(user.getAtPath(), is(nullValue()));
            }

        }

    }

    public static class Roles extends ApplicationUserIntegTest {

        @Before
        public void setUpRoles() throws Exception {
            scenarioExecution().install(
                    new AllExampleRolesAndPermissions()
            );

            // necessary to lookup again because above fixtures will be installed in a new xactn
            user = wrap(applicationUserRepository.findOrCreateUserByUsername(SvenUser.USER_NAME));

            adminRole = applicationRoleRepository.findByNameCached(IsisModuleSecurityAdminRoleAndPermissions.ROLE_NAME);
            userRole = applicationRoleRepository.findByNameCached(ExampleRegularRoleAndPermissions.ROLE_NAME);

            assertThat(adminRole, is(notNullValue()));
            assertThat(userRole, is(notNullValue()));
        }

        @Inject
        ApplicationRoleRepository applicationRoleRepository;

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

            @Test
            public void cannotAddUsingSupportingMethod() throws Exception {

                // expect
                expectedException.expect(DisabledException.class);

                // when
                user.addToRoles(userRole);
            }

            @Test
            public void cannotAddDirectly() throws Exception {

                // expect
                expectedException.expect(UnsupportedOperationException.class);

                // when
                user.getRoles().add(userRole);
            }

        }
    }
}