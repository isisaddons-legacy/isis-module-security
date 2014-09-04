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
package org.isisaddons.module.security.dom.role;

import java.util.Collection;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.InheritanceStrategy;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.isisaddons.module.security.dom.feature.ApplicationFeature;
import org.isisaddons.module.security.dom.feature.ApplicationFeatureType;
import org.isisaddons.module.security.dom.feature.ApplicationFeatures;
import org.isisaddons.module.security.dom.feature.ApplicationMemberType;
import org.isisaddons.module.security.dom.permission.ApplicationPermission;
import org.isisaddons.module.security.dom.permission.ApplicationPermissionMode;
import org.isisaddons.module.security.dom.permission.ApplicationPermissionRule;
import org.isisaddons.module.security.dom.permission.ApplicationPermissions;
import org.isisaddons.module.security.dom.user.ApplicationUser;
import org.isisaddons.module.security.dom.user.ApplicationUsers;
import org.isisaddons.module.security.seed.scripts.IsisModuleSecurityAdminRoleAndPermissions;
import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.*;
import org.apache.isis.applib.util.ObjectContracts;
import org.apache.isis.objectstore.jdo.applib.service.JdoColumnLength;

@javax.jdo.annotations.PersistenceCapable(
        identityType = IdentityType.DATASTORE, table = "IsisSecurityApplicationRole")
@javax.jdo.annotations.Inheritance(
        strategy = InheritanceStrategy.NEW_TABLE)
@javax.jdo.annotations.DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE, column = "id")
@javax.jdo.annotations.Uniques({
        @javax.jdo.annotations.Unique(
                name = "IsisSecurityApplicationRole_name_UNQ", members = { "name" })
})
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByName", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.isisaddons.module.security.dom.role.ApplicationRole "
                        + "WHERE name == :name"),
        @javax.jdo.annotations.Query(
                name = "findByNameContaining", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.isisaddons.module.security.dom.role.ApplicationRole "
                        + "WHERE name.matches(:nameRegex) >= 0")
})
@Bounded
@ObjectType("IsisSecurityApplicationRole")
@Bookmarkable
public class ApplicationRole implements Comparable<ApplicationRole> {

    //region > constants

    public static final int MAX_LENGTH_NAME = 50;
    public static final int TYPICAL_LENGTH_NAME = 30;
    public static final int TYPICAL_LENGTH_DESCRIPTION = 50;
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

    //region > name (property,)
    private String name;

    @javax.jdo.annotations.Column(allowsNull="false", length = MAX_LENGTH_NAME)
    @TypicalLength(TYPICAL_LENGTH_NAME)
    @Disabled
    @MemberOrder(sequence = "1")
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }
    @MemberOrder(name="name", sequence = "1")
    @ActionSemantics(ActionSemantics.Of.IDEMPOTENT)
    public ApplicationRole updateName(
            final @Named("Name") @TypicalLength(TYPICAL_LENGTH_NAME) @MaxLength(MAX_LENGTH_NAME) String name) {
        setName(name);
        return this;
    }

    public String default0UpdateName() {
        return getName();
    }

    //endregion

    //region > description (property)
    private String description;

    @javax.jdo.annotations.Column(allowsNull="true", length = JdoColumnLength.DESCRIPTION)
    @TypicalLength(TYPICAL_LENGTH_DESCRIPTION)
    @Disabled
    @MemberOrder(sequence = "2")
    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }
    @MemberOrder(name="description", sequence = "1")
    @ActionSemantics(ActionSemantics.Of.IDEMPOTENT)
    public ApplicationRole updateDescription(
            final @Named("Description") @Optional @TypicalLength(TYPICAL_LENGTH_DESCRIPTION) @MaxLength(JdoColumnLength.DESCRIPTION) String description) {
        setDescription(description);
        return this;
    }

    public String default0UpdateDescription() {
        return getDescription();
    }

    //endregion

    //region > permissions (derived collection)
    @MemberOrder(sequence = "10")
    @ActionSemantics(ActionSemantics.Of.SAFE)
    @Render(Render.Type.EAGERLY)
    @SortedBy(ApplicationPermission.DefaultComparator.class)
    public List<ApplicationPermission> getPermissions() {
        return applicationPermissions.findByRole(this);
    }
    //endregion

    //region > addPackage (action)

    /**
     * Adds a {@link org.isisaddons.module.security.dom.permission.ApplicationPermission permission} for this role to a
     * {@link org.isisaddons.module.security.dom.feature.ApplicationFeatureType#PACKAGE package}
     * {@link org.isisaddons.module.security.dom.feature.ApplicationFeature feature}.
     */
    @ActionSemantics(ActionSemantics.Of.NON_IDEMPOTENT)
    @MemberOrder(name = "Permissions", sequence = "1")
    public ApplicationRole addPackage(
            final @Named("Rule") ApplicationPermissionRule rule,
            final @Named("Mode") ApplicationPermissionMode mode,
            final @TypicalLength(ApplicationFeature.TYPICAL_LENGTH_PKG_FQN) @Named("Package") String packageFqn) {
        applicationPermissions.newPermission(this, rule, mode, ApplicationFeatureType.PACKAGE, packageFqn);
        return this;
    }

    public ApplicationPermissionRule default0AddPackage() {
        return ApplicationPermissionRule.ALLOW;
    }

    public ApplicationPermissionMode default1AddPackage() {
        return ApplicationPermissionMode.CHANGING;
    }

    public List<String> choices2AddPackage() {
        return Lists.newArrayList(Iterables.transform(applicationFeatures.allFeatures(ApplicationFeatureType.PACKAGE), ApplicationFeature.Functions.GET_FQN));
    }
    //endregion

    //region > addClass (action)
    /**
     * Adds a {@link org.isisaddons.module.security.dom.permission.ApplicationPermission permission} for this role to a
     * {@link org.isisaddons.module.security.dom.feature.ApplicationFeatureType#MEMBER member}
     * {@link org.isisaddons.module.security.dom.feature.ApplicationFeature feature}.
     */
    @ActionSemantics(ActionSemantics.Of.NON_IDEMPOTENT)
    @MemberOrder(name = "Permissions", sequence = "2")
    public ApplicationRole addClass(
            final @Named("Rule") ApplicationPermissionRule rule,
            final @Named("Mode") ApplicationPermissionMode mode,
            final @Named("Package") @TypicalLength(ApplicationFeature.TYPICAL_LENGTH_PKG_FQN) String packageFqn,
            final @Named("Class") @TypicalLength(ApplicationFeature.TYPICAL_LENGTH_CLS_NAME) String className) {
        applicationPermissions.newPermission(this, rule, mode, ApplicationFeatureType.CLASS, packageFqn + "." + className);
        return this;
    }

    public ApplicationPermissionRule default0AddClass() {
        return ApplicationPermissionRule.ALLOW;
    }

    public ApplicationPermissionMode default1AddClass() {
        return ApplicationPermissionMode.CHANGING;
    }

    /**
     * Package names that have classes in them.
     */
    public List<String> choices2AddClass() {
        return applicationFeatures.packageNamesContainingClasses(null);
    }

    /**
     * Class names for selected package.
     */
    public List<String> choices3AddClass(
            final ApplicationPermissionRule rule,
            final ApplicationPermissionMode mode,
            final String packageFqn) {
        return applicationFeatures.classNamesContainedIn(packageFqn, null);
    }

    //endregion

    //region > addAction (action)
    /**
     * Adds a {@link org.isisaddons.module.security.dom.permission.ApplicationPermission permission} for this role to a
     * {@link org.isisaddons.module.security.dom.feature.ApplicationMemberType#ACTION action}
     * {@link org.isisaddons.module.security.dom.feature.ApplicationFeatureType#MEMBER member}
     * {@link org.isisaddons.module.security.dom.feature.ApplicationFeature feature}.
     */
    @ActionSemantics(ActionSemantics.Of.NON_IDEMPOTENT)
    @MemberOrder(name = "Permissions", sequence = "3")
    public ApplicationRole addAction(
            final @Named("Rule") ApplicationPermissionRule rule,
            final @Named("Mode") ApplicationPermissionMode mode,
            final @Named("Package") @TypicalLength(ApplicationFeature.TYPICAL_LENGTH_PKG_FQN) String packageFqn,
            final @Named("Class") @TypicalLength(ApplicationFeature.TYPICAL_LENGTH_CLS_NAME) String className,
            final @Named("Action") @TypicalLength(ApplicationFeature.TYPICAL_LENGTH_MEMBER_NAME) String memberName) {
        applicationPermissions.newPermission(this, rule, mode, packageFqn, className, memberName);
        return this;
    }

    public ApplicationPermissionRule default0AddAction() {
        return ApplicationPermissionRule.ALLOW;
    }

    public ApplicationPermissionMode default1AddAction() {
        return ApplicationPermissionMode.CHANGING;
    }

    public List<String> choices2AddAction() {
        return applicationFeatures.packageNamesContainingClasses(ApplicationMemberType.ACTION);
    }

    public List<String> choices3AddAction(
            final ApplicationPermissionRule rule,
            final ApplicationPermissionMode mode,
            final String packageFqn) {
        return applicationFeatures.classNamesContainedIn(packageFqn, ApplicationMemberType.ACTION);
    }

    public List<String> choices4AddAction(
            final ApplicationPermissionRule rule,
            final ApplicationPermissionMode mode,
            final String packageFqn,
            final String className) {
        return applicationFeatures.memberNamesOf(packageFqn, className, ApplicationMemberType.ACTION);
    }

    //endregion

    //region > addProperty (action)
    /**
     * Adds a {@link org.isisaddons.module.security.dom.permission.ApplicationPermission permission} for this role to a
     * {@link org.isisaddons.module.security.dom.feature.ApplicationMemberType#PROPERTY property}
     * {@link org.isisaddons.module.security.dom.feature.ApplicationFeatureType#MEMBER member}
     * {@link org.isisaddons.module.security.dom.feature.ApplicationFeature feature}.
     */
    @ActionSemantics(ActionSemantics.Of.NON_IDEMPOTENT)
    @MemberOrder(name = "Permissions", sequence = "4")
    public ApplicationRole addProperty(
            final @Named("Rule") ApplicationPermissionRule rule,
            final @Named("Mode") ApplicationPermissionMode mode,
            final @Named("Package") @TypicalLength(ApplicationFeature.TYPICAL_LENGTH_PKG_FQN) String packageFqn,
            final @Named("Class") @TypicalLength(ApplicationFeature.TYPICAL_LENGTH_CLS_NAME) String className,
            final @Named("Property") @TypicalLength(ApplicationFeature.TYPICAL_LENGTH_MEMBER_NAME) String memberName) {
        applicationPermissions.newPermission(this, rule, mode, packageFqn, className, memberName);
        return this;
    }

    public ApplicationPermissionRule default0AddProperty() {
        return ApplicationPermissionRule.ALLOW;
    }

    public ApplicationPermissionMode default1AddProperty() {
        return ApplicationPermissionMode.CHANGING;
    }

    /**
     * Package names that have classes in them.
     */
    public List<String> choices2AddProperty() {
        return applicationFeatures.packageNamesContainingClasses(ApplicationMemberType.PROPERTY);
    }

    /**
     * Class names for selected package.
     */
    public List<String> choices3AddProperty(
            final ApplicationPermissionRule rule,
            final ApplicationPermissionMode mode,
            final String packageFqn) {
        return applicationFeatures.classNamesContainedIn(packageFqn, ApplicationMemberType.PROPERTY);
    }

    /**
     * Member names for selected class.
     */
    public List<String> choices4AddProperty(
            final ApplicationPermissionRule rule,
            final ApplicationPermissionMode mode,
            final String packageFqn,
            final String className) {
        return applicationFeatures.memberNamesOf(packageFqn, className, ApplicationMemberType.PROPERTY);
    }
    //endregion

    //region > addCollection (action)
    /**
     * Adds a {@link org.isisaddons.module.security.dom.permission.ApplicationPermission permission} for this role to a
     * {@link org.isisaddons.module.security.dom.feature.ApplicationMemberType#COLLECTION collection}
     * {@link org.isisaddons.module.security.dom.feature.ApplicationFeatureType#MEMBER member}
     * {@link org.isisaddons.module.security.dom.feature.ApplicationFeature feature}.
     */
    @ActionSemantics(ActionSemantics.Of.NON_IDEMPOTENT)
    @MemberOrder(name = "Permissions", sequence = "5")
    public ApplicationRole addCollection(
            final @Named("Rule") ApplicationPermissionRule rule,
            final @Named("Mode") ApplicationPermissionMode mode,
            final @Named("Package") @TypicalLength(ApplicationFeature.TYPICAL_LENGTH_PKG_FQN) String packageFqn,
            final @Named("Class") @TypicalLength(ApplicationFeature.TYPICAL_LENGTH_CLS_NAME) String className,
            final @Named("Collection") @TypicalLength(ApplicationFeature.TYPICAL_LENGTH_MEMBER_NAME) String memberName) {
        applicationPermissions.newPermission(this, rule, mode, packageFqn, className, memberName);
        return this;
    }

    public ApplicationPermissionRule default0AddCollection() {
        return ApplicationPermissionRule.ALLOW;
    }

    public ApplicationPermissionMode default1AddCollection() {
        return ApplicationPermissionMode.CHANGING;
    }

    public List<String> choices2AddCollection() {
        return applicationFeatures.packageNamesContainingClasses(ApplicationMemberType.COLLECTION);
    }

    public List<String> choices3AddCollection(
            final ApplicationPermissionRule rule,
            final ApplicationPermissionMode mode,
            final String packageFqn) {
        return applicationFeatures.classNamesContainedIn(packageFqn, ApplicationMemberType.COLLECTION);
    }

    public List<String> choices4AddCollection(
            final ApplicationPermissionRule rule,
            final ApplicationPermissionMode mode,
            final String packageFqn,
            final String className) {
        return applicationFeatures.memberNamesOf(packageFqn, className, ApplicationMemberType.COLLECTION);
    }

    //endregion

    //region > removePermission (action)
    @MemberOrder(name = "Permissions", sequence = "9")
    @ActionSemantics(ActionSemantics.Of.IDEMPOTENT)
    public ApplicationRole removePermission(
            final @Named("Rule") ApplicationPermissionRule rule,
            final @Named("Type") ApplicationFeatureType type,
            final @Named("Feature") @TypicalLength(ApplicationFeature.TYPICAL_LENGTH_MEMBER_NAME) String featureFqn) {
        final ApplicationPermission permission = applicationPermissions.findByRoleAndRuleAndFeature(this, rule, type, featureFqn);
        if(permission != null) {
            container.removeIfNotAlready(permission);
        }
        return this;
    }

    public String validateRemovePermission(
            final @Named("Rule") ApplicationPermissionRule rule,
            final @Named("Type") ApplicationFeatureType type,
            final @Named("Feature") @TypicalLength(ApplicationFeature.TYPICAL_LENGTH_MEMBER_NAME) String featureFqn) {
        if(isAdminRole() && IsisModuleSecurityAdminRoleAndPermissions.oneOf(featureFqn)) {
            return "Cannot remove top-level package permissions for the admin role.";
        }
        return null;
    }
    public ApplicationPermissionRule default0RemovePermission() {
        return ApplicationPermissionRule.ALLOW;
    }
    public ApplicationFeatureType default1RemovePermission() {
        return ApplicationFeatureType.PACKAGE;
    }

    public Collection<String> choices2RemovePermission(
            final ApplicationPermissionRule rule,
            final ApplicationFeatureType type) {
        final List<ApplicationPermission> permissions = applicationPermissions.findByRoleAndRuleAndFeatureType(this, rule, type);
        return Lists.newArrayList(
                Iterables.transform(
                        permissions,
                        ApplicationPermission.Functions.GET_FQN));
    }

    //endregion

    //region > users (collection)
    @javax.jdo.annotations.Persistent(mappedBy = "roles")
    private SortedSet<ApplicationUser> users = new TreeSet<>();

    @MemberOrder(sequence = "20")
    @Render(Render.Type.EAGERLY)
    @Disabled
    public SortedSet<ApplicationUser> getUsers() {
        return users;
    }

    public void setUsers(final SortedSet<ApplicationUser> users) {
        this.users = users;
    }

    // necessary for integration tests
    public void addToUsers(final ApplicationUser applicationUser) {
        getUsers().add(applicationUser);
    }
    // necessary for integration tests
    public void removeFromUsers(final ApplicationUser applicationUser) {
        getUsers().remove(applicationUser);
    }
    //endregion

    //region > addUser (action), removeUser (action)
    @ActionSemantics(ActionSemantics.Of.IDEMPOTENT)
    @Named("Add")
    @MemberOrder(name="Users", sequence = "1")
    public ApplicationRole addUser(final ApplicationUser applicationUser) {
        applicationUser.addRole(this);
        // no need to add to users set, since will be done by JDO/DN.
        return this;
    }

    public List<ApplicationUser> autoComplete0AddUser(final String search) {
        final List<ApplicationUser> matchingSearch = applicationUsers.findUsersByName(search);
        final List<ApplicationUser> list = Lists.newArrayList(matchingSearch);
        list.removeAll(getUsers());
        return list;
    }

    @ActionSemantics(ActionSemantics.Of.IDEMPOTENT)
    @Named("Remove")
    @MemberOrder(name="Users", sequence = "2")
    public ApplicationRole removeUser(final ApplicationUser applicationUser) {
        applicationUser.removeRole(this);
        // no need to remove from users set, since will be done by JDO/DN.
        return this;
    }

    public Collection<ApplicationUser> choices0RemoveUser() {
        return getUsers();
    }
    public String disableRemoveUser(final ApplicationUser applicationUser) {
        return choices0RemoveUser().isEmpty()? "No users to remove": null;
    }

    //endregion

    //region > delete (action)
    @ActionSemantics(ActionSemantics.Of.NON_IDEMPOTENT)
    @MemberOrder(sequence = "1")
    public List<ApplicationRole> delete(
            final @Named("Are you sure?") @Optional Boolean areYouSure) {
        getUsers().clear();
        getPermissions().clear();
        container.flush();
        container.removeIfNotAlready(this);
        container.flush();
        return applicationRoles.allRoles();
    }

    public String disableDelete(final Boolean areYouSure) {
        return isAdminRole() ? "Cannot delete the admin role" : null;
    }

    public String validateDelete(final Boolean areYouSure) {
        return not(areYouSure) ? "Please confirm this action": null;
    }

    static boolean not(Boolean areYouSure) {
        return areYouSure == null || !areYouSure;
    }
    //endregion

    //region > helpers
    boolean isAdminRole() {
        final ApplicationRole adminRole = applicationRoles.findRoleByName(IsisModuleSecurityAdminRoleAndPermissions.ROLE_NAME);
        return this == adminRole;
    }
    //endregion

    //region > Functions

    public static class Functions {
        private Functions(){}

        public static Function<ApplicationRole, String> GET_NAME = new Function<ApplicationRole, String>() {
            @Override
            public String apply(ApplicationRole input) {
                return input.getName();
            }
        };
    }
    //endregion

    //region > equals, hashCode, compareTo, toString
    private final static String propertyNames = "name";

    @Override
    public int compareTo(final ApplicationRole o) {
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
    DomainObjectContainer container;
    @javax.inject.Inject
    ApplicationFeatures applicationFeatures;
    @javax.inject.Inject
    ApplicationPermissions applicationPermissions;
    @javax.inject.Inject
    ApplicationUsers applicationUsers;
    @javax.inject.Inject
    ApplicationRoles applicationRoles;
    //endregion

}
