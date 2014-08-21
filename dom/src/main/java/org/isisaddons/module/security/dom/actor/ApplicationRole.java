/*
 *  Copyright 2014 Jeroen van der Wal
 *
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
package org.isisaddons.module.security.dom.actor;

import java.util.*;
import javax.inject.Inject;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.InheritanceStrategy;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.isisaddons.module.security.dom.feature.ApplicationFeature;
import org.isisaddons.module.security.dom.feature.ApplicationFeatureId;
import org.isisaddons.module.security.dom.feature.ApplicationFeatureType;
import org.isisaddons.module.security.dom.feature.ApplicationFeatures;
import org.isisaddons.module.security.dom.permission.ApplicationPermission;
import org.isisaddons.module.security.dom.permission.ApplicationPermissionMode;
import org.isisaddons.module.security.dom.permission.ApplicationPermissionRule;
import org.isisaddons.module.security.dom.permission.ApplicationPermissions;
import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.*;
import org.apache.isis.applib.util.ObjectContracts;
import org.apache.isis.applib.util.TitleBuffer;

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
                        + "FROM org.isisaddons.module.security.dom.actor.ApplicationRole "
                        + "WHERE name == :name"),
        @javax.jdo.annotations.Query(
                name = "findByNameContaining", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.isisaddons.module.security.dom.actor.ApplicationRole "
                        + "WHERE name.indexOf(:name) >= 0")
})
@AutoComplete(repository=ApplicationRoles.class, action="autoComplete")
@ObjectType("IsisSecurityApplicationRole")
@Bookmarkable
public class ApplicationRole implements Comparable<ApplicationRole>, Actor {


    //region > identification
    /**
     * having a title() method (rather than using @Title annotation) is necessary as a workaround to be able to use
     * wrapperFactory#unwrap(...) method, which is otherwise broken in Isis 1.6.0
     */
    public String title() {
        final TitleBuffer buf = new TitleBuffer();
        buf.append(getName());
        return buf.toString();
    }
    //endregion

    //region > name (property,)
    private String name;

    @javax.jdo.annotations.Column(allowsNull="false")
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
            final @Named("Name") String name) {
        setName(name);
        return this;
    }

    public String default0UpdateName() {
        return getName();
    }

    //endregion

    //region > roles (collection, not persisted, programmatic)

    @Programmatic
    public SortedSet<ApplicationRole> getRoles() {
        return new TreeSet(Collections.singleton(this));
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
    @MemberOrder(name = "Permissions", sequence = "1")
    @ActionSemantics(ActionSemantics.Of.IDEMPOTENT)
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
        return ApplicationPermissionMode.USABLE;
    }

    public List<String> choices2AddPackage() {
        return Lists.newArrayList(Iterables.transform(applicationFeatures.allFeatures(ApplicationFeatureType.PACKAGE), ApplicationFeature.Functions.GET_FQN));
    }
    //endregion

    //region > addClassOrMember (action)
    /**
     * Adds a {@link org.isisaddons.module.security.dom.permission.ApplicationPermission permission} for this role to a
     * {@link org.isisaddons.module.security.dom.feature.ApplicationFeatureType#MEMBER member}
     * {@link org.isisaddons.module.security.dom.feature.ApplicationFeature feature}.
     */
    @Named("Add Class/Member")
    @MemberOrder(name = "Permissions", sequence = "2")
    @ActionSemantics(ActionSemantics.Of.IDEMPOTENT)
    public ApplicationRole addClassOrMember(
            final @Named("Rule") ApplicationPermissionRule rule,
            final @Named("Mode") ApplicationPermissionMode mode,
            final @Named("Package") @TypicalLength(ApplicationFeature.TYPICAL_LENGTH_PKG_FQN) String packageFqn,
            final @Named("Class") @TypicalLength(ApplicationFeature.TYPICAL_LENGTH_CLS_NAME) String className,
            final @Optional @Named("Member") @TypicalLength(ApplicationFeature.TYPICAL_LENGTH_MEMBER_NAME) String memberName) {
        applicationPermissions.newPermission(this, rule, mode, packageFqn, className, memberName);
        return this;
    }

    public ApplicationPermissionRule default0AddClassOrMember() {
        return ApplicationPermissionRule.ALLOW;
    }

    public ApplicationPermissionMode default1AddClassOrMember() {
        return ApplicationPermissionMode.USABLE;
    }

    /**
     * Package names that have classes in them.
     */
    public List<String> choices2AddClassOrMember() {
        final Collection<ApplicationFeature> packages = applicationFeatures.allFeatures(ApplicationFeatureType.PACKAGE);
        return Lists.newArrayList(
                Iterables.transform(
                        Iterables.filter(
                                packages,
                                ApplicationFeature.Predicates.PACKAGE_CONTAINING_CLASSES
                        ),
                        ApplicationFeature.Functions.GET_FQN));
    }

    /**
     * Class names for selected package.
     */
    public List<String> choices3AddClassOrMember(
            final ApplicationPermissionRule rule,
            final ApplicationPermissionMode mode,
            final String packageFqn) {
        final ApplicationFeatureId packageId = ApplicationFeatureId.newPackage(packageFqn);
        final ApplicationFeature pkg = applicationFeatures.findPackage(packageId);
        if(pkg == null) {
            return Collections.emptyList();
        }
        final SortedSet<ApplicationFeatureId> contents = pkg.getContents();
        return Lists.newArrayList(
                Iterables.transform(
                        Iterables.filter(
                                contents,
                                ApplicationFeatureId.Predicates.IS_CLASS),
                        ApplicationFeatureId.Functions.GET_CLASS_NAME));
    }

    /**
     * Member names for selected class.
     */
    public List<String> choices4AddClassOrMember(
            final ApplicationPermissionRule rule,
            final ApplicationPermissionMode mode,
            final String packageFqn,
            final String className) {
        final ApplicationFeatureId classId = ApplicationFeatureId.newClass(packageFqn + "." + className);
        final ApplicationFeature cls = applicationFeatures.findClass(classId);
        if(cls == null) {
            return Collections.emptyList();
        }
        return Lists.newArrayList(
                Iterables.transform(cls.getMembers(), ApplicationFeatureId.Functions.GET_MEMBER)
        );
    }
    //endregion

    //region > remove (action)
    @MemberOrder(name = "Permissions", sequence = "2")
    @ActionSemantics(ActionSemantics.Of.IDEMPOTENT)
    public ApplicationRole remove(
            final @Named("Rule") ApplicationPermissionRule rule,
            final @Named("Type") ApplicationFeatureType type,
            final @Named("Feature") String featureFqn) {
        final ApplicationPermission permission = applicationPermissions.findByRoleAndRuleAndFeature(rule, type, featureFqn);
        if(permission != null) {
            container.removeIfNotAlready(permission);
        }
        return this;
    }

    public ApplicationPermissionRule default0Remove() {
        return ApplicationPermissionRule.ALLOW;
    }
    public ApplicationFeatureType default1Remove() {
        return ApplicationFeatureType.PACKAGE;
    }

    public Collection<String> choices2Remove(
            final ApplicationPermissionRule rule,
            final ApplicationFeatureType type) {
        final ApplicationRole role = this;
        final List<ApplicationPermission> permissions = applicationPermissions.findByRoleAndRuleAndFeatureType(rule, type, role);
        return Lists.newArrayList(
                Iterables.transform(
                        permissions,
                        ApplicationPermission.Functions.GET_FQN));
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
    @Inject
    DomainObjectContainer container;
    @Inject
    ApplicationFeatures applicationFeatures;
    @Inject
    ApplicationPermissions applicationPermissions;
    //endregion
}
