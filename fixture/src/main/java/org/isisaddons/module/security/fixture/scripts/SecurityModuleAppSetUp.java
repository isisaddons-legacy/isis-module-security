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
package org.isisaddons.module.security.fixture.scripts;

import org.isisaddons.module.security.fixture.dom.ExampleEntities;
import org.isisaddons.module.security.fixture.scripts.example.AllExampleEntities;
import org.isisaddons.module.security.fixture.scripts.roles.*;
import org.isisaddons.module.security.fixture.scripts.tenancy.AllTenancies;
import org.isisaddons.module.security.fixture.scripts.userrole.*;
import org.isisaddons.module.security.fixture.scripts.users.AllUsers;
import org.isisaddons.module.security.seed.SeedUsersAndRolesFixtureScript;
import org.apache.isis.applib.fixturescripts.DiscoverableFixtureScript;

public class SecurityModuleAppSetUp extends DiscoverableFixtureScript {

    @Override
    protected void execute(ExecutionContext executionContext) {

        executeChild(new SecurityModuleAppTearDown(), executionContext);
        executeChild(new SeedUsersAndRolesFixtureScript(), executionContext);

        executeChild(new AllExampleEntities(), executionContext);

        // roles and perms
        executeChild(new ExampleGuestRoleAndPremissions(), executionContext);
        executeChild(new ExampleNoGuestRoleAndPremissions(), executionContext);
        executeChild(new ExampleRegularRoleAndPermissions(), executionContext);
        executeChild(new ExampleFixtureScriptsRoleAndPermissions(), executionContext);
        executeChild(new ExampleHideEntityDescriptionRoleAndPermissions(), executionContext);

        executeChild(new AllExampleRolesAndPermissions(), executionContext);

        // users, tenancies
        executeChild(new AllUsers(), executionContext);
        executeChild(new AllTenancies(), executionContext);

        // user/role
        executeChild(new BobUser_Has_IsisSecurityAdminRole(), executionContext);
        executeChild(new BobUser_Has_ExampleHideEntityDescriptionRole(), executionContext);

        executeChild(new DickUser_Has_ExampleRegularRole(), executionContext);
        executeChild(new DickUser_Has_IsisSecurityModuleRegularRole(), executionContext);

        executeChild(new GuestUser_Has_ExampleGuestRole(), executionContext);
        executeChild(new GuestUser_Has_IsisSecurityModuleRegularRole(), executionContext);

        executeChild(new JoeUser_Has_ExampleGuestRole(), executionContext);
        executeChild(new JoeUser_Has_IsisSecurityModuleRegularRole(), executionContext);

        executeChild(new SvenUser_Has_IsisSecurityAdminRole(), executionContext);

        executeChild(new ConflictedUser_Has_ExampleConflictingRoles(), executionContext);
    }

    // //////////////////////////////////////

    @javax.inject.Inject
    private ExampleEntities exampleEntities;

}
