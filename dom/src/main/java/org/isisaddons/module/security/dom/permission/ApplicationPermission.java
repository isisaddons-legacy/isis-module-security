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

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.InheritanceStrategy;

import org.isisaddons.module.security.dom.feature.ApplicationFeature;
import org.isisaddons.module.security.dom.role.ApplicationRole;

@javax.jdo.annotations.PersistenceCapable(identityType = IdentityType.DATASTORE)
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@javax.jdo.annotations.DatastoreIdentity(strategy = IdGeneratorStrategy.NATIVE, column = "id")
public class ApplicationPermission {

    private ApplicationRole role;

    @Column(allowsNull="false")
    public ApplicationRole getRole() {
        return role;
    }

    public void setRole(ApplicationRole role) {
        this.role = role;
    }

    // //////////////////////////////////////

    private ApplicationFeature feature;

    @Column(allowsNull="false")
    public ApplicationFeature getFeature() {
        return feature;
    }

    public void setFeature(ApplicationFeature feature) {
        this.feature = feature;
    }
    
    // //////////////////////////////////////
    
    private ApplicationPermissionLevel level;
    
    @Column(allowsNull="false")
    public ApplicationPermissionLevel getLevel() {
        return level;
    }
    
    public void setLevel(ApplicationPermissionLevel level) {
        this.level = level;
    }
    
    // //////////////////////////////////////
    
    private boolean isInherited;
    
    public boolean isInherited() {
        return isInherited;
    }
    
    public void setInherited(boolean isInherited) {
        this.isInherited = isInherited;
    }

}
