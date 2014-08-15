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
import org.isisaddons.module.security.dom.actor.ApplicationRole;
import org.isisaddons.module.security.dom.feature.ApplicationFeature;
import org.apache.isis.applib.annotation.Disabled;

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
@javax.jdo.annotations.PersistenceCapable(identityType = IdentityType.DATASTORE)
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@javax.jdo.annotations.DatastoreIdentity(strategy = IdGeneratorStrategy.NATIVE, column = "id")
public class ApplicationPermission {

    //region > Role

    private ApplicationRole role;

    @javax.jdo.annotations.Column(allowsNull="false")
    @Disabled
    public ApplicationRole getRole() {
        return role;
    }

    public void setRole(ApplicationRole role) {
        this.role = role;
    }

    //endregion

    // //////////////////////////////////////

    //region > Feature

    @javax.jdo.annotations.NotPersistent
    @Disabled
    public ApplicationFeature getFeature() {
        return null;
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

    public void setType(ApplicationPermissionType type) {
        this.type = type;
    }
    //endregion

}
