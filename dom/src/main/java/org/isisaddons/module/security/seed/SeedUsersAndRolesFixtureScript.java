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
package org.isisaddons.module.security.seed;

import java.util.List;
import javax.inject.Inject;
import org.isisaddons.module.security.dom.role.ApplicationRole;
import org.isisaddons.module.security.dom.role.ApplicationRoles;
import org.isisaddons.module.security.dom.user.ApplicationUser;
import org.isisaddons.module.security.dom.user.ApplicationUsers;
import org.isisaddons.module.security.seed.scripts.*;
import org.apache.isis.applib.fixturescripts.FixtureScript;

/**
 * This fixture script will be run automatically on start-up by virtue of the fact that the
 * {@link org.isisaddons.module.security.seed.SeedSecurityModuleService} is a
 * {@link org.apache.isis.applib.annotation.DomainService} and calls the setup during its
 * {@link org.isisaddons.module.security.seed.SeedSecurityModuleService#init() init} ({@link javax.annotation.PostConstruct}) method.
 */
public class SeedUsersAndRolesFixtureScript extends FixtureScript {

    @Override
    protected void execute(ExecutionContext executionContext) {

        // only run if there is no data.
        final List<ApplicationRole> roleList = applicationRoles.allRoles();
        if(!roleList.isEmpty()) {
            return;
        }
        final List<ApplicationUser> userList = applicationUsers.allUsers();
        if(!userList.isEmpty()) {
            return;
        }

        // security module
        execute(new IsisModuleSecurityAdminRoleAndPermissions(), executionContext);

        execute(new IsisModuleSecurityFixtureRoleAndPermissions(), executionContext);
        execute(new IsisModuleSecurityRegularUserRoleAndPermissions(), executionContext);

        execute(new IsisModuleSecurityAdminUser(), executionContext);

        // isis applib
        execute(new IsisApplibFixtureResultsRoleAndPermissions(), executionContext);
    }

    //region > injected
    @Inject
    ApplicationRoles applicationRoles;
    @Inject
    ApplicationUsers applicationUsers;
    //endregion
}
