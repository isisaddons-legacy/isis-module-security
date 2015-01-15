/*
 *  Copyright 2014 Dan Haywood
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
import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.Identifier;
import org.apache.isis.applib.annotation.ActionInteraction;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.Bookmarkable;
import org.apache.isis.applib.annotation.Bounded;
import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MaxLength;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.ObjectType;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.Render;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.eventbus.ActionInteractionEvent;
import org.apache.isis.applib.util.ObjectContracts;

@javax.jdo.annotations.PersistenceCapable(
        identityType = IdentityType.APPLICATION, table = "IsisSecurityApplicationTenancy")
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
                name = "findByPath", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.isisaddons.module.security.dom.tenancy.ApplicationTenancy "
                        + "WHERE path == :path"),
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

    public static final int MAX_LENGTH_PATH = 12;
    public static final int MAX_LENGTH_NAME = 40;
    public static final int TYPICAL_LENGTH_NAME = 20;

    //region > name (property, title)

    public static class UpdateNameEvent extends ActionInteractionEvent<ApplicationTenancy> {
        public UpdateNameEvent(ApplicationTenancy source, Identifier identifier, Object... args) {
            super(source, identifier, args);
        }
    }

    private String name;

    @javax.jdo.annotations.Column(allowsNull="false", length = MAX_LENGTH_NAME)
    @PropertyLayout(typicalLength=TYPICAL_LENGTH_NAME)
    @Title
    @Disabled
    @MemberOrder(sequence = "1")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ActionInteraction(UpdateNameEvent.class)
    @MemberOrder(name="name", sequence = "1")
    @ActionSemantics(ActionSemantics.Of.IDEMPOTENT)
    public ApplicationTenancy updateName(
            final @ParameterLayout(named="Name", typicalLength=TYPICAL_LENGTH_NAME) @MaxLength(MAX_LENGTH_NAME) String name) {
        setName(name);
        return this;
    }

    public String default0UpdateName() {
        return getName();
    }
    //endregion

    //region > path
    private String path;

    @javax.jdo.annotations.PrimaryKey
    @javax.jdo.annotations.Column(length = MAX_LENGTH_PATH, allowsNull = "false")
    @Disabled
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
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

    public static class AddUserEvent extends ActionInteractionEvent<ApplicationTenancy> {
        public AddUserEvent(ApplicationTenancy source, Identifier identifier, Object... args) {
            super(source, identifier, args);
        }
    }

    public static class RemoveUserEvent extends ActionInteractionEvent<ApplicationTenancy> {
        public RemoveUserEvent(ApplicationTenancy source, Identifier identifier, Object... args) {
            super(source, identifier, args);
        }
    }

    @ActionInteraction(AddUserEvent.class)
    @ActionSemantics(ActionSemantics.Of.IDEMPOTENT)
    @MemberOrder(name="Users", sequence = "1")
    @ActionLayout(named="Add",cssClassFa = "fa fa-plus-square")
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

    @ActionInteraction(RemoveUserEvent.class)
    @ActionSemantics(ActionSemantics.Of.IDEMPOTENT)
    @MemberOrder(name="Users", sequence = "2")
    @ActionLayout(named="Remove",cssClassFa = "fa fa-minus-square")
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

    //region > parent (property), updateParent (action)
    private ApplicationTenancy parent;

    @javax.jdo.annotations.Column(name = "parentPath", allowsNull = "true")
    @Hidden(where = Where.PARENTED_TABLES)
    @Disabled
    public ApplicationTenancy getParent() {
        return parent;
    }

    public void setParent(ApplicationTenancy parent) {
        this.parent = parent;
    }

    public static class UpdateParentEvent extends ActionInteractionEvent<ApplicationTenancy> {
        public UpdateParentEvent(ApplicationTenancy source, Identifier identifier, Object... args) {
            super(source, identifier, args);
        }
    }

    @ActionInteraction(UpdateParentEvent.class)
    @MemberOrder(name="parent", sequence = "1")
    @ActionSemantics(ActionSemantics.Of.IDEMPOTENT)
    public ApplicationTenancy updateParent(final @Optional ApplicationTenancy tenancy) {
        // no need to add to children set, since will be done by JDO/DN.
        setParent(tenancy);
        return this;
    }

    public ApplicationTenancy default0UpdateParent() {
        return getParent();
    }
    //endregion


    //region > children
    @javax.jdo.annotations.Persistent(mappedBy = "parent")
    private SortedSet<ApplicationTenancy> children = new TreeSet<ApplicationTenancy>();

    @Render(Render.Type.EAGERLY)
    @Disabled
    public SortedSet<ApplicationTenancy> getChildren() {
        return children;
    }

    public void setChildren(SortedSet<ApplicationTenancy> children) {
        this.children = children;
    }

    // necessary for integration tests
    public void addToChildren(final ApplicationTenancy applicationTenancy) {
        getChildren().add(applicationTenancy);
    }
    // necessary for integration tests
    public void removeFromChildren(final ApplicationTenancy applicationTenancy) {
        getChildren().remove(applicationTenancy);
    }
    //endregion

    //region > addChild (action), removeChild (action)

    public static class AddChildEvent extends ActionInteractionEvent<ApplicationTenancy> {
        public AddChildEvent(ApplicationTenancy source, Identifier identifier, Object... args) {
            super(source, identifier, args);
        }
    }

    public static class RemoveChildEvent extends ActionInteractionEvent<ApplicationTenancy> {
        public RemoveChildEvent(ApplicationTenancy source, Identifier identifier, Object... args) {
            super(source, identifier, args);
        }
    }

    @ActionInteraction(AddChildEvent.class)
    @ActionSemantics(ActionSemantics.Of.IDEMPOTENT)
    @MemberOrder(name="Children", sequence = "1")
    @ActionLayout(named="Add",cssClassFa = "fa fa-plus-square")
    public ApplicationTenancy addChild(final ApplicationTenancy applicationTenancy) {
        applicationTenancy.setParent(this);
        // no need to add to children set, since will be done by JDO/DN.
        return this;
    }

    @ActionInteraction(RemoveChildEvent.class)
    @ActionSemantics(ActionSemantics.Of.IDEMPOTENT)
    @MemberOrder(name="Children", sequence = "2")
    @ActionLayout(named="Remove",cssClassFa = "fa fa-minus-square")
    public ApplicationTenancy removeChild(final ApplicationTenancy applicationTenancy) {
        applicationTenancy.setParent(null);
        // no need to remove from children set, since will be done by JDO/DN.
        return this;
    }
    public Collection<ApplicationTenancy> choices0RemoveChild() {
        return getChildren();
    }
    public String disableRemoveChild(final ApplicationTenancy applicationTenancy) {
        return choices0RemoveChild().isEmpty()? "No children to remove": null;
    }

    //endregion



    //region > delete (action)
    public static class DeleteEvent extends ActionInteractionEvent<ApplicationTenancy> {
        public DeleteEvent(ApplicationTenancy source, Identifier identifier, Object... args) {
            super(source, identifier, args);
        }
    }

    @ActionInteraction(DeleteEvent.class)
    @ActionSemantics(ActionSemantics.Of.NON_IDEMPOTENT)
    @MemberOrder(sequence = "1")
    @ActionLayout(
        cssClassFa = "fa fa-trash",
        cssClass = "btn btn-danger"
    )
    public List<ApplicationTenancy> delete(
            final @ParameterLayout(named="Are you sure?") @Optional Boolean areYouSure) {
        for (ApplicationUser user : getUsers()) {
            user.updateTenancy(null);
        }
        container.removeIfNotAlready(this);
        container.flush();
        return applicationTenancies.allTenancies();
    }

    public String validateDelete(final Boolean areYouSure) {
        return not(areYouSure) ? "Please confirm this action": null;
    }

    public Boolean default0Delete() {
        return Boolean.FALSE;
    }

    static boolean not(Boolean areYouSure) {
        return areYouSure == null || !areYouSure;
    }
    //endregion

    //region > compareTo


    @Override
    public String toString() {
        return ObjectContracts.toString(this, "path,name");
    }

    @Override
    public int compareTo(final ApplicationTenancy o) {
        return ObjectContracts.compare(this, o, "path");
    }
    //endregion

    //region  >  (injected)
    @javax.inject.Inject
    ApplicationUsers applicationUsers;
    @javax.inject.Inject
    ApplicationTenancies applicationTenancies;
    @javax.inject.Inject
    DomainObjectContainer container;
    //endregion
}
