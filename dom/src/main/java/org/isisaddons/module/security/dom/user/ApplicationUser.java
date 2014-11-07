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
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.VersionStrategy;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import org.isisaddons.module.security.dom.password.PasswordEncryptionService;
import org.isisaddons.module.security.dom.permission.ApplicationPermission;
import org.isisaddons.module.security.dom.permission.ApplicationPermissionValueSet;
import org.isisaddons.module.security.dom.permission.ApplicationPermissions;
import org.isisaddons.module.security.dom.permission.PermissionsEvaluationService;
import org.isisaddons.module.security.dom.role.ApplicationRole;
import org.isisaddons.module.security.dom.role.ApplicationRoles;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.isisaddons.module.security.seed.scripts.IsisModuleSecurityAdminRoleAndPermissions;
import org.isisaddons.module.security.seed.scripts.IsisModuleSecurityAdminUser;
import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.*;
import org.apache.isis.applib.security.RoleMemento;
import org.apache.isis.applib.security.UserMemento;
import org.apache.isis.applib.util.ObjectContracts;
import org.apache.isis.applib.value.Password;

@javax.jdo.annotations.PersistenceCapable(
        identityType = IdentityType.DATASTORE, table = "IsisSecurityApplicationUser")
@javax.jdo.annotations.Inheritance(
        strategy = InheritanceStrategy.NEW_TABLE)
@javax.jdo.annotations.DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE, column = "id")
@javax.jdo.annotations.Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@javax.jdo.annotations.Uniques({
        @javax.jdo.annotations.Unique(
                name = "IsisSecurityApplicationUser_username_UNQ", members = { "username" })
})
@javax.jdo.annotations.Queries( {
        @javax.jdo.annotations.Query(
                name = "findByUsername", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.isisaddons.module.security.dom.user.ApplicationUser "
                        + "WHERE username == :username"),
        @javax.jdo.annotations.Query(
                name = "findByName", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.isisaddons.module.security.dom.user.ApplicationUser "
                        + "WHERE username.matches(:nameRegex)"
                        + "   || familyName.matches(:nameRegex)"
                        + "   || givenName.matches(:nameRegex)"
                        + "   || knownAs.matches(:nameRegex)"
        )
})
@AutoComplete(repository=ApplicationUsers.class, action="autoComplete")
@ObjectType("IsisSecurityApplicationUser")
@Bookmarkable
@MemberGroupLayout(columnSpans = {4,4,4,12},
    left = {"Id", "Name"},
    middle= {"Contact Details"},
    right= {"Status", "Tenancy"}
)
public class ApplicationUser implements Comparable<ApplicationUser> {

    //region > constants

    public static final int MAX_LENGTH_USERNAME = 30;
    public static final int MAX_LENGTH_FAMILY_NAME = 50;
    public static final int MAX_LENGTH_GIVEN_NAME = 50;
    public static final int MAX_LENGTH_KNOWN_AS = 20;
    public static final int MAX_LENGTH_EMAIL_ADDRESS = 50;
    public static final int MAX_LENGTH_PHONE_NUMBER = 25;
    //endregion

    //region > identification

    /**
     * having a title() method (rather than using @Title annotation) is necessary as a workaround to be able to use
     * wrapperFactory#unwrap(...) method, which is otherwise broken in Isis 1.6.0
     */
    public String title() {
        return getName();
    }

    //endregion

    //region > name (derived property)
    @javax.jdo.annotations.NotPersistent
    @Hidden(where=Where.OBJECT_FORMS)
    @Disabled
    @MemberOrder(name="Id", sequence = "1")
    public String getName() {
        final StringBuilder buf = new StringBuilder();
        if(getFamilyName() != null) {
            if(getKnownAs() != null) {
                buf.append(getKnownAs());
            } else {
                buf.append(getGivenName());
            }
            buf.append(" ")
                    .append(getFamilyName())
                    .append(" (").append(getUsername()).append(")");
        } else {
            buf.append(getUsername());
        }
        return buf.toString();
    }
    //endregion

    //region > username (property)
    private String username;

    @javax.jdo.annotations.Column(allowsNull="false", length = MAX_LENGTH_USERNAME)
    @Hidden(where=Where.PARENTED_TABLES)
    @Disabled
    @MemberOrder(name="Id", sequence = "1")
    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    @MemberOrder(name="username", sequence = "1")
    @ActionSemantics(ActionSemantics.Of.IDEMPOTENT)
    public ApplicationUser updateUsername(
            final @Named("Username") @MaxLength(MAX_LENGTH_USERNAME) String username) {
        setUsername(username);
        return this;
    }

    public String default0UpdateUsername() {
        return getUsername();
    }
    //endregion

    //region > familyName (property)
    private String familyName;

    @javax.jdo.annotations.Column(allowsNull="true", length = MAX_LENGTH_FAMILY_NAME)
    @Hidden(where=Where.ALL_TABLES)
    @Disabled
    @MemberOrder(name="Name",sequence = "2.1")
    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(final String familyName) {
        this.familyName = familyName;
    }
    //endregion

    //region > givenName (property)
    private String givenName;

    @javax.jdo.annotations.Column(allowsNull="true", length = MAX_LENGTH_GIVEN_NAME)
    @Hidden(where=Where.ALL_TABLES)
    @Disabled
    @MemberOrder(name="Name", sequence = "2.2")
    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(final String givenName) {
        this.givenName = givenName;
    }
    //endregion

    //region > knownAs (property)
    private String knownAs;

    @javax.jdo.annotations.Column(allowsNull="true", length = MAX_LENGTH_KNOWN_AS)
    @Hidden(where=Where.ALL_TABLES)
    @Disabled
    @MemberOrder(name="Name",sequence = "2.3")
    public String getKnownAs() {
        return knownAs;
    }

    public void setKnownAs(final String knownAs) {
        this.knownAs = knownAs;
    }
    //endregion

    //region > updateName (action)

    @MemberOrder(name="knownAs", sequence = "1")
    @ActionSemantics(ActionSemantics.Of.IDEMPOTENT)
    public ApplicationUser updateName(
            final @Named("Family Name") @Optional @MaxLength(MAX_LENGTH_FAMILY_NAME) String familyName,
            final @Named("Given Name") @Optional @MaxLength(MAX_LENGTH_GIVEN_NAME) String givenName,
            final @Named("Known As") @Optional @MaxLength(MAX_LENGTH_KNOWN_AS) String knownAs
    ) {
        setFamilyName(familyName);
        setGivenName(givenName);
        setKnownAs(knownAs);
        return this;
    }

    public String default0UpdateName() {
        return getFamilyName();
    }

    public String default1UpdateName() {
        return getGivenName();
    }

    public String default2UpdateName() {
        return getKnownAs();
    }

    public String disableUpdateName(final String familyName, final String givenName, final String knownAs) {
        return isForSelfOrRunAsAdministrator()? null: "Can only update your own user record.";
    }

    public String validateUpdateName(final String familyName, final String givenName, final String knownAs) {
        if(familyName != null && givenName == null) {
            return "Must provide given name if family name has been provided.";
        }
        if(familyName == null && (givenName != null | knownAs != null)) {
            return "Must provide family name if given name or 'known as' name has been provided.";
        }
        return null;
    }
    //endregion

    //region > emailAddress (property)
    private String emailAddress;

    @javax.jdo.annotations.Column(allowsNull="true", length = MAX_LENGTH_EMAIL_ADDRESS)
    @Disabled
    @MemberOrder(name="Contact Details", sequence = "3.1")
    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(final String emailAddress) {
        this.emailAddress = emailAddress;
    }

    @MemberOrder(name="emailAddress", sequence = "1")
    @ActionSemantics(ActionSemantics.Of.IDEMPOTENT)
    public ApplicationUser updateEmailAddress(
            final @Named("Email") @MaxLength(MAX_LENGTH_EMAIL_ADDRESS) String emailAddress) {
        setEmailAddress(emailAddress);
        return this;
    }

    public String default0UpdateEmailAddress() {
        return getEmailAddress();
    }

    public String disableUpdateEmailAddress(final String emailAddress) {
        return isForSelfOrRunAsAdministrator()? null: "Can only update your own user record.";
    }
    //endregion

    //region > phoneNumber (property)
    private String phoneNumber;

    @javax.jdo.annotations.Column(allowsNull="true", length = MAX_LENGTH_PHONE_NUMBER)
    @Disabled
    @MemberOrder(name="Contact Details", sequence = "3.2")
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(final String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @MemberOrder(name="phoneNumber", sequence = "1")
    @ActionSemantics(ActionSemantics.Of.IDEMPOTENT)
    public ApplicationUser updatePhoneNumber(
            final @Named("Phone") @Optional @MaxLength(MAX_LENGTH_PHONE_NUMBER) String phoneNumber) {
        setPhoneNumber(phoneNumber);
        return this;
    }

    public String disableUpdatePhoneNumber(final String faxNumber) {
        return isForSelfOrRunAsAdministrator()? null: "Can only update your own user record.";
    }
    public String default0UpdatePhoneNumber() {
        return getPhoneNumber();
    }

    //endregion

    //region > faxNumber (property)
    private String faxNumber;

    @javax.jdo.annotations.Column(allowsNull="true", length = MAX_LENGTH_PHONE_NUMBER)
    @Hidden(where=Where.PARENTED_TABLES)
    @Disabled
    @MemberOrder(name="Contact Details", sequence = "3.3")
    public String getFaxNumber() {
        return faxNumber;
    }

    public void setFaxNumber(final String faxNumber) {
        this.faxNumber = faxNumber;
    }

    @MemberOrder(name="faxNumber", sequence = "1")
    @ActionSemantics(ActionSemantics.Of.IDEMPOTENT)
    public ApplicationUser updateFaxNumber(
            final @Named("Fax") @Optional @MaxLength(MAX_LENGTH_PHONE_NUMBER) String faxNumber) {
        setFaxNumber(faxNumber);
        return this;
    }

    public String default0UpdateFaxNumber() {
        return getFaxNumber();
    }

    public String disableUpdateFaxNumber(final String faxNumber) {
        return isForSelfOrRunAsAdministrator()? null: "Can only update your own user record.";
    }

    //endregion

    //region > tenancy (property)
    private ApplicationTenancy tenancy;

    @javax.jdo.annotations.Column(name = "tenancyId", allowsNull="true")
    @MemberOrder(name="Tenancy", sequence = "3.4")
    @Disabled
    public ApplicationTenancy getTenancy() {
        return tenancy;
    }

    public void setTenancy(final ApplicationTenancy tenancy) {
        this.tenancy = tenancy;
    }

    @MemberOrder(name="tenancy", sequence = "1")
    @ActionSemantics(ActionSemantics.Of.IDEMPOTENT)
    public ApplicationUser updateTenancy(final @Optional ApplicationTenancy tenancy) {
        setTenancy(tenancy);
        return this;
    }

    public ApplicationTenancy default0UpdateTenancy() {
        return getTenancy();
    }
    //endregion

    //region > accountType (property), updateAccountType

    private AccountType accountType;

    @javax.jdo.annotations.Column(allowsNull="false")
    @Disabled
    @MemberOrder(name="Status", sequence = "3")
    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    @ActionSemantics(ActionSemantics.Of.IDEMPOTENT)
    @MemberOrder(name = "Account Type", sequence = "1")
    public ApplicationUser updateAccountType(
            final AccountType accountType) {
        setAccountType(accountType);
        return this;
    }
    public String disableUpdateAccountType(final AccountType accountType) {
        return isAdminUser()
                ? "Cannot change account type for admin user"
                : null;
    }
    public AccountType default0UpdateAccountType() {
        return getAccountType();
    }

    private boolean isDelegateAccountOrPasswordEncryptionNotAvailable() {
        return !isLocalAccountWithPasswordEncryptionAvailable();
    }

    private boolean isLocalAccountWithPasswordEncryptionAvailable() {
        return getAccountType() == AccountType.LOCAL && passwordEncryptionService != null;
    }

    //endregion

    //region > status (property), visible (action), usable (action)

    private ApplicationUserStatus status;

    @javax.jdo.annotations.Column(allowsNull="false")
    @Disabled
    @MemberOrder(name="Status", sequence = "4")
    public ApplicationUserStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationUserStatus status) {
        this.status = status;
    }

    @ActionSemantics(ActionSemantics.Of.IDEMPOTENT)
    @MemberOrder(name = "Status", sequence = "1")
    @Named("Enable") // symmetry with lock (disable)
    public ApplicationUser unlock() {
        setStatus(ApplicationUserStatus.ENABLED);
        return this;
    }
    public String disableUnlock() {
        return getStatus() == ApplicationUserStatus.ENABLED ? "Status is already set to ENABLE": null;
    }

    @ActionSemantics(ActionSemantics.Of.IDEMPOTENT)
    @MemberOrder(name = "Status", sequence = "2")
    @Named("Disable") // method cannot be called 'disable' as that would clash with Isis' naming conventions
    public ApplicationUser lock() {
        setStatus(ApplicationUserStatus.DISABLED);
        return this;
    }
    public String disableLock() {
        if(isAdminUser()) {
            return "Cannot disable the '" + IsisModuleSecurityAdminUser.USER_NAME + "' user.";
        }
        return getStatus() == ApplicationUserStatus.DISABLED ? "Status is already set to DISABLE": null;
    }

    //endregion

    //region > encryptedPassword (hidden property)
    private String encryptedPassword;

    @javax.jdo.annotations.Column(allowsNull="true")
    @Hidden
    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    public void setEncryptedPassword(final String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }

    public boolean hideEncryptedPassword() {
        return isDelegateAccountOrPasswordEncryptionNotAvailable();
    }
    //endregion


    //region > hasPassword (derived property)


    @Disabled
    @MemberOrder(name="Status", sequence = "4")
    public boolean isHasPassword() {
        return !Strings.isNullOrEmpty(getEncryptedPassword());
    }

    public boolean hideHasPassword() {
        return isDelegateAccountOrPasswordEncryptionNotAvailable();
    }

    //endregion

    //region > updatePassword (action)

    @MemberOrder(name="hasPassword", sequence = "10")
    @ActionSemantics(ActionSemantics.Of.IDEMPOTENT)
    public ApplicationUser updatePassword(
            final @Named("Existing password") Password existingPassword,
            final @Named("New password") Password newPassword,
            final @Named("Re-enter password") Password newPasswordRepeat) {
        updatePassword(newPassword.getPassword());
        return this;
    }

    public boolean hideUpdatePassword(
            final Password existingPassword,
            final Password newPassword,
            final Password newPasswordRepeat) {
        return isDelegateAccountOrPasswordEncryptionNotAvailable();
    }

    public String disableUpdatePassword(
            final Password existingPassword,
            final Password newPassword,
            final Password newPasswordConfirm) {

        if(!isForSelfOrRunAsAdministrator()) {
            return "Can only update password for your own user account.";
        }
        if (!isHasPassword()) {
            return "Password must be reset by administrator.";
        }
        return null;
    }


    public String validateUpdatePassword(
            final Password existingPassword,
            final Password newPassword,
            final Password newPasswordRepeat) {
        if(isDelegateAccountOrPasswordEncryptionNotAvailable()) {
            return null;
        }

        if(getEncryptedPassword() != null) {
            if (!passwordEncryptionService.matches(existingPassword.getPassword(), getEncryptedPassword())) {
                return "Existing password is incorrect";
            }
        }

        if (!match(newPassword, newPasswordRepeat)) {
            return "Passwords do not match";
        }

        return null;
    }

    @Programmatic
    public void updatePassword(String password) {
        // in case called programmatically
        if(isDelegateAccountOrPasswordEncryptionNotAvailable()) {
            return;
        }
        final String encryptedPassword = passwordEncryptionService.encrypt(password);
        setEncryptedPassword(encryptedPassword);
    }

    //endregion

    //region > resetPassword (action)

    @MemberOrder(name="hasPassword", sequence = "20")
    @ActionSemantics(ActionSemantics.Of.IDEMPOTENT)
    public ApplicationUser resetPassword(
            final @Named("New password") Password newPassword,
            final @Named("Repeat password") Password newPasswordRepeat) {
        updatePassword(newPassword.getPassword());
        return this;
    }

    public boolean hideResetPassword(
            final Password newPassword,
            final Password newPasswordRepeat) {
        return isDelegateAccountOrPasswordEncryptionNotAvailable();
    }

    public String validateResetPassword(
            final Password newPassword,
            final Password newPasswordRepeat) {
        if(isDelegateAccountOrPasswordEncryptionNotAvailable()) {
            return null;
        }
        if (!match(newPassword, newPasswordRepeat)) {
            return "Passwords do not match";
        }

        return null;
    }

    boolean match(Password newPassword, Password newPasswordRepeat) {
        if (newPassword == null && newPasswordRepeat == null) {
            return true;
        }
        if (newPassword == null || newPasswordRepeat == null) {
            return false;
        }
        return Objects.equals(newPassword.getPassword(), newPasswordRepeat.getPassword());
    }

    //endregion

    //region > roles (collection)
    @javax.jdo.annotations.Persistent(table="IsisSecurityApplicationUserRoles")
    @javax.jdo.annotations.Join(column="userId")
    @javax.jdo.annotations.Element(column="roleId")
    private SortedSet<ApplicationRole> roles = new TreeSet<>();

    @MemberOrder(sequence = "20")
    @Render(Render.Type.EAGERLY)
    @Disabled
    public SortedSet<ApplicationRole> getRoles() {
        return roles;
    }

    public void setRoles(final SortedSet<ApplicationRole> roles) {
        this.roles = roles;
    }

    // necessary only because otherwise call to getRoles() through wrapped object
    // (in integration tests) is ambiguous.
    public void addToRoles(final ApplicationRole applicationRole) {
        getRoles().add(applicationRole);
    }
    // necessary only because otherwise call to getRoles() through wrapped object
    // (in integration tests) is ambiguous.
    public void removeFromRoles(final ApplicationRole applicationRole) {
        getRoles().remove(applicationRole);
    }
    //endregion

    //region > addRole (action)
    @MemberOrder(name="roles", sequence = "1")
    @ActionSemantics(ActionSemantics.Of.IDEMPOTENT)
    @Named("Add")
    @CssClassFa("fa fa-plus-square")
    public ApplicationUser addRole(final ApplicationRole role) {
        addToRoles(role);
        return this;
    }

    public SortedSet<ApplicationRole> choices0AddRole() {
        final List<ApplicationRole> allRoles = applicationRoles.allRoles();
        final SortedSet<ApplicationRole> applicationRoles = Sets.newTreeSet(allRoles);
        applicationRoles.removeAll(getRoles());
        return applicationRoles;
    }

    public String disableAddRole(final ApplicationRole role) {
        return choices0AddRole().isEmpty()? "All roles added": null;
    }
    //endregion

    //region > removeRole (action)
    @MemberOrder(name="roles", sequence = "2")
    @ActionSemantics(ActionSemantics.Of.IDEMPOTENT)
    @Named("Remove")
    @CssClassFa("fa fa-minus-square")
    public ApplicationUser removeRole(final ApplicationRole role) {
        removeFromRoles(role);
        return this;
    }

    public String disableRemoveRole(final ApplicationRole role) {
        return getRoles().isEmpty()? "No roles to remove": null;
    }

    public SortedSet<ApplicationRole> choices0RemoveRole() {
        return getRoles();
    }

    public String validateRemoveRole(
            final ApplicationRole applicationRole) {
        if(isAdminUser() && applicationRole.isAdminRole()) {
            return "Cannot remove admin user from the admin role.";
        }
        return null;
    }

    //endregion

    //region > delete (action)
    @ActionSemantics(ActionSemantics.Of.NON_IDEMPOTENT)
    @MemberOrder(sequence = "1")
    @CssClassFa("fa fa-trash")
    @CssClass("btn btn-danger")
    public List<ApplicationUser> delete(
            final @Named("Are you sure?") @Optional Boolean areYouSure) {
        container.removeIfNotAlready(this);
        container.flush();
        return applicationUsers.allUsers();
    }

    public String validateDelete(final Boolean areYouSure) {
        return not(areYouSure) ? "Please confirm this action": null;
    }
    public String disableDelete(final Boolean areYouSure) {
        return isAdminUser()? "Cannot delete the admin user": null;
    }

    static boolean not(Boolean areYouSure) {
        return areYouSure == null || !areYouSure;
    }
    //endregion

    //region > PermissionSet (programmatic)

    // short-term caching
    private transient ApplicationPermissionValueSet cachedPermissionSet;
    @Programmatic
    public ApplicationPermissionValueSet getPermissionSet() {
        if(cachedPermissionSet != null) {
            return cachedPermissionSet;
        }
        final List<ApplicationPermission> permissions = applicationPermissions.findByUser(this);
        return cachedPermissionSet =
                new ApplicationPermissionValueSet(
                        Iterables.transform(permissions, ApplicationPermission.Functions.AS_VALUE),
                        permissionsEvaluationService);
    }
    //endregion

    //region > isAdminUser (programmatic)
    @Programmatic
    public boolean isAdminUser() {
        return IsisModuleSecurityAdminUser.USER_NAME.equals(getName());
    }

    //endregion

    //region > helpers
    boolean isForSelfOrRunAsAdministrator() {
        return isForSelf() || isRunAsAdministrator();
    }

    boolean isForSelf() {
        final String currentUserName = container.getUser().getName();
        return Objects.equals(getName(), currentUserName);
    }
    boolean isRunAsAdministrator() {
        final UserMemento currentUser = container.getUser();
        final List<RoleMemento> roles = currentUser.getRoles();
        for (RoleMemento role : roles) {
            final String roleName = role.getName();
            // format is realmName:roleName.
            // since we don't know what the realm's name is (depends on its configuration in shiro.ini),
            // simply check that the last part matches the role name.
            if(roleName.endsWith(IsisModuleSecurityAdminRoleAndPermissions.ROLE_NAME)) {
                return true;
            }
        }
        return false;
    }
    //endregion

    //region > equals, hashCode, compareTo, toString
    private final static String propertyNames = "username";

    @Override
    public int compareTo(final ApplicationUser o) {
        return ObjectContracts.compare(this, o, propertyNames);
    }

    @Override
    public boolean equals(final Object obj) {
        return ObjectContracts.equals(this, obj, propertyNames);
    }

    @Override
    public int hashCode() {
        return ObjectContracts.hashCode(this, propertyNames);
    }

    @Override
    public String toString() {
        return ObjectContracts.toString(this, propertyNames);
    }

    //endregion

    //region  >  (injected)
    @javax.inject.Inject
    ApplicationRoles applicationRoles;
    @javax.inject.Inject
    ApplicationUsers applicationUsers;
    @javax.inject.Inject
    ApplicationPermissions applicationPermissions;
    @javax.inject.Inject
    PasswordEncryptionService passwordEncryptionService;
    @javax.inject.Inject
    DomainObjectContainer container;

    /**
     * Optional service, if configured then is used to evaluate permissions within
     * {@link org.isisaddons.module.security.dom.permission.ApplicationPermissionValueSet#evaluate(org.isisaddons.module.security.dom.feature.ApplicationFeatureId, org.isisaddons.module.security.dom.permission.ApplicationPermissionMode)},
     * else will fallback to a {@link org.isisaddons.module.security.dom.permission.PermissionsEvaluationService#DEFAULT default}
     * implementation.
     */
    @javax.inject.Inject
    PermissionsEvaluationService permissionsEvaluationService;
    //endregion
}
