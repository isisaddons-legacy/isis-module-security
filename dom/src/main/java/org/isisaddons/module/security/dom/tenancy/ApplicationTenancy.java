/*
 *  Copyright 2014 Dan Haywood
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
package org.isisaddons.module.security.dom.tenancy;

import java.util.Collection;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.VersionStrategy;
import com.google.common.collect.Lists;
import org.isisaddons.module.security.dom.user.ApplicationUser;
import org.isisaddons.module.security.dom.user.ApplicationUsers;
import org.apache.isis.applib.annotation.*;
import org.apache.isis.applib.util.ObjectContracts;

@javax.jdo.annotations.PersistenceCapable(
        identityType = IdentityType.DATASTORE, table = "IsisSecurityApplicationTenancy")
@javax.jdo.annotations.Inheritance(
        strategy = InheritanceStrategy.NEW_TABLE)
@javax.jdo.annotations.DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE, column = "id")
@javax.jdo.annotations.Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@javax.jdo.annotations.Uniques({
        @javax.jdo.annotations.Unique(
                name = "IsisSecurityApplicationTenancy_name_UNQ", members = { "name" })
})
@javax.jdo.annotations.Queries( {
        @javax.jdo.annotations.Query(
                name = "findByName", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.isisaddons.module.security.dom.tenancy.ApplicationTenancy "
                        + "WHERE name == :name"),
        @javax.jdo.annotations.Query(
                name = "findByNameContaining", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.isisaddons.module.security.dom.tenancy.ApplicationTenancy "
                        + "WHERE name.indexOf(:name) >= 0")

})
@ObjectType("IsisSecurityApplicationTenancy")
@Bounded // rather than auto-complete, since only small number likely to exist.
@Bookmarkable
public class ApplicationTenancy implements Comparable<ApplicationTenancy> {

    public static final int MAX_LENGTH_NAME = 40;
    public static final int TYPICAL_LENGTH_NAME = 20;

    //region > name (property, title)
    private String name;

    @javax.jdo.annotations.Column(allowsNull="false", length = MAX_LENGTH_NAME)
    @TypicalLength(TYPICAL_LENGTH_NAME)
    @Title
    @Disabled
    @MemberOrder(sequence = "1")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @MemberOrder(name="name", sequence = "1")
    @ActionSemantics(ActionSemantics.Of.IDEMPOTENT)
    public ApplicationTenancy updateName(
            final @Named("Name") @TypicalLength(TYPICAL_LENGTH_NAME) @MaxLength(MAX_LENGTH_NAME) String name) {
        setName(name);
        return this;
    }

    public String default0UpdateName() {
        return getName();
    }
    //endregion

    //region > users (collection)
    @javax.jdo.annotations.Persistent(mappedBy = "tenancy")
    private SortedSet<ApplicationUser> users = new TreeSet<>();

    @MemberOrder(sequence = "10")
    @Render(Render.Type.EAGERLY)
    @Disabled
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

    //region > addUser (action), removeUser (action)
    @ActionSemantics(ActionSemantics.Of.IDEMPOTENT)
    @Named("Add")
    @MemberOrder(name="Users", sequence = "1")
    public ApplicationTenancy addUser(final ApplicationUser applicationUser) {
        applicationUser.setTenancy(this);
        // no need to add to users set, since will be done by JDO/DN.
        return this;
    }

    public List<ApplicationUser> autoComplete0AddUser(final String search) {
        final List<ApplicationUser> matchingSearch = applicationUsers.findUsersByName(search);
        final List<ApplicationUser> list = Lists.newArrayList(matchingSearch);
        list.removeAll(getUsers());
        return list;
    }

    @ActionSemantics(ActionSemantics.Of.IDEMPOTENT)
    @Named("Remove")
    @MemberOrder(name="Users", sequence = "2")
    public ApplicationTenancy removeUser(final ApplicationUser applicationUser) {
        applicationUser.setTenancy(null);
        // no need to add to users set, since will be done by JDO/DN.
        return this;
    }
    public Collection<ApplicationUser> choices0RemoveUser() {
        return getUsers();
    }
    public String disableRemoveUser(final ApplicationUser applicationUser) {
        return choices0RemoveUser().isEmpty()? "No users to remove": null;
    }

    //endregion

    //region > compareTo

    @Override
    public int compareTo(final ApplicationTenancy o) {
        return ObjectContracts.compare(this, o, "name");
    }
    //endregion

    //region  >  (injected)
    @javax.inject.Inject
    ApplicationUsers applicationUsers;
    //endregion
}
