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
package org.isisaddons.module.security.webapp;

import javax.inject.Inject;
import org.isisaddons.module.security.dom.role.ApplicationRole;
import org.isisaddons.module.security.dom.role.ApplicationRoles;
import org.isisaddons.module.security.dom.user.ApplicationUser;
import org.isisaddons.module.security.dom.user.ApplicationUsers;
import org.isisaddons.module.security.fixture.scripts.roles.ExampleRegularRoleAndPermissions;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.services.userreg.UserRegistrationService;
import org.apache.isis.applib.value.Password;

@DomainService
public class SecurityModuleAppUserRegistrationService implements UserRegistrationService {

    @Override
    public void registerUser(
        final String username,
        final String passwordStr,
        final String emailAddress) {

        final Password password = new Password(passwordStr);
        ApplicationRole initialRole = applicationRoles.findRoleByName(ExampleRegularRoleAndPermissions.ROLE_NAME);
        Boolean enabled = true;
        ApplicationUser applicationUser = applicationUsers.newLocalUser(username, password, password, initialRole, enabled);
        applicationUser.setEmailAddress(emailAddress);
    }

    @Override
    public boolean emailExists(String emailAddress) {
        return applicationUsers.findUserByEmail(emailAddress) != null;
    }

    @Inject
    private ApplicationUsers applicationUsers;

    @Inject
    private ApplicationRoles applicationRoles;

}
