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
package org.isisaddons.module.security.dom.role;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.isisaddons.module.security.SecurityModule;
import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.Identifier;
import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.objectstore.jdo.applib.service.JdoColumnLength;

@DomainService(repositoryFor = ApplicationRole.class)
@DomainServiceLayout(
        named="Security",
        menuBar = DomainServiceLayout.MenuBar.SECONDARY,
        menuOrder = "100.20"
)
public class ApplicationRoles extends AbstractFactoryAndRepository {

    public static abstract class PropertyDomainEvent<T> extends SecurityModule.PropertyDomainEvent<ApplicationRoles, T> {
        public PropertyDomainEvent(final ApplicationRoles source, final Identifier identifier) {
            super(source, identifier);
        }

        public PropertyDomainEvent(final ApplicationRoles source, final Identifier identifier, final T oldValue, final T newValue) {
            super(source, identifier, oldValue, newValue);
        }
    }

    public static abstract class CollectionDomainEvent<T> extends SecurityModule.CollectionDomainEvent<ApplicationRoles, T> {
        public CollectionDomainEvent(final ApplicationRoles source, final Identifier identifier, final Of of) {
            super(source, identifier, of);
        }

        public CollectionDomainEvent(final ApplicationRoles source, final Identifier identifier, final Of of, final T value) {
            super(source, identifier, of, value);
        }
    }

    public static abstract class ActionDomainEvent extends SecurityModule.ActionDomainEvent<ApplicationRoles> {
        public ActionDomainEvent(final ApplicationRoles source, final Identifier identifier) {
            super(source, identifier);
        }

        public ActionDomainEvent(final ApplicationRoles source, final Identifier identifier, final Object... arguments) {
            super(source, identifier, arguments);
        }

        public ActionDomainEvent(final ApplicationRoles source, final Identifier identifier, final List<Object> arguments) {
            super(source, identifier, arguments);
        }
    }

    // //////////////////////////////////////

    //region > iconName

    public String iconName() {
        return "applicationRole";
    }

    //endregion

    //region > init

    @Programmatic
    @PostConstruct
    public void init() {
        if(applicationRoleFactory == null) {
            applicationRoleFactory = new ApplicationRoleFactory.Default(getContainer());
        }
    }

    //endregion

    //region > findRoleByName

    public static class FindByRoleNameDomainEvent extends ActionDomainEvent {
        public FindByRoleNameDomainEvent(final ApplicationRoles source, final Identifier identifier, final Object... args) {
            super(source, identifier, args);
        }
    }

    @Action(
            domainEvent = FindByRoleNameDomainEvent.class,
            semantics = SemanticsOf.SAFE
    )
    @MemberOrder(sequence = "20.1")
    public ApplicationRole findRoleByName(
            @Parameter(maxLength = ApplicationRole.MAX_LENGTH_NAME)
            @ParameterLayout(named="Name", typicalLength=ApplicationRole.TYPICAL_LENGTH_NAME)
            final String name) {
        return uniqueMatch(new QueryDefault<>(ApplicationRole.class, "findByName", "name", name));
    }

    //endregion

    //region > newRole

    public static class NewRoleDomainEvent extends ActionDomainEvent {
        public NewRoleDomainEvent(final ApplicationRoles source, final Identifier identifier, final Object... args) {
            super(source, identifier, args);
        }
    }

    @Action(
            domainEvent = NewRoleDomainEvent.class,
            semantics = SemanticsOf.IDEMPOTENT
    )
    @MemberOrder(sequence = "20.2")
    public ApplicationRole newRole(
            @Parameter(maxLength = ApplicationRole.MAX_LENGTH_NAME)
            @ParameterLayout(named="Name", typicalLength=ApplicationRole.TYPICAL_LENGTH_NAME)
            final String name,
            @Parameter(maxLength = JdoColumnLength.DESCRIPTION, optionality = Optionality.OPTIONAL)
            @ParameterLayout(named="Description", typicalLength=ApplicationRole.TYPICAL_LENGTH_DESCRIPTION)
            final String description) {
        ApplicationRole role = findRoleByName(name);
        if (role == null){
            role = applicationRoleFactory.newApplicationRole();
            role.setName(name);
            role.setDescription(description);
            persist(role);
        }
        return role;
    }

    //endregion

    //region > allRoles

    public static class AllRolesDomainEvent extends ActionDomainEvent {
        public AllRolesDomainEvent(final ApplicationRoles source, final Identifier identifier, final Object... args) {
            super(source, identifier, args);
        }
    }

    @Action(
            domainEvent = AllRolesDomainEvent.class,
            semantics = SemanticsOf.SAFE
    )
    @MemberOrder(sequence = "20.3")
    public List<ApplicationRole> allRoles() {
        return allInstances(ApplicationRole.class);
    }

    //endregion

    //region > injected
    /**
     * Will only be injected to if the programmer has supplied an implementation.  Otherwise
     * this class will install a default implementation in {@link #init()}.
     */
    @Inject
    ApplicationRoleFactory applicationRoleFactory;
    //endregion

}
