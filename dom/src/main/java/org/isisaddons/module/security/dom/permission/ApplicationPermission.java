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

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.VersionStrategy;
import org.isisaddons.module.security.dom.actor.ApplicationRole;
import org.isisaddons.module.security.dom.feature.ApplicationFeatureId;
import org.isisaddons.module.security.dom.feature.ApplicationFeatureType;
import org.isisaddons.module.security.dom.feature.ApplicationFeatureViewModel;
import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Programmatic;
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
                        + "  AND featureFqn == :featureFqn")
})
@javax.jdo.annotations.Uniques({
        @javax.jdo.annotations.Unique(
                name = "IsisSecurityApplicationPermission_role_featureStr_mode_UNQ", members = { "name", "featureStr", "mode" })
})
public class ApplicationPermission {

    //region > identification
    /**
     * having a title() method (rather than using @Title annotation) is necessary as a workaround to be able to use
     * wrapperFactory#unwrap(...) method, which is otherwise broken in Isis 1.6.0
     */
    public String title() {
        final TitleBuffer buf = new TitleBuffer();
        buf.append(getRole().getName())           // admin
           .append(": ").append(getRule())        // ALLOW|VETO
           .append(" ").append(getFeatureType())  // PACKAGE|CLASS|MEMBER
           .append(" ").append(getFeatureFqn())   // com.mycompany.Bar#bar
           .append(" ").append(getMode());        // VISIBLE|USABLE
        return buf.toString();
    }
    //endregion

    //region > Role

    private ApplicationRole role;

    @javax.jdo.annotations.Column(allowsNull="false")
    @Disabled
    public ApplicationRole getRole() {
        return role;
    }

    public void setRole(final ApplicationRole role) {
        this.role = role;
    }

    //endregion

    // //////////////////////////////////////

    //region > Feature

    @javax.jdo.annotations.NotPersistent
    @Disabled
    public ApplicationFeatureViewModel getFeature() {
        if(getFeatureType() == null) {
            return null;
        }
        final ApplicationFeatureId featureId = ApplicationFeatureId.newFeature(getFeatureType(), getFeatureFqn());
        return container.newViewModelInstance(ApplicationFeatureViewModel.class, featureId.asEncodedString());
    }

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
    @javax.jdo.annotations.Column(allowsNull="false")
    @Programmatic
    public String getFeatureFqn() {
        return featureFqn;
    }

    public void setFeatureFqn(String featureFqn) {
        this.featureFqn = featureFqn;
    }
    //endregion

    // //////////////////////////////////////

    //region > Mode

    private ApplicationPermissionMode mode;

    @javax.jdo.annotations.Column(allowsNull="false")
    public ApplicationPermissionMode getMode() {
        return mode;
    }

    public void setMode(ApplicationPermissionMode mode) {
        this.mode = mode;
    }
    //endregion

    // //////////////////////////////////////

    //region > Type

    private ApplicationPermissionRule rule;

    @javax.jdo.annotations.Column(allowsNull="false")
    public ApplicationPermissionRule getRule() {
        return rule;
    }

    public void setRule(final ApplicationPermissionRule rule) {
        this.rule = rule;
    }
    //endregion

    // //////////////////////////////////////

    //region  >  (injected)
    @javax.inject.Inject
    DomainObjectContainer container;
    //endregion
}
