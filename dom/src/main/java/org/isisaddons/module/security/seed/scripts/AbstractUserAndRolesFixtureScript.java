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

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.value.Password;

import org.isisaddons.module.security.dom.password.PasswordEncryptionService;
import org.isisaddons.module.security.dom.role.ApplicationRole;
import org.isisaddons.module.security.dom.role.ApplicationRoleRepository;
import org.isisaddons.module.security.dom.user.AccountType;
import org.isisaddons.module.security.dom.user.ApplicationUser;
import org.isisaddons.module.security.dom.user.ApplicationUserRepository;

public class AbstractUserAndRolesFixtureScript extends FixtureScript {

    private final String username;
    private final String password;
    private final String emailAddress;
    private final String tenancyPath;
    private final AccountType accountType;
    private final List<String> roleNames;

    public AbstractUserAndRolesFixtureScript(
            final String username,
            final String password,
            final AccountType accountType, 
            final List<String> roleNames) {
        this(username, password, null, null, accountType, roleNames);
    }

    public AbstractUserAndRolesFixtureScript(
            final String username,
            final String password,
            final String emailAddress,
            final String tenancyPath,
            final AccountType accountType,
            final List<String> roleNames) {
        this.username = username;
        this.password = password;
        this.emailAddress = emailAddress;
        this.tenancyPath = tenancyPath;
        this.accountType = accountType;
        this.roleNames = Collections.unmodifiableList(Lists.newArrayList(roleNames));
    }

    @Override
    protected void execute(final ExecutionContext executionContext) {

        // create user if does not exist, and assign to the role
        applicationUser = applicationUserRepository.findByUsername(username);
        if(applicationUser == null) {
            final boolean enabled = true;
            switch (accountType) {
                case DELEGATED:
                    applicationUser = applicationUserRepository.newDelegateUser(username, null , enabled);
                    break;
                case LOCAL:
                    final Password pwd = new Password(password);
                    applicationUser = applicationUserRepository.newLocalUser(username, pwd, pwd, null, enabled, emailAddress);
            }

            // update tenancy (repository checks for null)
            applicationUser.setAtPath(tenancyPath);

            for (final String roleName : roleNames) {
                final ApplicationRole securityRole = applicationRoleRepository.findByName(roleName);
                applicationUser.addRole(securityRole);
            }
        }
    }

    private ApplicationUser applicationUser;

    /**
     * The {@link org.isisaddons.module.security.dom.user.ApplicationUser} updated/created by the fixture.
     */
    public ApplicationUser getApplicationUser() {
        return applicationUser;
    }

    //region  >  (injected)
    @Inject
    ApplicationUserRepository applicationUserRepository;
    @Inject
    ApplicationRoleRepository applicationRoleRepository;
    //endregion

}
