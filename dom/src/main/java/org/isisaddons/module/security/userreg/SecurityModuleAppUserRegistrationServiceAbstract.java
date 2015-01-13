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
package org.isisaddons.module.security.userreg;

import javax.inject.Inject;

import org.apache.isis.applib.services.userreg.UserRegistrationService;
import org.apache.isis.applib.value.Password;
import org.isisaddons.module.security.dom.role.ApplicationRole;
import org.isisaddons.module.security.dom.user.ApplicationUser;
import org.isisaddons.module.security.dom.user.ApplicationUsers;

/**
 * An abstract implementation of {@link org.apache.isis.applib.services.userreg.UserRegistrationService}
 * with a single abstract method for the initial role of newly created local users
 */
public abstract class SecurityModuleAppUserRegistrationServiceAbstract implements UserRegistrationService {

    @Override
    public boolean usernameExists(String username) {
        return applicationUsers.findUserByUsername(username) != null;
    }

    @Override
    public void registerUser(
        final String username,
        final String passwordStr,
        final String emailAddress) {

        final Password password = new Password(passwordStr);
        ApplicationRole initialRole = getInitialRole();
        Boolean enabled = true;
        applicationUsers.newLocalUser(username, password, password, initialRole, enabled, emailAddress);
    }

    @Override
    public boolean emailExists(String emailAddress) {
        return applicationUsers.findUserByEmail(emailAddress) != null;
    }

    @Override
    public boolean updatePasswordByEmail(String emailAddress, String password) {
        boolean passwordUpdated = false;
        ApplicationUser user = applicationUsers.findUserByEmail(emailAddress);
        if (user != null) {
            user.updatePassword(password);
            passwordUpdated = true;
        }
        return passwordUpdated;
    }

    /**
     * @return The role to use for newly created local users
     */
    protected abstract ApplicationRole getInitialRole();

    @Inject
    private ApplicationUsers applicationUsers;
}
