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

        executionContext.executeChild(this, new SecurityModuleAppTearDown());
        executionContext.executeChild(this, new SeedUsersAndRolesFixtureScript());

        executionContext.executeChild(this, new AllExampleEntities());

        // roles and perms
        executionContext.executeChild(this, new ExampleGuestRoleAndPremissions());
        executionContext.executeChild(this, new ExampleNoGuestRoleAndPremissions());
        executionContext.executeChild(this, new ExampleRegularRoleAndPermissions());
        executionContext.executeChild(this, new ExampleFixtureScriptsRoleAndPermissions());
        executionContext.executeChild(this, new ExampleHideEntityDescriptionRoleAndPermissions());

        executionContext.executeChild(this, new AllExampleRolesAndPermissions());

        // users, tenancies
        executionContext.executeChild(this, new AllUsers());
        executionContext.executeChild(this, new AllTenancies());

        // user/role
        executionContext.executeChild(this, new BobUser_Has_IsisSecurityAdminRole());
        executionContext.executeChild(this, new BobUser_Has_ExampleHideEntityDescriptionRole());

        executionContext.executeChild(this, new DickUser_Has_ExampleRegularRole());
        executionContext.executeChild(this, new DickUser_Has_IsisSecurityModuleRegularRole());

        executionContext.executeChild(this, new GuestUser_Has_ExampleGuestRole());
        executionContext.executeChild(this, new GuestUser_Has_IsisSecurityModuleRegularRole());

        executionContext.executeChild(this, new JoeUser_Has_ExampleGuestRole());
        executionContext.executeChild(this, new JoeUser_Has_IsisSecurityModuleRegularRole());

        executionContext.executeChild(this, new SvenUser_Has_IsisSecurityAdminRole());

        executionContext.executeChild(this, new ConflictedUser_Has_ExampleConflictingRoles());
    }

    // //////////////////////////////////////

    @javax.inject.Inject
    private ExampleEntities exampleEntities;

}
