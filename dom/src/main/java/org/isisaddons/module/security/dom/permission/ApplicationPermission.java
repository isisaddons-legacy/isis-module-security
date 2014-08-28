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
package org.isisaddons.module.security.dom.permission;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.VersionStrategy;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import org.isisaddons.module.security.dom.actor.ApplicationRole;
import org.isisaddons.module.security.dom.feature.*;
import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.*;
import org.apache.isis.applib.util.ObjectContracts;
import org.apache.isis.applib.util.TitleBuffer;

/**
 * Specifies how a particular {@link #getRole() application role} may interact with a specific
 * {@link #getFeature() application feature}.
 *
 * <p>
 *     Each permission has a {@link #getRule() rule} and a {@link #getMode() mode}.  The
 *     {@link ApplicationPermissionRule rule} determines whether the permission {@link ApplicationPermissionRule#ALLOW grants}
 *     access to the feature or {@link ApplicationPermissionRule#VETO veto}es access
 *     to it.  The {@link ApplicationPermissionMode mode} indicates whether
 *     the role can {@link ApplicationPermissionMode#VISIBLE view} the feature
 *     or can {@link ApplicationPermissionMode#USABLE change} the state of the
 *     system using the feature.
 * </p>
 *
 * <p>
 *     For a given permission, there is an interaction between the {@link ApplicationPermissionRule rule} and the
 *     {@link ApplicationPermissionMode mode}:
 * <ul>
 *     <li>for an {@link ApplicationPermissionRule#ALLOW allow}, a
 *     {@link ApplicationPermissionMode#USABLE usability} allow
 *     implies {@link ApplicationPermissionMode#VISIBLE visibility} allow.
 *     </li>
 *     <li>conversely, for a {@link ApplicationPermissionRule#VETO veto},
 *     a {@link ApplicationPermissionMode#VISIBLE visibility} veto
 *     implies a {@link ApplicationPermissionMode#USABLE usability} veto.</li>
 * </ul>
 * </p>
 */
@javax.jdo.annotations.PersistenceCapable(
        identityType = IdentityType.DATASTORE, table = "IsisSecurityApplicationPermission")
@javax.jdo.annotations.Inheritance(
        strategy = InheritanceStrategy.NEW_TABLE)
@javax.jdo.annotations.DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE, column = "id")
@javax.jdo.annotations.Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@javax.jdo.annotations.Queries( {
        @javax.jdo.annotations.Query(
                name = "findByRole", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.isisaddons.module.security.dom.permission.ApplicationPermission "
                        + "WHERE role == :role"),
        @javax.jdo.annotations.Query(
                name = "findByFeature", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.isisaddons.module.security.dom.permission.ApplicationPermission "
                        + "WHERE featureType == :featureType "
                        + "   && featureFqn == :featureFqn"),
        @javax.jdo.annotations.Query(
                name = "findByRoleAndRuleAndFeature", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.isisaddons.module.security.dom.permission.ApplicationPermission "
                        + "WHERE role == :role "
                        + "   && rule == :rule "
                        + "   && featureType == :featureType "
                        + "   && featureFqn == :featureFqn "),
        @javax.jdo.annotations.Query(
                name = "findByRoleAndRuleAndFeatureType", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.isisaddons.module.security.dom.permission.ApplicationPermission "
                        + "WHERE role == :role "
                        + "   && rule == :rule "
                        + "   && featureType == :featureType "),
})
@javax.jdo.annotations.Uniques({
        @javax.jdo.annotations.Unique(
                name = "IsisSecurityApplicationPermission_role_feature_rule_UNQ", members = { "role", "featureType", "featureFqn", "rule" })
})
@MemberGroupLayout(
        columnSpans = {4,4,4,12},
        left="Role",
        middle = "Permissions",
        right="Feature"
)
@Bookmarkable(BookmarkPolicy.AS_CHILD)
public class ApplicationPermission implements Comparable<ApplicationPermission> {

    //region > identification
    /**
     * having a title() method (rather than using @Title annotation) is necessary as a workaround to be able to use
     * wrapperFactory#unwrap(...) method, which is otherwise broken in Isis 1.6.0
     */
    public String title() {
        final TitleBuffer buf = new TitleBuffer();
        buf.append(getRole().getName()).append(":")  // admin:
           .append(" ").append(getRule().toString()) // Allow|Veto
           .append(" ").append(getMode().toString()) // Visible|Usable
           .append(" ");

        final ApplicationFeatureId featureId = getFeatureId();
        switch (getFeatureType()) {
            case PACKAGE:
                buf.append(getFeatureFqn());              // com.mycompany
                break;
            case CLASS:
                if(getFeatureFqn().length() < 30) {
                    buf.append(getFeatureFqn());          // com.mycompany.Bar
                } else {
                    buf.append(featureId.getClassName()); // Bar
                }
                break;
            case MEMBER:
                buf.append(featureId.getClassName())
                   .append("#")
                   .append(featureId.getMemberName()); // Bar#foo
                break;
        }
        return buf.toString();
    }
    //endregion

    //region > role (property), updateRole (action)

    private ApplicationRole role;

    @javax.jdo.annotations.Column(name = "roleId", allowsNull="false")
    @Hidden
    public ApplicationRole getRole() {
        return role;
    }

    public void setRole(final ApplicationRole role) {
        this.role = role;
    }

    /**
     * This is a work-around to an Isis debug whereby when editing the link is replaced with a disabled drop-down,
     * even if disabled.  This unpleasant behaviour doesn't seem to happen for derived properties.
     */
    @javax.jdo.annotations.NotPersistent
    @Disabled
    @Hidden(where = Where.REFERENCES_PARENT)
    @Named("Role")
    @MemberOrder(name="Role", sequence = "1")
    public ApplicationRole getRoleWorkaround() {
        return getRole();
    }

    @MemberOrder(name="RoleWorkaround", sequence = "1")
    public ApplicationPermission updateRole(final ApplicationRole applicationRole) {
        setRole(applicationRole);
        return this;
    }

    public ApplicationRole default0UpdateRole() {
        return getRole();
    }

    //endregion

    //region > rule (property), allow (action), veto (action)

    private ApplicationPermissionRule rule;

    @javax.jdo.annotations.Column(allowsNull="false")
    @Disabled
    @MemberOrder(name="Permissions", sequence = "2")
    public ApplicationPermissionRule getRule() {
        return rule;
    }

    public void setRule(final ApplicationPermissionRule rule) {
        this.rule = rule;
    }

    @ActionSemantics(ActionSemantics.Of.IDEMPOTENT)
    @MemberOrder(name = "Rule", sequence = "1")
    public ApplicationPermission allow() {
        setRule(ApplicationPermissionRule.ALLOW);
        return this;
    }
    public String disableAllow() {
        return getRule() == ApplicationPermissionRule.ALLOW? "Rule is already set to ALLOW": null;
    }

    @ActionSemantics(ActionSemantics.Of.IDEMPOTENT)
    @MemberOrder(name = "Rule", sequence = "1")
    public ApplicationPermission veto() {
        setRule(ApplicationPermissionRule.VETO);
        return this;
    }
    public String disableVeto() {
        return getRule() == ApplicationPermissionRule.VETO? "Rule is already set to VETO": null;
    }

    //endregion

    //region > mode (property), visible (action), usable (action)

    private ApplicationPermissionMode mode;

    @javax.jdo.annotations.Column(allowsNull="false")
    @Disabled
    @MemberOrder(name="Permissions", sequence = "3")
    public ApplicationPermissionMode getMode() {
        return mode;
    }

    public void setMode(ApplicationPermissionMode mode) {
        this.mode = mode;
    }

    @ActionSemantics(ActionSemantics.Of.IDEMPOTENT)
    @MemberOrder(name = "Mode", sequence = "1")
    public ApplicationPermission visible() {
        setMode(ApplicationPermissionMode.VISIBLE);
        return this;
    }
    public String disableVisible() {
        return getMode() == ApplicationPermissionMode.VISIBLE? "Mode is already set to VISIBLE": null;
    }

    @ActionSemantics(ActionSemantics.Of.IDEMPOTENT)
    @MemberOrder(name = "Mode", sequence = "2")
    public ApplicationPermission usable() {
        setMode(ApplicationPermissionMode.USABLE);
        return this;
    }
    public String disableUsable() {
        return getMode() == ApplicationPermissionMode.USABLE? "Mode is already set to USABLE": null;
    }
    //endregion

    //region > feature (derived property), updateFeature (action)

    ApplicationFeatureId getFeatureId() {
        return ApplicationFeatureId.newFeature(getFeatureType(), getFeatureFqn());
    }

    @javax.jdo.annotations.NotPersistent
    @Disabled
    @Hidden(where=Where.REFERENCES_PARENT)
    @MemberOrder(name="Feature", sequence = "4")
    public ApplicationFeatureViewModel getFeature() {
        if(getFeatureType() == null) {
            return null;
        }
        return container.newViewModelInstance(ApplicationFeatureViewModel.class, getFeatureId().asEncodedString());
    }

    @javax.jdo.annotations.Column(allowsNull="false")
    private ApplicationFeatureType featureType;

    /**
     * The {@link org.isisaddons.module.security.dom.feature.ApplicationFeatureId#getType() feature type} of the
     * feature.
     *
     * <p>
     *     The combination of the feature type and the {@link #getFeatureFqn() fully qualified name} is used to build
     *     the corresponding {@link #getFeature() feature} (view model).
     * </p>
     *
     * @see #getFeatureFqn()
     */
    @Disabled
    @MemberOrder(name="Feature", sequence = "5")
    public ApplicationFeatureType getFeatureType() {
        return featureType;
    }

    public void setFeatureType(ApplicationFeatureType featureType) {
        this.featureType = featureType;
    }


    @javax.jdo.annotations.Column(allowsNull="false")
    private String featureFqn;

    /**
     * The {@link org.isisaddons.module.security.dom.feature.ApplicationFeatureId#getFullyQualifiedName() fully qualified name}
     * of the feature.
     * 
     * <p>
     *     The combination of the {@link #getFeatureType() feature type} and the fully qualified name is used to build
     *     the corresponding {@link #getFeature() feature} (view model).
     * </p>
     *
     * @see #getFeatureType()
     */
    @Programmatic
    public String getFeatureFqn() {
        return featureFqn;
    }

    public void setFeatureFqn(String featureFqn) {
        this.featureFqn = featureFqn;
    }


    @ActionSemantics(ActionSemantics.Of.IDEMPOTENT)
    @MemberOrder(name = "feature", sequence = "1")
    public ApplicationPermission updateFeature(
            final @Named("Feature type") ApplicationFeatureType type,
            final @Named("Fully qualified name") String featureFqn) {
        setFeatureType(type);
        setFeatureFqn(featureFqn);
        return this;
    }

    public ApplicationFeatureType default0UpdateFeature() {
        return getFeatureType();
    }
    public String default1UpdateFeature() {
        return getFeatureFqn();
    }

    public List<? extends String> choices1UpdateFeature(final ApplicationFeatureType featureType) {
        final Collection<ApplicationFeature> features = applicationFeatures.allFeatures(featureType);
        return Lists.newArrayList(
                Iterables.transform(features, ApplicationFeature.Functions.GET_FQN));
    }

    //endregion

    //region > copy (action)

    // TODO: split out into copyPackage (hide if not a package) and copyClassOrMember (hide if not),
    // TODO: and do all the choices/defaults etc as in ApplicationRole#addPackage and ApplicationRole#addClassOrMember
    @Hidden
    @MemberOrder(sequence = "1")
    public ApplicationPermission copy(
            final ApplicationRole role,
            final @Named("Rule") ApplicationPermissionRule rule,
            final @Named("Mode") ApplicationPermissionMode mode,
            final @Named("Package") @TypicalLength(ApplicationFeature.TYPICAL_LENGTH_PKG_FQN) String packageFqn,
            final @Named("Class") @TypicalLength(ApplicationFeature.TYPICAL_LENGTH_CLS_NAME) String className,
            final @Optional @Named("Member") @TypicalLength(ApplicationFeature.TYPICAL_LENGTH_MEMBER_NAME) String memberName) {
        return applicationPermissions.newPermission(role, rule, mode, packageFqn, className, memberName);
    }

    public ApplicationRole default0Copy() {
        return getRole();
    }

    public ApplicationPermissionRule default1Copy() {
        return getRule();
    }

    public ApplicationPermissionMode default2Copy() {
        return getMode();
    }

    public String default3Copy() {
        return getFeatureId().getPackageName();
    }

    public String default4Copy() {
        return getFeatureId().getClassName();
    }

    public String default5Copy() {
        return getFeatureId().getMemberName();
    }
    //endregion

    //region > equals, hashCode, compareTo, toString
    private final static String propertyNames = "role, featureType, featureFqn, mode";

    @Override
    public int compareTo(final ApplicationPermission o) {
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

    public static class DefaultComparator implements Comparator<ApplicationPermission> {
        @Override
        public int compare(ApplicationPermission o1, ApplicationPermission o2) {
            return Ordering.natural().compare(o1, o2);
        }
    }
    //endregion

    //region > Functions

    public static class Functions {

        private Functions(){}

        public static final Function<ApplicationPermission, String> GET_FQN = new Function<ApplicationPermission, String>() {
            @Override
            public String apply(ApplicationPermission input) {
                return input.getFeatureFqn();
            }
        };

    }
    //endregion

    //region  > services (injected)
    @javax.inject.Inject
    DomainObjectContainer container;

    @javax.inject.Inject
    ApplicationFeatures applicationFeatures;

    @javax.inject.Inject
    ApplicationPermissions applicationPermissions;

    //endregion

}
