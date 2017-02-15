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

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.objectstore.jdo.applib.service.JdoColumnLength;

import org.isisaddons.module.security.SecurityModule;

@DomainService(
        nature = NatureOfService.VIEW_MENU_ONLY,
        objectType = "isissecurity.ApplicationRoleMenu"
)
@DomainServiceLayout(
        named = "Security",
        menuBar = DomainServiceLayout.MenuBar.SECONDARY,
        menuOrder = "100.20"
)
public class  ApplicationRoleMenu {

    //region > domain event classes
    public static class PropertyDomainEvent<T> extends SecurityModule.PropertyDomainEvent<ApplicationRoleMenu, T> {}

    public static abstract class CollectionDomainEvent<T> extends SecurityModule.CollectionDomainEvent<ApplicationRoleMenu, T> {}

    public static abstract class ActionDomainEvent extends SecurityModule.ActionDomainEvent<ApplicationRoleMenu> {}
    //endregion

    //region > iconName
    public String iconName() {
        return "applicationRole";
    }
    //endregion

    //region > findRoles
    public static class FindRolesDomainEvent extends ActionDomainEvent {}

    @Action(
            domainEvent = FindRolesDomainEvent.class,
            semantics = SemanticsOf.SAFE
    )
    @MemberOrder(sequence = "100.20.1")
    public List<ApplicationRole> findRoles(
            @Parameter(maxLength = ApplicationRole.MAX_LENGTH_NAME)
            @ParameterLayout(named = "Search", typicalLength = ApplicationRole.TYPICAL_LENGTH_NAME)
            final String search) {
        return applicationRoleRepository.findNameContaining(search);
    }
    //endregion

    //region > newRole
    public static class NewRoleDomainEvent extends ActionDomainEvent {}

    @Action(
            domainEvent = NewRoleDomainEvent.class,
            semantics = SemanticsOf.IDEMPOTENT
    )
    @MemberOrder(sequence = "100.20.2")
    public ApplicationRole newRole(
            @Parameter(maxLength = ApplicationRole.MAX_LENGTH_NAME)
            @ParameterLayout(named="Name", typicalLength=ApplicationRole.TYPICAL_LENGTH_NAME)
            final String name,
            @Parameter(maxLength = JdoColumnLength.DESCRIPTION, optionality = Optionality.OPTIONAL)
            @ParameterLayout(named="Description", typicalLength=ApplicationRole.TYPICAL_LENGTH_DESCRIPTION)
            final String description) {
        return applicationRoleRepository.newRole(name, description);
    }
    //endregion

    //region > allRoles
    public static class AllRolesDomainEvent extends ActionDomainEvent {}

    @Action(
            domainEvent = AllRolesDomainEvent.class,
            semantics = SemanticsOf.SAFE
    )
    @MemberOrder(sequence = "100.20.3")
    public List<ApplicationRole> allRoles() {
        return applicationRoleRepository.allRoles();
    }
    //endregion

    @Inject
    ApplicationRoleRepository applicationRoleRepository;
}
