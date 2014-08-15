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

import java.util.SortedSet;
import javax.jdo.annotations.*;
import com.google.common.collect.Sets;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.util.ObjectContracts;

@javax.jdo.annotations.PersistenceCapable(
        identityType = IdentityType.DATASTORE)
@javax.jdo.annotations.Inheritance(
        strategy = InheritanceStrategy.NEW_TABLE)
@javax.jdo.annotations.DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE, column = "id")
@javax.jdo.annotations.Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@javax.jdo.annotations.Uniques({
        @javax.jdo.annotations.Unique(
                name = "ApplicationUser_name_UNQ", members = { "name" })
})
public class ApplicationUser implements Comparable<ApplicationRole>, Actor {

    //region > name (property)
    private String name;

    @javax.jdo.annotations.Column(allowsNull="false")
    @Title
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    //endregion

    //region > roles (collection)
    @javax.jdo.annotations.Persistent(table="UserRoles")
    @javax.jdo.annotations.Join(column="userId")
    @javax.jdo.annotations.Element(column="roleId", unique = "true")
    private SortedSet<ApplicationRole> roles = Sets.newTreeSet();

    @MemberOrder(sequence = "1")
    public SortedSet<ApplicationRole> getRoles() {
        return roles;
    }

    public void setRoles(final SortedSet<ApplicationRole> roles) {
        this.roles = roles;
    }

    //endregion

    //region > compareTo

    @Override
    public int compareTo(ApplicationRole o) {
        return ObjectContracts.compare(this, o, "name");
    }
    //endregion

}
