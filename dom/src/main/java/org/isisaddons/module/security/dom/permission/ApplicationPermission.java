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
import org.isisaddons.module.security.dom.feature.ApplicationFeature;
import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.util.TitleBuffer;

/**
 * Specifies how a particular {@link #getRole() application role} may interact with a specific
 * {@link #getFeature() application feature}.
 *
 * <p>
 *     Each permission has a {@link #getType() type} and a {@link #getMode() mode}.  The
 *     {@link ApplicationPermissionType type} determines whether the permission {@link ApplicationPermissionType#ALLOW grants}
 *     access to the feature or {@link org.isisaddons.module.security.dom.permission.ApplicationPermissionType#VETO veto}es access
 *     to it.  The {@link org.isisaddons.module.security.dom.permission.ApplicationPermissionMode mode} indicates whether
 *     the role can {@link org.isisaddons.module.security.dom.permission.ApplicationPermissionMode#VISIBLE view} the feature
 *     or can {@link org.isisaddons.module.security.dom.permission.ApplicationPermissionMode#USABLE change} the state of the
 *     system using the feature.
 * </p>
 *
 * <p>
 *     For a given permission, there is an interaction between the {@link ApplicationPermissionType type} and the
 *     {@link org.isisaddons.module.security.dom.permission.ApplicationPermissionMode mode}:
 * <ul>
 *     <li>for an {@link org.isisaddons.module.security.dom.permission.ApplicationPermissionType#ALLOW allow}, a
 *     {@link org.isisaddons.module.security.dom.permission.ApplicationPermissionMode#USABLE usability} allow
 *     implies {@link org.isisaddons.module.security.dom.permission.ApplicationPermissionMode#VISIBLE visibility} allow.
 *     </li>
 *     <li>conversely, for a {@link org.isisaddons.module.security.dom.permission.ApplicationPermissionType#VETO veto},
 *     a {@link org.isisaddons.module.security.dom.permission.ApplicationPermissionMode#VISIBLE visibility} veto
 *     implies a {@link org.isisaddons.module.security.dom.permission.ApplicationPermissionMode#USABLE usability} veto.</li>
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
                        + "WHERE featureStr == :feature"),
        @javax.jdo.annotations.Query(
                name = "findByRole", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.isisaddons.module.security.dom.permission.ApplicationPermission "
                        + "WHERE role == :role")

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
           .append(": ").append(getType())        // ALLOW|VETO
           .append(" ").append(getFeatureStr())   // com.mycompany.Bar#*
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
    public ApplicationFeature getFeature() {
        return getFeatureStr() != null?
            container.newViewModelInstance(ApplicationFeature.class, getFeatureStr()): null;
    }

    private String featureStr;

    /**
     * String representation of the feature.
     */
    @javax.jdo.annotations.Column(allowsNull="false", name = "feature")
    @Disabled
    public String getFeatureStr() {
        return featureStr;
    }

    public void setFeatureStr(String featureStr) {
        this.featureStr = featureStr;
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

    private ApplicationPermissionType type;

    @javax.jdo.annotations.Column(allowsNull="false")
    public ApplicationPermissionType getType() {
        return type;
    }

    public void setType(final ApplicationPermissionType type) {
        this.type = type;
    }
    //endregion

    // //////////////////////////////////////

    //region  >  (injected)
    @javax.inject.Inject
    DomainObjectContainer container;
    //endregion
}
