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
import org.isisaddons.module.security.fixture.scripts.perms.*;
import org.isisaddons.module.security.fixture.scripts.tenancy.AllTenancies;
import org.isisaddons.module.security.fixture.scripts.userrole.*;
import org.isisaddons.module.security.fixture.scripts.users.AllUsers;
import org.isisaddons.module.security.seed.SeedUsersAndRolesFixtureScript;
import org.apache.isis.applib.fixturescripts.DiscoverableFixtureScript;

public class SecurityModuleAppSetUp extends DiscoverableFixtureScript {

    @Override
    protected void execute(ExecutionContext executionContext) {

        execute(new SecurityModuleAppTearDown(), executionContext);
        execute(new SeedUsersAndRolesFixtureScript(), executionContext);

        execute(new AllExampleEntities(), executionContext);

        execute(new AllExampleRolesAndPermissions(), executionContext);
        execute(new AllUsers(), executionContext);
        execute(new AllTenancies(), executionContext);

        // perms (role/features)
        execute(new ExampleGuestRoleAndPremissions(), executionContext);
        execute(new ExampleRegularRoleAndPermissions(), executionContext);
        execute(new ExampleFixtureScriptsRoleAndPermissions(), executionContext);
        execute(new ExampleHideEntityDescriptionRoleAndPermissions(), executionContext);

        // user/role
        execute(new BobUser_Has_IsisSecurityAdminRole(), executionContext);
        execute(new BobUser_Has_ExampleHideEntityDescriptionRole(), executionContext);

        execute(new DickUser_Has_ExampleRegularRole(), executionContext);
        execute(new DickUser_Has_IsisSecurityModuleRegularRole(), executionContext);

        execute(new GuestUser_Has_ExampleGuestRole(), executionContext);
        execute(new GuestUser_Has_IsisSecurityModuleRegularRole(), executionContext);

        execute(new JoeUser_Has_ExampleGuestRole(), executionContext);
        execute(new JoeUser_Has_IsisSecurityModuleRegularRole(), executionContext);

        execute(new SvenUser_Has_IsisSecurityAdminRole(), executionContext);
    }

    // //////////////////////////////////////

    @javax.inject.Inject
    private ExampleEntities exampleEntities;

}
