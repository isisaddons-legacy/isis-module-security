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
import org.isisaddons.module.security.dom.password.PasswordEncryptionService;
import org.isisaddons.module.security.dom.role.ApplicationRole;
import org.isisaddons.module.security.dom.role.ApplicationRoles;
import org.isisaddons.module.security.seed.scripts.IsisModuleSecurityRegularUserRoleAndPermissions;
import org.isisaddons.module.security.shiro.IsisModuleSecurityRealm;
import org.isisaddons.module.security.shiro.ShiroUtils;
import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.Identifier;
import org.apache.isis.applib.annotation.ActionInteraction;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MaxLength;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NotContributed;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.eventbus.ActionInteractionEvent;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;
import org.apache.isis.applib.value.Password;

@DomainServiceLayout(
        named="Security",
        menuBar = DomainServiceLayout.MenuBar.SECONDARY,
        menuOrder = "100.10"
)
@DomainService(repositoryFor = ApplicationUser.class)
public class ApplicationUsers extends AbstractFactoryAndRepository {

    //region > identification
    public String iconName() {
        return "applicationUser";
    }
    //endregion

    //region > init

    @Programmatic
    @PostConstruct
    public void init() {
        if(applicationUserFactory == null) {
            applicationUserFactory = new ApplicationUserFactory.Default(getContainer());
        }
    }

    //endregion

    //region > findUserByName

    public static class FindOrCreateUserByUsernameEvent extends ActionInteractionEvent<ApplicationUsers> {
        public FindOrCreateUserByUsernameEvent(ApplicationUsers source, Identifier identifier, Object... args) {
            super(source, identifier, args);
        }
    }

    /**
     * Uses the {@link org.apache.isis.applib.services.queryresultscache.QueryResultsCache} in order to support
     * multiple lookups from <code>org.isisaddons.module.security.app.user.UserPermissionViewModel</code>.
     *
     * <p>
     *     If the user does not exist, it will be automatically created.
     * </p>
     */
    @ActionInteraction(FindOrCreateUserByUsernameEvent.class)
    @MemberOrder(sequence = "10.2")
    @ActionSemantics(Of.IDEMPOTENT)
    public ApplicationUser findOrCreateUserByUsername(
            final @ParameterLayout(named="Username") @MaxLength(ApplicationUser.MAX_LENGTH_USERNAME) String username) {
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
            final @ParameterLayout(named="Username") String username) {
        return uniqueMatch(new QueryDefault<>(
                ApplicationUser.class,
                "findByUsername", "username", username));
    }

    /**
     * Uses the {@link org.apache.isis.applib.services.queryresultscache.QueryResultsCache} in order to support
     * multiple lookups from <code>org.isisaddons.module.security.app.user.UserPermissionViewModel</code>.
     */
    @Programmatic
    public ApplicationUser findUserByEmail(
        final @ParameterLayout(named="Email") String emailAddress) {
        return uniqueMatch(new QueryDefault<>(
            ApplicationUser.class,
            "findByEmailAddress", "emailAddress", emailAddress));
    }
    //endregion

    //region > findUsersByName

    public static class FindUsersByNameEvent extends ActionInteractionEvent<ApplicationUsers> {
        public FindUsersByNameEvent(ApplicationUsers source, Identifier identifier, Object... args) {
            super(source, identifier, args);
        }
    }

    @ActionInteraction(FindUsersByNameEvent.class)
    @MemberOrder(sequence = "10.3")
    @ActionSemantics(Of.SAFE)
    public List<ApplicationUser> findUsersByName(
            final @ParameterLayout(named="Name") String name) {
        final String nameRegex = "(?i).*" + name + ".*";
        return allMatches(new QueryDefault<>(
                ApplicationUser.class,
                "findByName", "nameRegex", nameRegex));
    }
    //endregion

    //region > newUser (no password)

    public static class NewDelegateUserEvent extends ActionInteractionEvent<ApplicationUsers> {
        public NewDelegateUserEvent(ApplicationUsers source, Identifier identifier, Object... args) {
            super(source, identifier, args);
        }
    }

    @ActionInteraction(NewDelegateUserEvent.class)
    @MemberOrder(sequence = "10.4")
    @ActionSemantics(Of.NON_IDEMPOTENT)
    @NotContributed
    public ApplicationUser newDelegateUser(
            final @ParameterLayout(named="Name") @MaxLength(ApplicationUser.MAX_LENGTH_USERNAME) String username,
            final @ParameterLayout(named="Initial role") @Optional ApplicationRole initialRole,
            final @ParameterLayout(named="Enabled?") @Optional Boolean enabled) {
        ApplicationUser user = applicationUserFactory.newApplicationUser();
        user.setUsername(username);
        user.setStatus(ApplicationUserStatus.parse(enabled));
        user.setAccountType(AccountType.DELEGATED);
        if(initialRole != null) {
            user.addRole(initialRole);
        }
        persistIfNotAlready(user);
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

    //region > newLocalUser (action)

    public static class NewLocalUserEvent extends ActionInteractionEvent<ApplicationUsers> {
        public NewLocalUserEvent(ApplicationUsers source, Identifier identifier, Object... args) {
            super(source, identifier, args);
        }
    }

    @ActionInteraction(NewLocalUserEvent.class)
    @MemberOrder(sequence = "10.4")
    @ActionSemantics(Of.IDEMPOTENT)
    @NotContributed
    public ApplicationUser newLocalUser(
            final @ParameterLayout(named="Name") @MaxLength(ApplicationUser.MAX_LENGTH_USERNAME) String username,
            final @ParameterLayout(named="Password") @Optional Password password,
            final @ParameterLayout(named="Repeat password") @Optional Password passwordRepeat,
            final @ParameterLayout(named="Initial role") @Optional ApplicationRole initialRole,
            final @ParameterLayout(named="Enabled?") @Optional Boolean enabled) {
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
        persistIfNotAlready(user);
        return user;
    }
    public String validateNewLocalUser(
            final String username,
            final Password password,
            final Password passwordRepeat,
            final ApplicationRole initialRole,
            final Boolean enabled) {
        ApplicationUser user = applicationUserFactory.newApplicationUser();
        return user.validateResetPassword(password, passwordRepeat);
    }

    public ApplicationRole default3NewLocalUser() {
        return applicationRoles.findRoleByName(IsisModuleSecurityRegularUserRoleAndPermissions.ROLE_NAME);
    }
    //endregion

    //region > allUsers

    public static class AllUsersEvent extends ActionInteractionEvent<ApplicationUsers> {
        public AllUsersEvent(ApplicationUsers source, Identifier identifier, Object... args) {
            super(source, identifier, args);
        }
    }

    @ActionInteraction(AllUsersEvent.class)
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

    //region > helpers: isPasswordsFeatureEnabled, isPasswordsFeatureDisabled

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

    /**
     * Will only be injected to if the programmer has supplied an implementation.  Otherwise
     * this class will install a default implementation in {@link #init()}.
     */
    @Inject
    ApplicationUserFactory applicationUserFactory;

    //endregion

}
