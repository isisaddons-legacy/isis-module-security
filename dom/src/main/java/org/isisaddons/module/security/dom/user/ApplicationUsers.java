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

import javax.inject.Inject;

import org.apache.isis.applib.Identifier;
import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.value.Password;

import org.isisaddons.module.security.SecurityModule;
import org.isisaddons.module.security.dom.role.ApplicationRole;
import org.isisaddons.module.security.dom.role.ApplicationRoleRepository;
import org.isisaddons.module.security.seed.scripts.IsisModuleSecurityRegularUserRoleAndPermissions;
import org.isisaddons.module.security.shiro.IsisModuleSecurityRealm;
import org.isisaddons.module.security.shiro.ShiroUtils;

/**
 * @deprecated - use {@link ApplicationUserRepository} or {@link ApplicationUserMenu} instead.
 */
@Deprecated
public class ApplicationUsers {

    //region > domain event classes

    public static abstract class PropertyDomainEvent<T> extends SecurityModule.PropertyDomainEvent<ApplicationUsers, T> {
        public PropertyDomainEvent(final ApplicationUsers source, final Identifier identifier) {
            super(source, identifier);
        }

        public PropertyDomainEvent(final ApplicationUsers source, final Identifier identifier, final T oldValue, final T newValue) {
            super(source, identifier, oldValue, newValue);
        }
    }

    public static abstract class CollectionDomainEvent<T> extends SecurityModule.CollectionDomainEvent<ApplicationUsers, T> {
        public CollectionDomainEvent(final ApplicationUsers source, final Identifier identifier, final Of of) {
            super(source, identifier, of);
        }

        public CollectionDomainEvent(final ApplicationUsers source, final Identifier identifier, final Of of, final T value) {
            super(source, identifier, of, value);
        }
    }

    public static abstract class ActionDomainEvent extends SecurityModule.ActionDomainEvent<ApplicationUsers> {
        public ActionDomainEvent(final ApplicationUsers source, final Identifier identifier) {
            super(source, identifier);
        }

        public ActionDomainEvent(final ApplicationUsers source, final Identifier identifier, final Object... arguments) {
            super(source, identifier, arguments);
        }

        public ActionDomainEvent(final ApplicationUsers source, final Identifier identifier, final List<Object> arguments) {
            super(source, identifier, arguments);
        }
    }
    //endregion

    //region > identification
    public String iconName() {
        return "applicationUser";
    }
    //endregion

    //region > findOrCreateUserByUsername (programmatic)

    /**
     * @deprecated - use {@link ApplicationUserRepository#findOrCreateUserByUsername(String)} instead.
     */
    @Deprecated
    @Programmatic
    public ApplicationUser findOrCreateUserByUsername(final String username) {
        return applicationUserRepository.findOrCreateUserByUsername(username);
    }

    //endregion

    //region > findUserByName

    public static class FindUserByUserNameDomainEvent extends ActionDomainEvent {
        public FindUserByUserNameDomainEvent(final ApplicationUsers source, final Identifier identifier, final Object... args) {
            super(source, identifier, args);
        }
    }

    @Action(
            domainEvent = FindUserByUserNameDomainEvent.class,
            semantics = SemanticsOf.IDEMPOTENT,
            hidden = Where.EVERYWHERE
    )
    public ApplicationUser findUserByUsername(
            @Parameter(maxLength = ApplicationUser.MAX_LENGTH_USERNAME)
            @ParameterLayout(named = "Username")
            final String username) {
        return applicationUserRepository.findByUsername(username);
    }

    //endregion

    //region > findUserByEmail (programmatic)

    @Programmatic
    public ApplicationUser findUserByEmail(
        final @ParameterLayout(named="Email") String emailAddress) {
        return applicationUserRepository.findByEmailAddress(emailAddress);
    }
    //endregion

    //region > findUsersByName

    public static class FindUsersByNameDomainEvent extends ActionDomainEvent {
        public FindUsersByNameDomainEvent(final ApplicationUsers source, final Identifier identifier, final Object... args) {
            super(source, identifier, args);
        }
    }

    @Action(
            domainEvent = FindUsersByNameDomainEvent.class,
            semantics = SemanticsOf.SAFE,
            hidden = Where.EVERYWHERE
    )
    public List<ApplicationUser> findUsersByName(
            final @ParameterLayout(named="Name") String name) {
        final String nameRegex = "(?i).*" + name + ".*";
        return applicationUserRepository.find(name);
    }
    //endregion

    //region > newDelegateUser (action)

    public static class NewDelegateUserDomainEvent extends ActionDomainEvent {
        public NewDelegateUserDomainEvent(final ApplicationUsers source, final Identifier identifier, final Object... args) {
            super(source, identifier, args);
        }
    }

    @Action(
            domainEvent = NewDelegateUserDomainEvent.class,
            semantics = SemanticsOf.NON_IDEMPOTENT
    )
    @ActionLayout(
            cssClassFa = "fa-plus"
    )
    @MemberOrder(sequence = "100.10.3")
    public ApplicationUser newDelegateUser(
            @Parameter(maxLength = ApplicationUser.MAX_LENGTH_USERNAME)
            @ParameterLayout(named="Name")
            final String username,
            @Parameter(optionality = Optionality.OPTIONAL)
            @ParameterLayout(named="Initial role")
            final ApplicationRole initialRole,
            @Parameter(optionality = Optionality.OPTIONAL)
            @ParameterLayout(named="Enabled?")
            final Boolean enabled) {
        return applicationUserRepository.newDelegateUser(username, initialRole, enabled);
    }

    public boolean hideNewDelegateUser(
            final String username,
            final ApplicationRole initialRole,
            final Boolean enabled) {
        return hasNoDelegateAuthenticationRealm();
    }

    public ApplicationRole default1NewDelegateUser() {
        return applicationRoleRepository.findByNameCached(IsisModuleSecurityRegularUserRoleAndPermissions.ROLE_NAME);
    }

    //endregion

    //region > newLocalUser (action)

    public static class NewLocalUserDomainEvent extends ActionDomainEvent {
        public NewLocalUserDomainEvent(final ApplicationUsers source, final Identifier identifier, final Object... args) {
            super(source, identifier, args);
        }
    }

    @Action(
            domainEvent = NewLocalUserDomainEvent.class,
            semantics = SemanticsOf.IDEMPOTENT
    )
    @ActionLayout(
            cssClassFa = "fa-plus"
    )
    @MemberOrder(sequence = "100.10.4")
    public ApplicationUser newLocalUser(
            @Parameter(maxLength = ApplicationUser.MAX_LENGTH_USERNAME)
            @ParameterLayout(named="Name")
            final String username,
            @Parameter(optionality = Optionality.OPTIONAL)
            @ParameterLayout(named="Password")
            final Password password,
            @Parameter(optionality = Optionality.OPTIONAL)
            @ParameterLayout(named="Repeat password")
            final Password passwordRepeat,
            @Parameter(optionality = Optionality.OPTIONAL)
            @ParameterLayout(named="Initial role")
            final ApplicationRole initialRole,
            @Parameter(optionality = Optionality.OPTIONAL)
            @ParameterLayout(named="Enabled?")
            final Boolean enabled,
            @Parameter(optionality = Optionality.OPTIONAL)
            @ParameterLayout(named="Email Address")
            final String emailAddress) {
        return applicationUserRepository.newLocalUser(username, password, passwordRepeat, initialRole, enabled, emailAddress);
    }

    public String validateNewLocalUser(
            final String username,
            final Password password,
            final Password passwordRepeat,
            final ApplicationRole initialRole,
            final Boolean enabled,
            final String emailAddress) {
        return applicationUserRepository.validateNewLocalUser(username, password, passwordRepeat, initialRole, enabled, emailAddress);
    }

    public ApplicationRole default3NewLocalUser() {
        return applicationRoleRepository.findByNameCached(IsisModuleSecurityRegularUserRoleAndPermissions.ROLE_NAME);
    }
    //endregion

    //region > allUsers

    public static class AllUsersDomainEvent extends ActionDomainEvent {
        public AllUsersDomainEvent(final ApplicationUsers source, final Identifier identifier, final Object... args) {
            super(source, identifier, args);
        }
    }

    @Action(
            domainEvent = AllUsersDomainEvent.class,
            semantics = SemanticsOf.SAFE
    )
    @ActionLayout(
            cssClassFa = "fa-list"
    )
    @MemberOrder(sequence = "100.10.5")
    public List<ApplicationUser> allUsers() {
        return applicationUserRepository.allUsers();
    }

    //endregion

    //region > helpers: hasNoDelegateAuthenticationRealm

    private boolean hasNoDelegateAuthenticationRealm() {
        final IsisModuleSecurityRealm realm = ShiroUtils.getIsisModuleSecurityRealm();
        return realm == null || !realm.hasDelegateAuthenticationRealm();
    }

    //endregion

    //region  > injected
    @Inject
    ApplicationRoleRepository applicationRoleRepository;

    @Inject
    ApplicationUserRepository applicationUserRepository;
    //endregion

}
