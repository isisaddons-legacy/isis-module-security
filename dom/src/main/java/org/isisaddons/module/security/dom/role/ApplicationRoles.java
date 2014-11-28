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
import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.Identifier;
import org.apache.isis.applib.annotation.ActionInteraction;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.MaxLength;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.TypicalLength;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.eventbus.ActionInteractionEvent;
import org.apache.isis.objectstore.jdo.applib.service.JdoColumnLength;

@Named("Security")
@DomainService(menuOrder = "90.2", repositoryFor = ApplicationRole.class)
public class ApplicationRoles extends AbstractFactoryAndRepository {

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

    public static class FindByRoleNameEvent extends ActionInteractionEvent<ApplicationRoles> {
        public FindByRoleNameEvent(ApplicationRoles source, Identifier identifier, Object... args) {
            super(source, identifier, args);
        }
    }

    @ActionInteraction(FindByRoleNameEvent.class)
    @MemberOrder(sequence = "20.1")
    @ActionSemantics(Of.SAFE)
    public ApplicationRole findRoleByName(
            final @Named("Name") @TypicalLength(ApplicationRole.TYPICAL_LENGTH_NAME) @MaxLength(ApplicationRole.MAX_LENGTH_NAME) String name) {
        return uniqueMatch(new QueryDefault<>(ApplicationRole.class, "findByName", "name", name));
    }

    //endregion

    //region > newRole

    public static class NewRoleEvent extends ActionInteractionEvent<ApplicationRoles> {
        public NewRoleEvent(ApplicationRoles source, Identifier identifier, Object... args) {
            super(source, identifier, args);
        }
    }

    @ActionInteraction(NewRoleEvent.class)
    @MemberOrder(sequence = "20.2")
    @ActionSemantics(Of.IDEMPOTENT)
    public ApplicationRole newRole(
            final @Named("Name") @TypicalLength(ApplicationRole.TYPICAL_LENGTH_NAME) @MaxLength(ApplicationRole.MAX_LENGTH_NAME) String name,
            final @Named("Description") @Optional @TypicalLength(ApplicationRole.TYPICAL_LENGTH_DESCRIPTION) @MaxLength(JdoColumnLength.DESCRIPTION) String description) {
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

    public static class AllRolesEvent extends ActionInteractionEvent<ApplicationRoles> {
        public AllRolesEvent(ApplicationRoles source, Identifier identifier, Object... args) {
            super(source, identifier, args);
        }
    }

    @ActionInteraction(AllRolesEvent.class)
    @MemberOrder(sequence = "20.3")
    @ActionSemantics(Of.SAFE)
    public List<ApplicationRole> allRoles() {
        return allInstances(ApplicationRole.class);
    }

    //endregion

    //region > injected
    /**
     * Will only be injected to if the programmer has supplied an implementation.  Otherwise
     * this class will install a default implementation in {@link #postConstruct()}.
     */
    @Inject
    ApplicationRoleFactory applicationRoleFactory;
    //endregion

}
