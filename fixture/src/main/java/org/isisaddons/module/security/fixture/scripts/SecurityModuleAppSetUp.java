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

package org.isisaddons.module.security.fixture.scripts;

import org.isisaddons.module.security.fixture.dom.ExampleSecuredEntities;
import org.isisaddons.module.security.fixture.scripts.exampleEntities.AllEntitiesFixture;
import org.isisaddons.module.security.fixture.scripts.permission.AdminRoleAllowUsableToSelectedFeatures;
import org.isisaddons.module.security.fixture.scripts.roles.AllRolesFixture;
import org.isisaddons.module.security.fixture.scripts.tenancy.AllTenanciesFixture;
import org.isisaddons.module.security.fixture.scripts.userRoles.DickHasRegularUserRoleFixture;
import org.isisaddons.module.security.fixture.scripts.userRoles.DickHasWriteOnlyRoleFixture;
import org.isisaddons.module.security.fixture.scripts.userRoles.SvenHasAdminRoleFixture;
import org.isisaddons.module.security.fixture.scripts.users.AllUsersFixture;
import org.apache.isis.applib.fixturescripts.DiscoverableFixtureScript;

public class SecurityModuleAppSetUp extends DiscoverableFixtureScript {

    @Override
    protected void execute(ExecutionContext executionContext) {

        execute(new SecurityModuleAppTearDown(), executionContext);

        execute(new AllEntitiesFixture(), executionContext);

        execute(new AllRolesFixture(), executionContext);
        execute(new AllUsersFixture(), executionContext);
        execute(new AllTenanciesFixture(), executionContext);

        // user/role
        execute(new SvenHasAdminRoleFixture(), executionContext);
        execute(new DickHasRegularUserRoleFixture(), executionContext);
        execute(new DickHasWriteOnlyRoleFixture(), executionContext);

        // perms (role/features)
        execute(new AdminRoleAllowUsableToSelectedFeatures(), executionContext);

    }

    // //////////////////////////////////////

    @javax.inject.Inject
    private ExampleSecuredEntities exampleSecuredEntities;

}
