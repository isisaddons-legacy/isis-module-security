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

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.MinLength;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.RestrictTo;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.isisaddons.module.security.SecurityModule;

@DomainService(
        nature = NatureOfService.VIEW_MENU_ONLY,
        objectType = "isissecurity.ApplicationTenancyMenu"
)
@DomainServiceLayout(
        named = "Security",
        menuBar = DomainServiceLayout.MenuBar.SECONDARY,
        menuOrder = "100.30"
)
public class ApplicationTenancyMenu {

    //region > domain event classes
    public static abstract class PropertyDomainEvent<T> extends SecurityModule.PropertyDomainEvent<ApplicationTenancies, T> {
    }

    public static abstract class CollectionDomainEvent<T> extends SecurityModule.CollectionDomainEvent<ApplicationTenancies, T> {
    }

    public static abstract class ActionDomainEvent extends SecurityModule.ActionDomainEvent<ApplicationTenancies> {
    }
    //endregion

    //region > iconName
    public String iconName() {
        return "applicationTenancy";
    }
    //endregion

    //region > findTenancies
    public static class FindTenanciesDomainEvent extends ActionDomainEvent {
    }

    @Action(
            domainEvent = FindTenanciesDomainEvent.class,
            semantics = SemanticsOf.SAFE
    )
    @MemberOrder(sequence = "100.30.1")
    public List<ApplicationTenancy> findTenancies(
            @Parameter(optionality = Optionality.OPTIONAL)
            @ParameterLayout(named = "Partial Name Or Path", describedAs = "String to search for, wildcard (*) can be used")
            @MinLength(1) // for auto-complete
            final String partialNameOrPath) {
        return applicationTenancyRepository.findByNameOrPathMatchingCached(partialNameOrPath);
    }
    //endregion

    //region > newTenancy
    public static class NewTenancyDomainEvent extends ActionDomainEvent {
    }

    @Action(
            domainEvent = NewTenancyDomainEvent.class,
            semantics = SemanticsOf.IDEMPOTENT
    )
    @MemberOrder(sequence = "100.30.3")
    public ApplicationTenancy newTenancy(
            @Parameter(maxLength = ApplicationTenancy.MAX_LENGTH_NAME)
            @ParameterLayout(named = "Name", typicalLength = ApplicationTenancy.TYPICAL_LENGTH_NAME)
            final String name,
            @Parameter(maxLength = ApplicationTenancy.MAX_LENGTH_PATH)
            @ParameterLayout(named = "Path")
            final String path,
            @Parameter(optionality = Optionality.OPTIONAL)
            @ParameterLayout(named = "Parent")
            final ApplicationTenancy parent) {
        return applicationTenancyRepository.newTenancy(name, path, parent);
    }
    //endregion

    //region > allTenancies
    public static class AllTenanciesDomainEvent extends ActionDomainEvent {
    }

    @Action(
            domainEvent = AllTenanciesDomainEvent.class,
            semantics = SemanticsOf.SAFE,
            restrictTo = RestrictTo.PROTOTYPING
    )
    @MemberOrder(sequence = "100.30.4")
    public List<ApplicationTenancy> allTenancies() {
        return applicationTenancyRepository.allTenancies();
    }
    //endregion

    //region > injected
    @Inject
    ApplicationTenancyRepository applicationTenancyRepository;
    //endregion

}
