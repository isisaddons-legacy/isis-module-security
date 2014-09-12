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
import org.isisaddons.module.security.dom.password.PasswordEncryptionService;
import org.isisaddons.module.security.dom.role.ApplicationRole;
import org.isisaddons.module.security.dom.role.ApplicationRoles;
import org.isisaddons.module.security.seed.scripts.IsisModuleSecurityRegularUserRoleAndPermissions;
import org.isisaddons.module.security.shiro.IsisModuleSecurityRealm;
import org.isisaddons.module.security.shiro.ShiroUtils;
import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.annotation.*;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;
import org.apache.isis.applib.value.Password;

@Named("Users")
@DomainService(menuOrder = "90.1", repositoryFor = ApplicationUser.class)
public class ApplicationUsers extends AbstractFactoryAndRepository {

    //region > identification
    public String iconName() {
        return "applicationUser";
    }
    //endregion

    //region > findUserByName

    /**
     * Uses the {@link org.apache.isis.applib.services.queryresultscache.QueryResultsCache} in order to support
     * multiple lookups from <code>org.isisaddons.module.security.app.user.UserPermissionViewModel</code>.
     *
     * <p>
     *     If the user does not exist, it will be automatically created.
     * </p>
     */
    @MemberOrder(sequence = "10.2")
    @ActionSemantics(Of.IDEMPOTENT)
    public ApplicationUser findOrCreateUserByUsername(
            final @Named("Username") @MaxLength(ApplicationUser.MAX_LENGTH_USERNAME) String username) {
        return queryResultsCache.execute(new Callable<ApplicationUser>() {
            @Override
            public ApplicationUser call() throws Exception {
                ApplicationUser applicationUser = findUserByUsername(username);
                if (applicationUser != null) {
                    return applicationUser;
                }
                return newDelegateUser(username, null, null);
            }
        }, ApplicationUsers.class, "findUserByUsername", username );
    }

    /**
     * Uses the {@link org.apache.isis.applib.services.queryresultscache.QueryResultsCache} in order to support
     * multiple lookups from <code>org.isisaddons.module.security.app.user.UserPermissionViewModel</code>.
     */
    @Programmatic
    public ApplicationUser findUserByUsername(
            final @Named("Username") String username) {
        return uniqueMatch(new QueryDefault<>(
                ApplicationUser.class,
                "findByUsername", "username", username));
    }
    //endregion

    //region > findUsersByName

    @MemberOrder(sequence = "10.3")
    @ActionSemantics(Of.SAFE)
    public List<ApplicationUser> findUsersByName(
            final @Named("Name") String name) {
        final String nameRegex = "(?i).*" + name + ".*";
        return allMatches(new QueryDefault<>(
                ApplicationUser.class,
                "findByName", "nameRegex", nameRegex));
    }
    //endregion

    //region > newUser (no password)

    @MemberOrder(sequence = "10.4")
    @ActionSemantics(Of.NON_IDEMPOTENT)
    @NotContributed
    public ApplicationUser newDelegateUser(
            final @Named("Name") @MaxLength(ApplicationUser.MAX_LENGTH_USERNAME) String username,
            final @Named("Initial role") @Optional ApplicationRole initialRole,
            final @Named("Enabled?") @Optional Boolean enabled) {
        ApplicationUser user = newTransientInstance(ApplicationUser.class);
        user.setUsername(username);
        user.setStatus(ApplicationUserStatus.parse(enabled));
        user.setAccountType(AccountType.DELEGATED);
        if(initialRole != null) {
            user.addRole(initialRole);
        }
        persist(user);
        return user;
    }

    public boolean hideNewDelegateUser(
            final String username,
            final ApplicationRole initialRole,
            final Boolean enabled) {
        return hasNoDelegateAuthenticationRealm();
    }

    public ApplicationRole default1NewDelegateUser() {
        return applicationRoles.findRoleByName(IsisModuleSecurityRegularUserRoleAndPermissions.ROLE_NAME);
    }

    //endregion

    //region > newUser (with password)
    @MemberOrder(sequence = "10.4")
    @ActionSemantics(Of.IDEMPOTENT)
    @NotContributed
    public ApplicationUser newLocalUser(
            final @Named("Name") @MaxLength(ApplicationUser.MAX_LENGTH_USERNAME) String username,
            final @Named("Password") @Optional Password password,
            final @Named("Repeat password") @Optional Password passwordRepeat,
            final @Named("Initial role") @Optional ApplicationRole initialRole,
            final @Named("Enabled?") @Optional Boolean enabled) {
        ApplicationUser user = findUserByUsername(username);
        if (user == null){
            user = newTransientInstance(ApplicationUser.class);
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
        persist(user);
        return user;
    }
    public String validateNewLocalUser(
            final String username,
            final Password password,
            final Password passwordRepeat,
            final ApplicationRole initialRole,
            final Boolean enabled) {
        ApplicationUser user = newTransientInstance(ApplicationUser.class);
        return user.validateResetPassword(password, passwordRepeat);
    }

    public ApplicationRole default3NewLocalUser() {
        return applicationRoles.findRoleByName(IsisModuleSecurityRegularUserRoleAndPermissions.ROLE_NAME);
    }
    //endregion

    //region > allUsers

    @MemberOrder(sequence = "10.9")
    @ActionSemantics(Of.SAFE)
    public List<ApplicationUser> allUsers() {
        return allInstances(ApplicationUser.class);
    }

    //endregion

    //region > autoComplete

    @Programmatic // not part of metamodel
    public List<ApplicationUser> autoComplete(final String name) {
        return findUsersByName(name);
    }
    //endregion

    //region > isPasswordsFeatureEnabled, isPasswordsFeatureDisabled


    private boolean hasNoDelegateAuthenticationRealm() {
        IsisModuleSecurityRealm imsr = ShiroUtils.getIsisModuleSecurityRealm();
        return imsr == null || !imsr.hasDelegateAuthenticationRealm();
    }

    //endregion

    //region  >  (injected)
    @Inject
    QueryResultsCache queryResultsCache;
    @Inject
    PasswordEncryptionService passwordEncryptionService;
    @Inject
    ApplicationRoles applicationRoles;
    //endregion

}
