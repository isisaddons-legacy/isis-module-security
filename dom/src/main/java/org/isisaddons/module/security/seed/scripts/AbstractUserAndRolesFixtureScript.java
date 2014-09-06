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
package org.isisaddons.module.security.seed.scripts;

import java.util.Collections;
import java.util.List;
import javax.inject.Inject;
import com.google.common.collect.Lists;
import org.isisaddons.module.security.dom.role.ApplicationRole;
import org.isisaddons.module.security.dom.role.ApplicationRoles;
import org.isisaddons.module.security.dom.user.ApplicationUser;
import org.isisaddons.module.security.dom.user.ApplicationUserStatus;
import org.isisaddons.module.security.dom.user.ApplicationUsers;
import org.apache.isis.applib.fixturescripts.FixtureScript;

public class AbstractUserAndRolesFixtureScript extends FixtureScript {

    private final String username;
    private final String password;
    private final List<String> roleNames;

    public AbstractUserAndRolesFixtureScript(
            final String username,
            final String password,
            final List<String> roleNames) {
        this.username = username;
        this.password = password;
        this.roleNames = Collections.unmodifiableList(Lists.newArrayList(roleNames));
    }

    @Override
    protected void execute(ExecutionContext executionContext) {

        // create user if does not exist, and assign to the role
        ApplicationUser adminUser = applicationUsers.findUserByUsername(username);
        if(adminUser == null) {
            adminUser = applicationUsers.newUser(username, null , null);
            adminUser.setStatus(ApplicationUserStatus.ENABLED);

            if(applicationUsers.isPasswordsFeatureEnabled() && password != null) {
                adminUser.updatePassword(password);
            }

            for (String roleName : roleNames) {
                ApplicationRole securityAdminRole = applicationRoles.findRoleByName(roleName);
                adminUser.addRole(securityAdminRole);
            }
        }
    }

    //region  >  (injected)
    @Inject
    ApplicationUsers applicationUsers;
    @Inject
    ApplicationRoles applicationRoles;
    //endregion

}
