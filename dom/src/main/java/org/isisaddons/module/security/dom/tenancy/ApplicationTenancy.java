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

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.VersionStrategy;

import com.google.common.collect.Lists;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.Identifier;
import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.Collection;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.RenderType;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.util.ObjectContracts;

import org.isisaddons.module.security.SecurityModule;
import org.isisaddons.module.security.dom.user.ApplicationUser;
import org.isisaddons.module.security.dom.user.ApplicationUserRepository;

@SuppressWarnings("UnusedDeclaration")
@javax.jdo.annotations.PersistenceCapable(
        identityType = IdentityType.APPLICATION,
        schema = "isissecurity",
        table = "ApplicationTenancy")
@javax.jdo.annotations.Inheritance(
        strategy = InheritanceStrategy.NEW_TABLE)
@javax.jdo.annotations.DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE, column = "id")
@javax.jdo.annotations.Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@javax.jdo.annotations.Uniques({
        @javax.jdo.annotations.Unique(
                name = "ApplicationTenancy_name_UNQ", members = { "name" })
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
                name = "findByNameOrPathMatching", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.isisaddons.module.security.dom.tenancy.ApplicationTenancy "
                        + "WHERE name.matches(:regex) || path.matches(:regex) ")})
@DomainObject(
        objectType = "isissecurity.ApplicationTenancy",
        autoCompleteRepository = ApplicationTenancyRepository.class,
        autoCompleteAction = "autoComplete"
)
@DomainObjectLayout(
        bookmarking = BookmarkPolicy.AS_ROOT
)
public class ApplicationTenancy implements Comparable<ApplicationTenancy> {

    public static abstract class PropertyDomainEvent<T> extends SecurityModule.PropertyDomainEvent<ApplicationTenancy, T> {
        public PropertyDomainEvent(final ApplicationTenancy source, final Identifier identifier) {
            super(source, identifier);
        }

        public PropertyDomainEvent(final ApplicationTenancy source, final Identifier identifier, final T oldValue, final T newValue) {
            super(source, identifier, oldValue, newValue);
        }
    }

    public static abstract class CollectionDomainEvent<T> extends SecurityModule.CollectionDomainEvent<ApplicationTenancy, T> {
        public CollectionDomainEvent(final ApplicationTenancy source, final Identifier identifier, final Of of) {
            super(source, identifier, of);
        }

        public CollectionDomainEvent(final ApplicationTenancy source, final Identifier identifier, final Of of, final T value) {
            super(source, identifier, of, value);
        }
    }

    public static abstract class ActionDomainEvent extends SecurityModule.ActionDomainEvent<ApplicationTenancy> {
        public ActionDomainEvent(final ApplicationTenancy source, final Identifier identifier) {
            super(source, identifier);
        }

        public ActionDomainEvent(final ApplicationTenancy source, final Identifier identifier, final Object... arguments) {
            super(source, identifier, arguments);
        }

        public ActionDomainEvent(final ApplicationTenancy source, final Identifier identifier, final List<Object> arguments) {
            super(source, identifier, arguments);
        }
    }

    // //////////////////////////////////////

    public static final int MAX_LENGTH_PATH = 255;
    public static final int MAX_LENGTH_NAME = 40;
    public static final int TYPICAL_LENGTH_NAME = 20;

    //region > name (property, title)

    public static class NameDomainEvent extends PropertyDomainEvent<String> {
        public NameDomainEvent(final ApplicationTenancy source, final Identifier identifier) {
            super(source, identifier);
        }

        public NameDomainEvent(final ApplicationTenancy source, final Identifier identifier, final String oldValue, final String newValue) {
            super(source, identifier, oldValue, newValue);
        }
    }

    private String name;

    @javax.jdo.annotations.Column(allowsNull="false", length = MAX_LENGTH_NAME)
    @Title
    @Property(
            domainEvent = NameDomainEvent.class,
            editing = Editing.DISABLED
    )
    @PropertyLayout(
            typicalLength=TYPICAL_LENGTH_NAME
    )
    @MemberOrder(sequence = "1")
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    //endregion

    //region > updateName (action)

    public static class UpdateNameDomainEvent extends ActionDomainEvent {
        public UpdateNameDomainEvent(final ApplicationTenancy source, final Identifier identifier, final Object... args) {
            super(source, identifier, args);
        }
    }

    @Action(
            domainEvent =UpdateNameDomainEvent.class,
            semantics = SemanticsOf.IDEMPOTENT
    )
    @MemberOrder(name="name", sequence = "1")
    public ApplicationTenancy updateName(
            @Parameter(maxLength = MAX_LENGTH_NAME)
            @ParameterLayout(named="Name", typicalLength=TYPICAL_LENGTH_NAME)
            final String name) {
        setName(name);
        return this;
    }

    public String default0UpdateName() {
        return getName();
    }
    //endregion

    //region > path

    public static class PathDomainEvent extends PropertyDomainEvent<String> {
        public PathDomainEvent(final ApplicationTenancy source, final Identifier identifier) {
            super(source, identifier);
        }

        public PathDomainEvent(final ApplicationTenancy source, final Identifier identifier, final String oldValue, final String newValue) {
            super(source, identifier, oldValue, newValue);
        }
    }

    private String path;

    @javax.jdo.annotations.PrimaryKey
    @javax.jdo.annotations.Column(length = MAX_LENGTH_PATH, allowsNull = "false")
    @Property(
            domainEvent = PathDomainEvent.class,
            editing = Editing.DISABLED
    )
    public String getPath() {
        return path;
    }

    public void setPath(final String path) {
        this.path = path;
    }

    //endregion

    //region > users (collection)

    public static class UsersDomainEvent extends CollectionDomainEvent<ApplicationUser> {
        public UsersDomainEvent(final ApplicationTenancy source, final Identifier identifier, final Of of) {
            super(source, identifier, of);
        }
        public UsersDomainEvent(final ApplicationTenancy source, final Identifier identifier, final Of of, final ApplicationUser value) {
            super(source, identifier, of, value);
        }
    }

    @javax.jdo.annotations.Persistent(mappedBy = "tenancy")
    private SortedSet<ApplicationUser> users = new TreeSet<>();

    @Collection(
            domainEvent = UsersDomainEvent.class,
            editing = Editing.DISABLED
    )
    @CollectionLayout(
            render = RenderType.EAGERLY
    )
    @MemberOrder(sequence = "10")
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

    //region > addUser (action)

    public static class AddUserDomainEvent extends ActionDomainEvent {
        public AddUserDomainEvent(final ApplicationTenancy source, final Identifier identifier, final Object... args) {
            super(source, identifier, args);
        }
    }

    @Action(
           domainEvent = AddUserDomainEvent.class,
            semantics = SemanticsOf.IDEMPOTENT
    )
    @ActionLayout(
            named="Add",
            cssClassFa = "fa fa-plus-square"
    )
    @MemberOrder(name="Users", sequence = "1")
    public ApplicationTenancy addUser(final ApplicationUser applicationUser) {
        applicationUser.setTenancy(this);
        // no need to add to users set, since will be done by JDO/DN.
        return this;
    }

    public List<ApplicationUser> autoComplete0AddUser(final String search) {
        final List<ApplicationUser> matchingSearch = applicationUserRepository.findByName(search);
        final List<ApplicationUser> list = Lists.newArrayList(matchingSearch);
        list.removeAll(getUsers());
        return list;
    }

    //endregion

    //region > removeUser (action)

    public static class RemoveUserDomainEvent extends ActionDomainEvent {
        public RemoveUserDomainEvent(final ApplicationTenancy source, final Identifier identifier, final Object... args) {
            super(source, identifier, args);
        }
    }

    @Action(
            domainEvent = RemoveUserDomainEvent.class,
            semantics = SemanticsOf.IDEMPOTENT
    )
    @ActionLayout(
            named="Remove",
            cssClassFa = "fa fa-minus-square"
    )
    @MemberOrder(name="Users", sequence = "2")
    public ApplicationTenancy removeUser(final ApplicationUser applicationUser) {
        applicationUser.setTenancy(null);
        // no need to add to users set, since will be done by JDO/DN.
        return this;
    }
    public java.util.Collection<ApplicationUser> choices0RemoveUser() {
        return getUsers();
    }
    public String disableRemoveUser(final ApplicationUser applicationUser) {
        return choices0RemoveUser().isEmpty()? "No users to remove": null;
    }

    //endregion

    //region > parent (property)

    public static class ParentDomainEvent extends PropertyDomainEvent<ApplicationTenancy> {
        public ParentDomainEvent(final ApplicationTenancy source, final Identifier identifier) {
            super(source, identifier);
        }
        public ParentDomainEvent(final ApplicationTenancy source, final Identifier identifier, final ApplicationTenancy oldValue, final ApplicationTenancy newValue) {
            super(source, identifier, oldValue, newValue);
        }
    }

    private ApplicationTenancy parent;

    @javax.jdo.annotations.Column(name = "parentPath", allowsNull = "true")
    @Property(
            domainEvent = ParentDomainEvent.class,
            editing = Editing.DISABLED
    )
    @PropertyLayout(
            hidden = Where.PARENTED_TABLES
    )
    public ApplicationTenancy getParent() {
        return parent;
    }

    public void setParent(final ApplicationTenancy parent) {
        this.parent = parent;
    }

    //endregion

    //region > updateParent (action)

    public static class UpdateParentDomainEvent extends ActionDomainEvent {
        public UpdateParentDomainEvent(final ApplicationTenancy source, final Identifier identifier, final Object... args) {
            super(source, identifier, args);
        }
    }

    @Action(
            domainEvent = UpdateParentDomainEvent.class,
            semantics = SemanticsOf.IDEMPOTENT
    )
    @MemberOrder(name="parent", sequence = "1")
    public ApplicationTenancy updateParent(
            @Parameter(optionality = Optionality.OPTIONAL)
            final ApplicationTenancy tenancy
    ) {
        // no need to add to children set, since will be done by JDO/DN.
        setParent(tenancy);
        return this;
    }

    public ApplicationTenancy default0UpdateParent() {
        return getParent();
    }
    //endregion


    //region > children

    public static class ChildrenDomainEvent extends CollectionDomainEvent<ApplicationTenancy> {
        public ChildrenDomainEvent(final ApplicationTenancy source, final Identifier identifier, final Of of) {
            super(source, identifier, of);
        }

        public ChildrenDomainEvent(final ApplicationTenancy source, final Identifier identifier, final Of of, final ApplicationTenancy value) {
            super(source, identifier, of, value);
        }
    }

    @javax.jdo.annotations.Persistent(mappedBy = "parent")
    private SortedSet<ApplicationTenancy> children = new TreeSet<>();

    @Collection(
            domainEvent = ChildrenDomainEvent.class,
            editing = Editing.DISABLED
    )
    @CollectionLayout(
            render = RenderType.EAGERLY
    )
    public SortedSet<ApplicationTenancy> getChildren() {
        return children;
    }

    public void setChildren(final SortedSet<ApplicationTenancy> children) {
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

    //region > addChild (action)

    public static class AddChildDomainEvent extends ActionDomainEvent {
        public AddChildDomainEvent(final ApplicationTenancy source, final Identifier identifier, final Object... args) {
            super(source, identifier, args);
        }
    }

    @Action(
            domainEvent = AddChildDomainEvent.class,
            semantics = SemanticsOf.IDEMPOTENT
    )
    @ActionLayout(
            named="Add",
            cssClassFa = "fa-plus-square"
    )
    @MemberOrder(name="Children", sequence = "1")
    public ApplicationTenancy addChild(final ApplicationTenancy applicationTenancy) {
        applicationTenancy.setParent(this);
        // no need to add to children set, since will be done by JDO/DN.
        return this;
    }

    //endregion

    //region > removeChild (action)

    public static class RemoveChildDomainEvent extends ActionDomainEvent {
        public RemoveChildDomainEvent(final ApplicationTenancy source, final Identifier identifier, final Object... args) {
            super(source, identifier, args);
        }
    }

    @Action(
            domainEvent = RemoveChildDomainEvent.class,
            semantics = SemanticsOf.IDEMPOTENT
    )
    @ActionLayout(
            named="Remove",
            cssClassFa = "fa fa-minus-square"
    )
    @MemberOrder(name="Children", sequence = "2")
    public ApplicationTenancy removeChild(final ApplicationTenancy applicationTenancy) {
        applicationTenancy.setParent(null);
        // no need to remove from children set, since will be done by JDO/DN.
        return this;
    }
    public java.util.Collection<ApplicationTenancy> choices0RemoveChild() {
        return getChildren();
    }
    public String disableRemoveChild(final ApplicationTenancy applicationTenancy) {
        return choices0RemoveChild().isEmpty()? "No children to remove": null;
    }

    //endregion


    //region > delete (action)
    public static class DeleteDomainEvent extends ActionDomainEvent {
        public DeleteDomainEvent(final ApplicationTenancy source, final Identifier identifier, final Object... args) {
            super(source, identifier, args);
        }
    }

    @Action(
            domainEvent = DeleteDomainEvent.class,
            semantics = SemanticsOf.NON_IDEMPOTENT
    )
    @MemberOrder(sequence = "1")
    @ActionLayout(
        cssClassFa = "fa-trash",
        cssClass = "btn btn-danger"
    )
    public List<ApplicationTenancy> delete(
            @Parameter(optionality = Optionality.OPTIONAL)
            @ParameterLayout(named="Are you sure?")
            final Boolean areYouSure) {
        for (final ApplicationUser user : getUsers()) {
            user.updateTenancy(null);
        }
        container.removeIfNotAlready(this);
        container.flush();
        return applicationTenancyRepository.allTenancies();
    }

    public String validateDelete(final Boolean areYouSure) {
        return not(areYouSure) ? "Please confirm this action": null;
    }

    public Boolean default0Delete() {
        return Boolean.FALSE;
    }

    static boolean not(final Boolean areYouSure) {
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
    ApplicationUserRepository applicationUserRepository;
    @javax.inject.Inject
    ApplicationTenancyRepository applicationTenancyRepository;
    @javax.inject.Inject
    DomainObjectContainer container;
    //endregion
}
