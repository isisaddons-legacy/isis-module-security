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

import java.util.Comparator;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.VersionStrategy;
import com.google.common.base.Function;
import com.google.common.collect.Ordering;
import org.isisaddons.module.security.dom.role.ApplicationRole;
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
 *     the role can {@link ApplicationPermissionMode#VIEWING view} the feature
 *     or can {@link ApplicationPermissionMode#CHANGING change} the state of the
 *     system using the feature.
 * </p>
 *
 * <p>
 *     For a given permission, there is an interaction between the {@link ApplicationPermissionRule rule} and the
 *     {@link ApplicationPermissionMode mode}:
 * <ul>
 *     <li>for an {@link ApplicationPermissionRule#ALLOW allow}, a
 *     {@link ApplicationPermissionMode#CHANGING usability} allow
 *     implies {@link ApplicationPermissionMode#VIEWING visibility} allow.
 *     </li>
 *     <li>conversely, for a {@link ApplicationPermissionRule#VETO veto},
 *     a {@link ApplicationPermissionMode#VIEWING visibility} veto
 *     implies a {@link ApplicationPermissionMode#CHANGING usability} veto.</li>
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
                name = "findByUser", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.isisaddons.module.security.dom.permission.ApplicationPermission "
                        + "WHERE (u.roles.contains(role) && u.username == :username) "
                        + "VARIABLES org.isisaddons.module.security.dom.user.ApplicationUser u"),
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
        columnSpans = {3,3,6,12},
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
           .append(" ").append(getMode().toString()) // Viewing|Changing
           .append(" of ");

        final ApplicationFeatureId featureId = getFeatureId();
        switch (getFeatureType()) {
            case PACKAGE:
                buf.append(getFeatureFqn());              // com.mycompany
                break;
            case CLASS:
                // abbreviate if required because otherwise title overflows on action prompt.
                if(getFeatureFqn().length() < 30) {
                    buf.append(getFeatureFqn());          // com.mycompany.Bar
                } else {
                    buf.append(featureId.getClassName()); // Bar
                }
                break;
            case MEMBER:
                buf.append(featureId.getClassName())
                   .append("#")
                   .append(featureId.getMemberName());   // com.mycompany.Bar#foo
                break;
        }
        return buf.toString();
    }
    //endregion

    //region > role (property), updateRole (action)

    private ApplicationRole role;

    @javax.jdo.annotations.Column(name = "roleId", allowsNull="false")
    @Disabled
    @Hidden(where = Where.REFERENCES_PARENT)
    @MemberOrder(name="Role", sequence = "1")
    public ApplicationRole getRole() {
        return role;
    }

    public void setRole(final ApplicationRole role) {
        this.role = role;
    }

    @MemberOrder(name="Role", sequence = "1")
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
    public ApplicationPermission viewing() {
        setMode(ApplicationPermissionMode.VIEWING);
        return this;
    }
    public String disableViewing() {
        return getMode() == ApplicationPermissionMode.VIEWING ? "Mode is already set to VIEWING": null;
    }

    @ActionSemantics(ActionSemantics.Of.IDEMPOTENT)
    @MemberOrder(name = "Mode", sequence = "2")
    public ApplicationPermission changing() {
        setMode(ApplicationPermissionMode.CHANGING);
        return this;
    }
    public String disableChanging() {
        return getMode() == ApplicationPermissionMode.CHANGING ? "Mode is already set to CHANGING": null;
    }
    //endregion

    //region > feature (derived property)

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
        return ApplicationFeatureViewModel.newViewModel(getFeatureId(), container);
    }

    //endregion


    // region > type (derived, combined featureType and memberType)

    /**
     * Combines {@link #getFeatureType() feature type} and member type.
     */
    @Disabled
    @MemberOrder(name="Feature", sequence = "5")
    public String getType() {
        Enum<?> e = getFeatureType() != ApplicationFeatureType.MEMBER ? getFeatureType() : getMemberType();
        return e.name();
    }

    @Programmatic
    private ApplicationMemberType getMemberType() {
        return getFeature().getMemberType();
    }
    //endregion

    //region > featureType

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
    @Programmatic
    public ApplicationFeatureType getFeatureType() {
        return featureType;
    }

    public void setFeatureType(ApplicationFeatureType featureType) {
        this.featureType = featureType;
    }
    //endregion

    //region > featureFqn

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

    public static final class Functions {

        private Functions(){}

        public static final Function<ApplicationPermission, ApplicationPermissionValue> AS_VALUE = new Function<ApplicationPermission, ApplicationPermissionValue>() {
            @Override
            public ApplicationPermissionValue apply(ApplicationPermission input) {
                return new ApplicationPermissionValue(input.getFeatureId(), input.getRule(), input.getMode());
            }
        };


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
