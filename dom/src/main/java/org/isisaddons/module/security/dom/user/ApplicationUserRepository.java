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
package org.isisaddons.module.security.dom.user;

import java.util.List;
import java.util.concurrent.Callable;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;
import org.apache.isis.applib.value.Password;

import org.isisaddons.module.security.dom.password.PasswordEncryptionService;
import org.isisaddons.module.security.dom.role.ApplicationRole;
import org.isisaddons.module.security.dom.role.ApplicationRoleRepository;

@SuppressWarnings("UnusedDeclaration")
@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = ApplicationUser.class
)
public class ApplicationUserRepository {

    //region > init

    @Programmatic
    @PostConstruct
    public void init() {
        if(applicationUserFactory == null) {
            applicationUserFactory = new ApplicationUserFactory.Default(container);
        }
    }

    //endregion

    //region > findOrCreateUserByUsername (programmatic)

    /**
     * Uses the {@link QueryResultsCache} in order to support
     * multiple lookups from <code>org.isisaddons.module.security.app.user.UserPermissionViewModel</code>.
     *
     * <p>
     *     If the user does not exist, it will be automatically created.
     * </p>
     */
    @Programmatic
    public ApplicationUser findOrCreateUserByUsername(
            final String username) {
        return queryResultsCache.execute(new Callable<ApplicationUser>() {
            @Override
            public ApplicationUser call() throws Exception {
                final ApplicationUser applicationUser = findUserByUsername(username);
                if (applicationUser != null) {
                    return applicationUser;
                }
                return newDelegateUser(username, null, null);
            }
        }, ApplicationUserRepository.class, "findOrCreateUserByUsername", username );
    }

    //endregion

    //region > findUserByName

    @Programmatic
    public ApplicationUser findUserByUsername(final String username) {
        return container.uniqueMatch(new QueryDefault<>(
                ApplicationUser.class,
                "findByUsername", "username", username));
    }

    //endregion

    //region > findUserByEmail (programmatic)

    @Programmatic
    public ApplicationUser findUserByEmail(final String emailAddress) {
        return container.uniqueMatch(new QueryDefault<>(
            ApplicationUser.class,
            "findByEmailAddress", "emailAddress", emailAddress));
    }
    //endregion

    //region > findUsersByName

    @Programmatic
    public List<ApplicationUser> findUsersByName(
            final String name) {
        final String nameRegex = "(?i).*" + name + ".*";
        return container.allMatches(new QueryDefault<>(
                ApplicationUser.class,
                "findByName", "nameRegex", nameRegex));
    }
    //endregion

    //region > newDelegateUser (action)

    @Programmatic
    public ApplicationUser newDelegateUser(
            final String username,
            final ApplicationRole initialRole,
            final Boolean enabled) {
        final ApplicationUser user = applicationUserFactory.newApplicationUser();
        user.setUsername(username);
        user.setStatus(ApplicationUserStatus.parse(enabled));
        user.setAccountType(AccountType.DELEGATED);
        if(initialRole != null) {
            user.addRole(initialRole);
        }
        container.persistIfNotAlready(user);
        return user;
    }
    //endregion

    //region > newLocalUser (action)

    @Programmatic
    public ApplicationUser newLocalUser(
            final String username,
            final Password password,
            final Password passwordRepeat,
            final ApplicationRole initialRole,
            final Boolean enabled,
            final String emailAddress) {
        ApplicationUser user = findUserByUsername(username);
        if (user == null){
            user = applicationUserFactory.newApplicationUser();
            user.setUsername(username);
            user.setStatus(ApplicationUserStatus.parse(enabled));
            user.setAccountType(AccountType.LOCAL);
        }
        if(initialRole != null) {
            user.addRole(initialRole);
        }
        if(password != null) {
            user.updatePassword(password.getPassword());
        }
        if(emailAddress != null) {
            user.updateEmailAddress(emailAddress);
        }
        container.persistIfNotAlready(user);
        return user;
    }

    @Programmatic
    public String validateNewLocalUser(
            final String username,
            final Password password,
            final Password passwordRepeat,
            final ApplicationRole initialRole,
            final Boolean enabled,
            final String emailAddress) {
        final ApplicationUser user = applicationUserFactory.newApplicationUser();
        return user.validateResetPassword(password, passwordRepeat);
    }

    //endregion

    //region > allUsers

    @Programmatic
    public List<ApplicationUser> allUsers() {
        return container.allInstances(ApplicationUser.class);
    }

    //endregion

    //region > autoComplete
    @Programmatic // not part of metamodel
    public List<ApplicationUser> autoComplete(final String name) {
        return findUsersByName(name);
    }
    //endregion

    //region  > injected
    @Inject
    QueryResultsCache queryResultsCache;
    @Inject
    PasswordEncryptionService passwordEncryptionService;
    @Inject
    ApplicationRoleRepository applicationRoleRepository;

    /**
     * Will only be injected to if the programmer has supplied an implementation.  Otherwise
     * this class will install a default implementation in {@link #init()}.
     */
    @Inject
    ApplicationUserFactory applicationUserFactory;
    @Inject
    DomainObjectContainer container;

    //endregion

}
