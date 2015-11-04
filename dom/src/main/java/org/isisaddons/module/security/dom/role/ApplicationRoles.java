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

import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.objectstore.jdo.applib.service.JdoColumnLength;

import org.isisaddons.module.security.SecurityModule;

/**
 * @deprecated - use {@link ApplicationRoleRepository} or {@link ApplicationRoleMenu} instead.
 */
@Deprecated
public class ApplicationRoles extends AbstractFactoryAndRepository {

    //region > domain event classes
    public static abstract class PropertyDomainEvent<T> extends SecurityModule.PropertyDomainEvent<ApplicationRoles, T> {}

    public static abstract class CollectionDomainEvent<T> extends SecurityModule.CollectionDomainEvent<ApplicationRoles, T> {}

    public static abstract class ActionDomainEvent extends SecurityModule.ActionDomainEvent<ApplicationRoles> {}
    //endregion

    //region > iconName
    public String iconName() {
        return "applicationRole";
    }
    //endregion

    //region > findRoleByName
    public static class FindByRoleNameDomainEvent extends ActionDomainEvent {}

    /**
     * @deprecated - {@link ApplicationRoleRepository#findByName(String)} instead.
     */
    @Action(
            domainEvent = FindByRoleNameDomainEvent.class,
            semantics = SemanticsOf.SAFE,
            hidden = Where.EVERYWHERE
    )
    @Deprecated
    public ApplicationRole findRoleByName(
            @Parameter(maxLength = ApplicationRole.MAX_LENGTH_NAME)
            @ParameterLayout(named="Name", typicalLength=ApplicationRole.TYPICAL_LENGTH_NAME)
            final String name) {
        return applicationRoleRepository.findByName(name);
    }
    //endregion

    //region > newRole
    public static class NewRoleDomainEvent extends ActionDomainEvent {}

    /**
     * @deprecated - use {@link ApplicationRoleMenu#newRole(String, String)} instead.
     */
    @Action(
            domainEvent = NewRoleDomainEvent.class,
            semantics = SemanticsOf.IDEMPOTENT,
            hidden = Where.EVERYWHERE
    )
    @Deprecated
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

    /**
     * @deprecated - use {@link ApplicationRoleMenu#allRoles()} instead.
     */
    @Action(
            domainEvent = AllRolesDomainEvent.class,
            semantics = SemanticsOf.SAFE,
            hidden = Where.EVERYWHERE
    )
    @Deprecated
    public List<ApplicationRole> allRoles() {
        return applicationRoleRepository.allRoles();
    }
    //endregion

    //region > injected
    @Inject
    ApplicationRoleRepository applicationRoleRepository;
    //endregion

}
