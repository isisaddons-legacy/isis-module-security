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

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.InheritanceStrategy;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.Identifier;
import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.Collection;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.RenderType;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.util.ObjectContracts;
import org.apache.isis.objectstore.jdo.applib.service.JdoColumnLength;

import org.isisaddons.module.security.SecurityModule;
import org.isisaddons.module.security.dom.feature.ApplicationFeature;
import org.isisaddons.module.security.dom.feature.ApplicationFeatureRepository;
import org.isisaddons.module.security.dom.feature.ApplicationFeatureType;
import org.isisaddons.module.security.dom.feature.ApplicationMemberType;
import org.isisaddons.module.security.dom.permission.ApplicationPermission;
import org.isisaddons.module.security.dom.permission.ApplicationPermissionMode;
import org.isisaddons.module.security.dom.permission.ApplicationPermissionRepository;
import org.isisaddons.module.security.dom.permission.ApplicationPermissionRule;
import org.isisaddons.module.security.dom.user.ApplicationUser;
import org.isisaddons.module.security.dom.user.ApplicationUserRepository;
import org.isisaddons.module.security.seed.scripts.IsisModuleSecurityAdminRoleAndPermissions;

@SuppressWarnings("UnusedDeclaration")
@javax.jdo.annotations.PersistenceCapable(
        identityType = IdentityType.DATASTORE,
        schema = "isissecurity",
        table = "ApplicationRole")
@javax.jdo.annotations.Inheritance(
        strategy = InheritanceStrategy.NEW_TABLE)
@javax.jdo.annotations.DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE, column = "id")
@javax.jdo.annotations.Uniques({
        @javax.jdo.annotations.Unique(
                name = "ApplicationRole_name_UNQ", members = { "name" })
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
@DomainObject(
        bounded = true,
        objectType = "isissecurity.ApplicationRole"
)
@DomainObjectLayout(
        bookmarking = BookmarkPolicy.AS_ROOT
)
public class ApplicationRole implements Comparable<ApplicationRole> {

    public static abstract class PropertyDomainEvent<T> extends SecurityModule.PropertyDomainEvent<ApplicationRole, T> {
        public PropertyDomainEvent(final ApplicationRole source, final Identifier identifier) {
            super(source, identifier);
        }

        public PropertyDomainEvent(final ApplicationRole source, final Identifier identifier, final T oldValue, final T newValue) {
            super(source, identifier, oldValue, newValue);
        }
    }

    public static abstract class CollectionDomainEvent<T> extends SecurityModule.CollectionDomainEvent<ApplicationRole, T> {
        public CollectionDomainEvent(final ApplicationRole source, final Identifier identifier, final Of of) {
            super(source, identifier, of);
        }

        public CollectionDomainEvent(final ApplicationRole source, final Identifier identifier, final Of of, final T value) {
            super(source, identifier, of, value);
        }
    }

    public static abstract class ActionDomainEvent extends SecurityModule.ActionDomainEvent<ApplicationRole> {
        public ActionDomainEvent(final ApplicationRole source, final Identifier identifier) {
            super(source, identifier);
        }

        public ActionDomainEvent(final ApplicationRole source, final Identifier identifier, final Object... arguments) {
            super(source, identifier, arguments);
        }

        public ActionDomainEvent(final ApplicationRole source, final Identifier identifier, final List<Object> arguments) {
            super(source, identifier, arguments);
        }
    }

    // //////////////////////////////////////

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

    //region > name (property)

    public static class NameDomainEvent extends PropertyDomainEvent<String> {
        public NameDomainEvent(final ApplicationRole source, final Identifier identifier) {
            super(source, identifier);
        }
        public NameDomainEvent(final ApplicationRole source, final Identifier identifier, final String oldValue, final String newValue) {
            super(source, identifier, oldValue, newValue);
        }
    }

    private String name;

    @javax.jdo.annotations.Column(allowsNull="false", length = MAX_LENGTH_NAME)
    @Property(
            domainEvent = NameDomainEvent.class,
            editing = Editing.DISABLED
    )
    @PropertyLayout(typicalLength=TYPICAL_LENGTH_NAME)
    @MemberOrder(sequence = "1")
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    //endregion

    //region > updateName (action)

    public static class UpdateNameDomainEvent extends ActionDomainEvent {
        public UpdateNameDomainEvent(final ApplicationRole source, final Identifier identifier, final Object... args) {
            super(source, identifier, args);
        }
    }

    @Action(
            domainEvent = UpdateNameDomainEvent.class,
            semantics = SemanticsOf.IDEMPOTENT
    )
    @MemberOrder(name="name", sequence = "1")
    public ApplicationRole updateName(
            @Parameter(maxLength = MAX_LENGTH_NAME) @ParameterLayout(named="Name", typicalLength = TYPICAL_LENGTH_NAME)
            final String name) {
        setName(name);
        return this;
    }

    public String default0UpdateName() {
        return getName();
    }

    //endregion

    //region > description (property)

    public static class DescriptionDomainEvent extends PropertyDomainEvent<String> {
        public DescriptionDomainEvent(final ApplicationRole source, final Identifier identifier) {
            super(source, identifier);
        }

        public DescriptionDomainEvent(final ApplicationRole source, final Identifier identifier, final String oldValue, final String newValue) {
            super(source, identifier, oldValue, newValue);
        }
    }

    private String description;

    @javax.jdo.annotations.Column(allowsNull="true", length = JdoColumnLength.DESCRIPTION)
    @Property(
            domainEvent = DescriptionDomainEvent.class,
            editing = Editing.DISABLED
    )
    @PropertyLayout(
            typicalLength=TYPICAL_LENGTH_DESCRIPTION
    )
    @MemberOrder(sequence = "2")
    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    //endregion

    //region > updateDescription (action)

    public static class UpdateDescriptionDomainEvent extends ActionDomainEvent {
        public UpdateDescriptionDomainEvent(final ApplicationRole source, final Identifier identifier, final Object... args) {
            super(source, identifier, args);
        }
    }

    @Action(
            domainEvent = UpdateDescriptionDomainEvent.class,
            semantics = SemanticsOf.IDEMPOTENT
    )
    @MemberOrder(name="description", sequence = "1")
    public ApplicationRole updateDescription(
            @Parameter(
                    maxLength = JdoColumnLength.DESCRIPTION,
                    optionality = Optionality.OPTIONAL
            )
            @ParameterLayout(named="Description", typicalLength=TYPICAL_LENGTH_DESCRIPTION)
            final String description) {
        setDescription(description);
        return this;
    }

    public String default0UpdateDescription() {
        return getDescription();
    }

    //endregion

    //region > permissions (derived collection)
    public static class PermissionsCollectionDomainEvent extends CollectionDomainEvent<ApplicationPermission> {
        public PermissionsCollectionDomainEvent(final ApplicationRole source, final Identifier identifier, final Of of) {
            super(source, identifier, of);
        }
        public PermissionsCollectionDomainEvent(final ApplicationRole source, final Identifier identifier, final Of of, final ApplicationPermission value) {
            super(source, identifier, of, value);
        }
    }

    @Collection(
            domainEvent = PermissionsCollectionDomainEvent.class
    )
    @CollectionLayout(
            render = RenderType.EAGERLY,
            sortedBy = ApplicationPermission.DefaultComparator.class
    )
    @MemberOrder(sequence = "10")
    public List<ApplicationPermission> getPermissions() {
        return applicationPermissionRepository.findByRole(this);
    }
    //endregion

    //region > addPackage (action)

    public static class AddPackageDomainEvent extends ActionDomainEvent {
        public AddPackageDomainEvent(final ApplicationRole source, final Identifier identifier, final Object... args) {
            super(source, identifier, args);
        }
    }

    /**
     * Adds a {@link org.isisaddons.module.security.dom.permission.ApplicationPermission permission} for this role to a
     * {@link org.isisaddons.module.security.dom.feature.ApplicationFeatureType#PACKAGE package}
     * {@link org.isisaddons.module.security.dom.feature.ApplicationFeature feature}.
     */
    @Action(
            domainEvent = AddPackageDomainEvent.class,
            semantics = SemanticsOf.NON_IDEMPOTENT
    )
    @ActionLayout(
            cssClassFa = "fa-plus-square"
    )
    @MemberOrder(name = "Permissions", sequence = "1")
    public ApplicationRole addPackage(
            @ParameterLayout(named="Rule")
            final ApplicationPermissionRule rule,
            @ParameterLayout(named="Mode")
            final ApplicationPermissionMode mode,
            @ParameterLayout(named="Package", typicalLength=ApplicationFeature.TYPICAL_LENGTH_PKG_FQN)
            final String packageFqn) {
        applicationPermissionRepository.newPermission(this, rule, mode, ApplicationFeatureType.PACKAGE, packageFqn);
        return this;
    }

    public ApplicationPermissionRule default0AddPackage() {
        return ApplicationPermissionRule.ALLOW;
    }

    public ApplicationPermissionMode default1AddPackage() {
        return ApplicationPermissionMode.CHANGING;
    }

    public List<String> choices2AddPackage() {
        return applicationFeatureRepository.packageNames();
    }
    //endregion

    //region > addClass (action)
    public static class AddClassDomainEvent extends ActionDomainEvent {
        public AddClassDomainEvent(final ApplicationRole source, final Identifier identifier, final Object... args) {
            super(source, identifier, args);
        }
    }

    /**
     * Adds a {@link org.isisaddons.module.security.dom.permission.ApplicationPermission permission} for this role to a
     * {@link org.isisaddons.module.security.dom.feature.ApplicationFeatureType#MEMBER member}
     * {@link org.isisaddons.module.security.dom.feature.ApplicationFeature feature}.
     */
    @Action(
            domainEvent = AddClassDomainEvent.class,
            semantics = SemanticsOf.NON_IDEMPOTENT
    )
    @ActionLayout(
            cssClassFa = "fa-plus-square"
    )
    @MemberOrder(name = "Permissions", sequence = "2")
    public ApplicationRole addClass(
            @ParameterLayout(named="Rule")
            final ApplicationPermissionRule rule,
            @ParameterLayout(named="Mode")
            final ApplicationPermissionMode mode,
            @ParameterLayout(named="Package", typicalLength=ApplicationFeature.TYPICAL_LENGTH_PKG_FQN)
            final String packageFqn,
            @ParameterLayout(named="Class", typicalLength=ApplicationFeature.TYPICAL_LENGTH_CLS_NAME)
            final String className) {
        applicationPermissionRepository.newPermission(this, rule, mode, ApplicationFeatureType.CLASS,
                packageFqn + "." + className);
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
        return applicationFeatureRepository.packageNamesContainingClasses(null);
    }

    /**
     * Class names for selected package.
     */
    public List<String> choices3AddClass(
            final ApplicationPermissionRule rule,
            final ApplicationPermissionMode mode,
            final String packageFqn) {
        return applicationFeatureRepository.classNamesContainedIn(packageFqn, null);
    }

    //endregion

    //region > addAction (action)
    public static class AddActionDomainEvent extends ActionDomainEvent {
        public AddActionDomainEvent(final ApplicationRole source, final Identifier identifier, final Object... args) {
            super(source, identifier, args);
        }
    }
    /**
     * Adds a {@link org.isisaddons.module.security.dom.permission.ApplicationPermission permission} for this role to a
     * {@link org.isisaddons.module.security.dom.feature.ApplicationMemberType#ACTION action}
     * {@link org.isisaddons.module.security.dom.feature.ApplicationFeatureType#MEMBER member}
     * {@link org.isisaddons.module.security.dom.feature.ApplicationFeature feature}.
     */
    @Action(
            domainEvent = AddActionDomainEvent.class,
            semantics = SemanticsOf.NON_IDEMPOTENT
    )
    @ActionLayout(
            cssClassFa = "fa-plus-square"
    )
    @MemberOrder(name = "Permissions", sequence = "3")
    public ApplicationRole addAction(
            @ParameterLayout(named="Rule")
            final ApplicationPermissionRule rule,
            @ParameterLayout(named="Mode")
            final ApplicationPermissionMode mode,
            @ParameterLayout(named="Package", typicalLength=ApplicationFeature.TYPICAL_LENGTH_PKG_FQN)
            final String packageFqn,
            @ParameterLayout(named="Class", typicalLength=ApplicationFeature.TYPICAL_LENGTH_CLS_NAME)
            final String className,
            @ParameterLayout(named="Action", typicalLength = ApplicationFeature.TYPICAL_LENGTH_MEMBER_NAME)
            final String memberName) {
        applicationPermissionRepository.newPermission(this, rule, mode, packageFqn, className, memberName);
        return this;
    }

    public ApplicationPermissionRule default0AddAction() {
        return ApplicationPermissionRule.ALLOW;
    }

    public ApplicationPermissionMode default1AddAction() {
        return ApplicationPermissionMode.CHANGING;
    }

    public List<String> choices2AddAction() {
        return applicationFeatureRepository.packageNamesContainingClasses(ApplicationMemberType.ACTION);
    }

    public List<String> choices3AddAction(
            final ApplicationPermissionRule rule,
            final ApplicationPermissionMode mode,
            final String packageFqn) {
        return applicationFeatureRepository.classNamesContainedIn(packageFqn, ApplicationMemberType.ACTION);
    }

    public List<String> choices4AddAction(
            final ApplicationPermissionRule rule,
            final ApplicationPermissionMode mode,
            final String packageFqn,
            final String className) {
        return applicationFeatureRepository.memberNamesOf(packageFqn, className, ApplicationMemberType.ACTION);
    }

    //endregion

    //region > addProperty (action)
    public static class AddPropertyDomainEvent extends ActionDomainEvent {
        public AddPropertyDomainEvent(final ApplicationRole source, final Identifier identifier, final Object... args) {
            super(source, identifier, args);
        }
    }
    /**
     * Adds a {@link org.isisaddons.module.security.dom.permission.ApplicationPermission permission} for this role to a
     * {@link org.isisaddons.module.security.dom.feature.ApplicationMemberType#PROPERTY property}
     * {@link org.isisaddons.module.security.dom.feature.ApplicationFeatureType#MEMBER member}
     * {@link org.isisaddons.module.security.dom.feature.ApplicationFeature feature}.
     */
    @Action(
            domainEvent = AddPropertyDomainEvent.class,
            semantics = SemanticsOf.NON_IDEMPOTENT
    )
    @ActionLayout(
            cssClassFa = "fa-plus-square"
    )
    @MemberOrder(name = "Permissions", sequence = "4")
    public ApplicationRole addProperty(
            @ParameterLayout(named="Rule")
            final ApplicationPermissionRule rule,
            @ParameterLayout(named="Mode")
            final ApplicationPermissionMode mode,
            @ParameterLayout(named="Package", typicalLength=ApplicationFeature.TYPICAL_LENGTH_PKG_FQN)
            final String packageFqn,
            @ParameterLayout(named="Class", typicalLength=ApplicationFeature.TYPICAL_LENGTH_CLS_NAME)
            final String className,
            @ParameterLayout(named="Property", typicalLength=ApplicationFeature.TYPICAL_LENGTH_MEMBER_NAME)
            final String memberName) {
        applicationPermissionRepository.newPermission(this, rule, mode, packageFqn, className, memberName);
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
        return applicationFeatureRepository.packageNamesContainingClasses(ApplicationMemberType.PROPERTY);
    }

    /**
     * Class names for selected package.
     */
    public List<String> choices3AddProperty(
            final ApplicationPermissionRule rule,
            final ApplicationPermissionMode mode,
            final String packageFqn) {
        return applicationFeatureRepository.classNamesContainedIn(packageFqn, ApplicationMemberType.PROPERTY);
    }

    /**
     * Member names for selected class.
     */
    public List<String> choices4AddProperty(
            final ApplicationPermissionRule rule,
            final ApplicationPermissionMode mode,
            final String packageFqn,
            final String className) {
        return applicationFeatureRepository.memberNamesOf(packageFqn, className, ApplicationMemberType.PROPERTY);
    }
    //endregion

    //region > addCollection (action)
    public static class AddCollectionDomainEvent extends ActionDomainEvent {
        public AddCollectionDomainEvent(final ApplicationRole source, final Identifier identifier, final Object... args) {
            super(source, identifier, args);
        }
    }

    /**
     * Adds a {@link org.isisaddons.module.security.dom.permission.ApplicationPermission permission} for this role to a
     * {@link org.isisaddons.module.security.dom.feature.ApplicationMemberType#COLLECTION collection}
     * {@link org.isisaddons.module.security.dom.feature.ApplicationFeatureType#MEMBER member}
     * {@link org.isisaddons.module.security.dom.feature.ApplicationFeature feature}.
     */
    @Action(
            domainEvent = AddCollectionDomainEvent.class,
            semantics = SemanticsOf.NON_IDEMPOTENT
    )
    @ActionLayout(
            cssClassFa = "fa-plus-square"
    )
    @MemberOrder(name = "Permissions", sequence = "5")
    public ApplicationRole addCollection(
            @ParameterLayout(named="Rule")
            final ApplicationPermissionRule rule,
            @ParameterLayout(named="Mode")
            final ApplicationPermissionMode mode,
            @ParameterLayout(named="Package", typicalLength=ApplicationFeature.TYPICAL_LENGTH_PKG_FQN)
            final String packageFqn,
            @ParameterLayout(named="Class", typicalLength=ApplicationFeature.TYPICAL_LENGTH_CLS_NAME)
            final String className,
            @ParameterLayout(named="Collection", typicalLength=ApplicationFeature.TYPICAL_LENGTH_MEMBER_NAME)
            final String memberName) {
        applicationPermissionRepository.newPermission(this, rule, mode, packageFqn, className, memberName);
        return this;
    }

    public ApplicationPermissionRule default0AddCollection() {
        return ApplicationPermissionRule.ALLOW;
    }

    public ApplicationPermissionMode default1AddCollection() {
        return ApplicationPermissionMode.CHANGING;
    }

    public List<String> choices2AddCollection() {
        return applicationFeatureRepository.packageNamesContainingClasses(ApplicationMemberType.COLLECTION);
    }

    public List<String> choices3AddCollection(
            final ApplicationPermissionRule rule,
            final ApplicationPermissionMode mode,
            final String packageFqn) {
        return applicationFeatureRepository.classNamesContainedIn(packageFqn, ApplicationMemberType.COLLECTION);
    }

    public List<String> choices4AddCollection(
            final ApplicationPermissionRule rule,
            final ApplicationPermissionMode mode,
            final String packageFqn,
            final String className) {
        return applicationFeatureRepository.memberNamesOf(packageFqn, className, ApplicationMemberType.COLLECTION);
    }

    //endregion

    //region > removePermission (action)
    public static class RemovePermissionDomainEvent extends ActionDomainEvent {
        public RemovePermissionDomainEvent(final ApplicationRole source, final Identifier identifier, final Object... args) {
            super(source, identifier, args);
        }
    }

    @Action(
            domainEvent= RemovePermissionDomainEvent.class,
            semantics = SemanticsOf.IDEMPOTENT
    )
    @ActionLayout(
            cssClassFa = "fa fa-minus-square"
    )
    @MemberOrder(name = "Permissions", sequence = "9")
    public ApplicationRole removePermission(
            @ParameterLayout(named="Rule")
            final ApplicationPermissionRule rule,
            @ParameterLayout(named="Type")
            final ApplicationFeatureType type,
            @ParameterLayout(named="Feature", typicalLength=ApplicationFeature.TYPICAL_LENGTH_MEMBER_NAME)
            final String featureFqn) {
        final ApplicationPermission permission = applicationPermissionRepository.findByRoleAndRuleAndFeature(this, rule, type, featureFqn);
        if(permission != null) {
            container.removeIfNotAlready(permission);
        }
        return this;
    }

    public String validateRemovePermission(
            @ParameterLayout(named="Rule")
            final ApplicationPermissionRule rule,
            @ParameterLayout(named="Type")
            final ApplicationFeatureType type,
            @ParameterLayout(named="Feature", typicalLength=ApplicationFeature.TYPICAL_LENGTH_MEMBER_NAME)
            final String featureFqn) {
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

    public java.util.Collection<String> choices2RemovePermission(
            final ApplicationPermissionRule rule,
            final ApplicationFeatureType type) {
        final List<ApplicationPermission> permissions = applicationPermissionRepository.findByRoleAndRuleAndFeatureType(this, rule, type);
        return Lists.newArrayList(
                Iterables.transform(
                        permissions,
                        ApplicationPermission.Functions.GET_FQN));
    }

    //endregion

    //region > users (collection)

    public static class UsersDomainEvent extends CollectionDomainEvent<ApplicationUser> {
        public UsersDomainEvent(final ApplicationRole source, final Identifier identifier, final Of of) {
            super(source, identifier, of);
        }

        public UsersDomainEvent(final ApplicationRole source, final Identifier identifier, final Of of, final ApplicationUser value) {
            super(source, identifier, of, value);
        }
    }

    @javax.jdo.annotations.Persistent(mappedBy = "roles")
    private SortedSet<ApplicationUser> users = new TreeSet<>();

    @Collection(
            domainEvent = UsersDomainEvent.class,
            editing = Editing.DISABLED
    )
    @CollectionLayout(
            render = RenderType.EAGERLY
    )
    @MemberOrder(sequence = "20")
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

    //region > addUser (action)

    public static class AddUserDomainEvent extends ActionDomainEvent {
        public AddUserDomainEvent(final ApplicationRole source, final Identifier identifier, final Object... args) {
            super(source, identifier, args);
        }
    }

    @Action(
            domainEvent = AddUserDomainEvent.class,
            semantics = SemanticsOf.IDEMPOTENT
    )
    @ActionLayout(
        named="Add",
        cssClassFa = "fa fa-plus-square"
    )
    @MemberOrder(name="Users", sequence = "1")
    public ApplicationRole addUser(final ApplicationUser applicationUser) {
        applicationUser.addRole(this);
        // no need to add to users set, since will be done by JDO/DN.
        return this;
    }

    public List<ApplicationUser> autoComplete0AddUser(final String search) {
        final List<ApplicationUser> matchingSearch = applicationUserRepository.findUsersByName(search);
        final List<ApplicationUser> list = Lists.newArrayList(matchingSearch);
        list.removeAll(getUsers());
        return list;
    }

    //endregion

    //region > removeUser (action)

    public static class RemoveUserDomainEvent extends ActionDomainEvent {
        public RemoveUserDomainEvent(final ApplicationRole source, final Identifier identifier, final Object... args) {
            super(source, identifier, args);
        }
    }

    @Action(
            domainEvent = RemoveUserDomainEvent.class,
            semantics = SemanticsOf.IDEMPOTENT
    )
    @ActionLayout(
            named="Remove",
            cssClassFa = "fa fa-minus-square"
    )
    @MemberOrder(name="Users", sequence = "2")
    public ApplicationRole removeUser(final ApplicationUser applicationUser) {
        applicationUser.removeRole(this);
        // no need to remove from users set, since will be done by JDO/DN.
        return this;
    }

    public java.util.Collection<ApplicationUser> choices0RemoveUser() {
        return getUsers();
    }

    public String validateRemoveUser(
            final ApplicationUser applicationUser) {
        return applicationUser.validateRemoveRole(this);
    }

    //endregion

    //region > delete (action)
    public static class DeleteDomainEvent extends ActionDomainEvent {
        public DeleteDomainEvent(final ApplicationRole source, final Identifier identifier, final Object... args) {
            super(source, identifier, args);
        }
    }

    @Action(
            domainEvent = DeleteDomainEvent.class,
            semantics = SemanticsOf.NON_IDEMPOTENT
    )
    @ActionLayout(
        cssClassFa = "fa-trash",
        cssClass = "btn btn-danger"
    )
    @MemberOrder(sequence = "1")
    public List<ApplicationRole> delete(
            @Parameter(optionality = Optionality.OPTIONAL)
            @ParameterLayout(named="Are you sure?")
            final Boolean areYouSure) {
        getUsers().clear();
        final List<ApplicationPermission> permissions = getPermissions();
        for (final ApplicationPermission permission : permissions) {
            permission.delete(areYouSure);
        }
        container.flush();
        container.removeIfNotAlready(this);
        container.flush();
        return applicationRoleRepository.allRoles();
    }

    public String disableDelete(final Boolean areYouSure) {
        return isAdminRole() ? "Cannot delete the admin role" : null;
    }

    public String validateDelete(final Boolean areYouSure) {
        return not(areYouSure) ? "Please confirm this action": null;
    }

    static boolean not(final Boolean areYouSure) {
        return areYouSure == null || !areYouSure;
    }
    //endregion

    //region > isAdminRole (programmatic)
    @Programmatic
    public boolean isAdminRole() {
        final ApplicationRole adminRole = applicationRoleRepository.findRoleByName(IsisModuleSecurityAdminRoleAndPermissions.ROLE_NAME);
        return this == adminRole;
    }
    //endregion

    //region > Functions

    public static class Functions {
        private Functions(){}

        public static Function<ApplicationRole, String> GET_NAME = new Function<ApplicationRole, String>() {
            @Override
            public String apply(final ApplicationRole input) {
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
    ApplicationFeatureRepository applicationFeatureRepository;
    @javax.inject.Inject
    ApplicationPermissionRepository applicationPermissionRepository;
    @javax.inject.Inject
    ApplicationUserRepository applicationUserRepository;
    @javax.inject.Inject
    ApplicationRoleRepository applicationRoleRepository;
    //endregion

}
