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

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.VersionStrategy;
import com.google.common.collect.Sets;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.apache.isis.applib.annotation.*;
import org.apache.isis.applib.util.ObjectContracts;
import org.apache.isis.applib.util.TitleBuffer;

@javax.jdo.annotations.PersistenceCapable(
        identityType = IdentityType.DATASTORE, table = "IsisSecurityApplicationUser")
@javax.jdo.annotations.Inheritance(
        strategy = InheritanceStrategy.NEW_TABLE)
@javax.jdo.annotations.DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE, column = "id")
@javax.jdo.annotations.Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@javax.jdo.annotations.Uniques({
        @javax.jdo.annotations.Unique(
                name = "IsisSecurityApplicationUser_name_UNQ", members = { "name" })
})
@javax.jdo.annotations.Queries( {
        @javax.jdo.annotations.Query(
                name = "findByName", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.isisaddons.module.security.dom.actor.ApplicationUser "
                        + "WHERE name == :name"),
        @javax.jdo.annotations.Query(
                name = "findByNameContaining", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.isisaddons.module.security.dom.actor.ApplicationUser "
                        + "WHERE name.indexOf(:name) >= 0")

})
@AutoComplete(repository=ApplicationUsers.class, action="autoComplete")
@ObjectType("IsisSecurityApplicationUser")
@Bookmarkable
public class ApplicationUser implements Comparable<ApplicationUser>, Actor {

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

    //region > name (property, title)
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
    public ApplicationUser updateName(
            final @Named("Name") String name) {
        setName(name);
        return this;
    }

    public String default0UpdateName() {
        return getName();
    }
    //endregion

    
    //region > tenancy (property)
    private ApplicationTenancy tenancy;

    @javax.jdo.annotations.Column(allowsNull="true")
    @MemberOrder(sequence = "1")
    @Disabled
    public ApplicationTenancy getTenancy() {
        return tenancy;
    }

    public void setTenancy(final ApplicationTenancy tenancy) {
        this.tenancy = tenancy;
    }

    @MemberOrder(name="tenancy", sequence = "1")
    @ActionSemantics(ActionSemantics.Of.IDEMPOTENT)
    public ApplicationUser updateTenancy(final @Optional ApplicationTenancy tenancy) {
        setTenancy(tenancy);
        return this;
    }

    public ApplicationTenancy default0UpdateTenancy() {
        return getTenancy();
    }
    //endregion


    //region > roles (collection)
    @javax.jdo.annotations.Persistent(table="IsisSecurityApplicationUserRoles")
    @javax.jdo.annotations.Join(column="userId")
    @javax.jdo.annotations.Element(column="roleId")
    private SortedSet<ApplicationRole> roles = new TreeSet<>();

    @MemberOrder(sequence = "2")
    @Render(Render.Type.EAGERLY)
    @Disabled
    public SortedSet<ApplicationRole> getRoles() {
        return roles;
    }

    public void setRoles(final SortedSet<ApplicationRole> roles) {
        this.roles = roles;
    }

    // necessary only because otherwise call to getRoles() through wrapped object
    // (in integration tests) is ambiguous.
    public void addToRoles(final ApplicationRole applicationRole) {
        getRoles().add(applicationRole);
    }
    // necessary only because otherwise call to getRoles() through wrapped object
    // (in integration tests) is ambiguous.
    public void removeFromRoles(final ApplicationRole applicationRole) {
        getRoles().remove(applicationRole);
    }


    //endregion


    //region > addRole (actions)
    @MemberOrder(name="roles", sequence = "1")
    @ActionSemantics(ActionSemantics.Of.IDEMPOTENT)
    public ApplicationUser addRole(final ApplicationRole role) {
        addToRoles(role);
        return this;
    }

    public SortedSet<ApplicationRole> choices0AddRole() {
        final List<ApplicationRole> allRoles = applicationRoles.allRoles();
        final SortedSet<ApplicationRole> applicationRoles = Sets.newTreeSet(allRoles);
        applicationRoles.removeAll(getRoles());
        return applicationRoles;
    }

    public String disableAddRole(final ApplicationRole role) {
        return choices0AddRole().isEmpty()? "All roles added": null;
    }
    //endregion


    //region > removeRole (actions)
    @MemberOrder(name="roles", sequence = "2")
    @ActionSemantics(ActionSemantics.Of.IDEMPOTENT)
    public ApplicationUser removeRole(final ApplicationRole role) {
        removeFromRoles(role);
        return this;
    }

    public SortedSet<ApplicationRole> choices0RemoveRole() {
        return getRoles();
    }

    public String disableRemoveRole(final ApplicationRole role) {
        return choices0RemoveRole().isEmpty()? "No roles to remove": null;
    }
    //endregion
    
    
    //region > equals, hashCode, compareTo, toString
    private final static String propertyNames = "name";

    @Override
    public int compareTo(final ApplicationUser o) {
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
    ApplicationRoles applicationRoles;
    //endregion
}
