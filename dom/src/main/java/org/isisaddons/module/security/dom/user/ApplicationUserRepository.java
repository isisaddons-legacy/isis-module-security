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

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.SemanticsOf;
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

    //region > findOrCreateUserByUsername (programmatic)

    /**
     * Uses the {@link QueryResultsCache} in order to support
     * multiple lookups from <code>org.isisaddons.module.security.app.user.UserPermissionViewModel</code>.
     * <p>
     * <p>
     * If the user does not exist, it will be automatically created.
     * </p>
     */
    @Programmatic
    public ApplicationUser findOrCreateUserByUsername(
            final String username) {
        // slightly unusual to cache a function that modifies state, but safe because this is idempotent
        return queryResultsCache.execute(new Callable<ApplicationUser>() {
            @Override
            public ApplicationUser call() throws Exception {
                final ApplicationUser applicationUser = findByUsername(username);
                if (applicationUser != null) {
                    return applicationUser;
                }
                return newDelegateUser(username, null, null);
            }
        }, ApplicationUserRepository.class, "findOrCreateUserByUsername", username);
    }

    //endregion

    //region > findByUsername

    @Programmatic
    public ApplicationUser findByUsernameCached(final String username) {
        return queryResultsCache.execute(new Callable<ApplicationUser>() {
            @Override public ApplicationUser call() throws Exception {
                return findByUsername(username);
            }
        }, ApplicationUserRepository.class, "findByUsernameCached", username);
    }

    @Programmatic
    public ApplicationUser findByUsername(final String username) {
        return container.uniqueMatch(new QueryDefault<>(
                ApplicationUser.class,
                "findByUsername", "username", username));
    }

    //endregion

    //region > findByEmailAddress (programmatic)

    @Programmatic
    public ApplicationUser findByEmailAddressCached(final String emailAddress) {
        return queryResultsCache.execute(new Callable<ApplicationUser>() {
            @Override public ApplicationUser call() throws Exception {
                return findByEmailAddress(emailAddress);
            }
        }, ApplicationUserRepository.class, "findByEmailAddressCached", emailAddress);
    }

    @Programmatic
    public ApplicationUser findByEmailAddress(final String emailAddress) {
        return container.uniqueMatch(new QueryDefault<>(
                ApplicationUser.class,
                "findByEmailAddress", "emailAddress", emailAddress));
    }
    //endregion

    //region > findByName

    @Programmatic
    public List<ApplicationUser> find(final String search) {
        final String regex = String.format("(?i).*%s.*", search.replace("*", ".*").replace("?", "."));
        return container.allMatches(new QueryDefault<>(
                ApplicationUser.class,
                "find", "regex", regex));
    }
    //endregion

    //region > newDelegateUser (action)

    @Programmatic
    public ApplicationUser newDelegateUser(
            final String username,
            final ApplicationRole initialRole,
            final Boolean enabled) {
        final ApplicationUser user = getApplicationUserFactory().newApplicationUser();
        user.setUsername(username);
        user.setStatus(ApplicationUserStatus.parse(enabled));
        user.setAccountType(AccountType.DELEGATED);
        if (initialRole != null) {
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
        ApplicationUser user = findByUsername(username);
        if (user == null) {
            user = getApplicationUserFactory().newApplicationUser();
            user.setUsername(username);
            user.setStatus(ApplicationUserStatus.parse(enabled));
            user.setAccountType(AccountType.LOCAL);
        }
        if (initialRole != null) {
            user.addRole(initialRole);
        }
        if (password != null) {
            user.updatePassword(password.getPassword());
        }
        if (emailAddress != null) {
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
        final ApplicationUser user = getApplicationUserFactory().newApplicationUser();
        return user.validateResetPassword(password, passwordRepeat);
    }

    //endregion

    //region > newLocalUserBasedOn (action)

    public ApplicationUser newLocalUserBasedOn(
            final String username,
            final Password password,
            final Password passwordRepeat,
            final ApplicationUser userWhosRolesShouldBeCloned,
            final Boolean enabled,
            final String emailAddress) {
        final ApplicationUser user = this.newLocalUser(username, password, passwordRepeat, null, enabled, emailAddress);
        for (ApplicationRole role : userWhosRolesShouldBeCloned.getRoles()) {
            user.addToRoles(role);
        }
        return user;
    }
    //endregion


    //region > allUsers

    @Programmatic
    public List<ApplicationUser> findByAtPath(final String atPath) {
        return container.allMatches(new QueryDefault<>(
                ApplicationUser.class,
                "findByAtPath", "atPath", atPath));
    }

    //endregion


    //region > allUsers

    @Programmatic
    public List<ApplicationUser> allUsers() {
        return container.allInstances(ApplicationUser.class);
    }

    //endregion

    @Action(semantics = SemanticsOf.SAFE)
    public List<ApplicationUser> findMatching(final String search) {
        if (search != null && search.length() > 0) {
            return find(search);
        }
        return Lists.newArrayList();
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
     * this class will install a default implementation in the {@link #getApplicationUserFactory() accessor}.
     */
    @Inject
    ApplicationUserFactory applicationUserFactory;

    private ApplicationUserFactory getApplicationUserFactory() {
        return applicationUserFactory != null
                ? applicationUserFactory
                : (applicationUserFactory = new ApplicationUserFactory.Default(container));
    }

    @Inject
    DomainObjectContainer container;

    //endregion

}
