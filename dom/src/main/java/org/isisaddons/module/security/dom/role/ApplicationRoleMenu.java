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

import org.apache.isis.applib.Identifier;
import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.isisaddons.module.security.SecurityModule;

@DomainService(
        nature = NatureOfService.VIEW_MENU_ONLY
)
@DomainServiceLayout(
        named = "Security",
        menuBar = DomainServiceLayout.MenuBar.SECONDARY,
        menuOrder = "100.20"
)
public class ApplicationRoleMenu extends ApplicationRoles {

    //region > domain event classes
    public static abstract class PropertyDomainEvent<T> extends SecurityModule.PropertyDomainEvent<ApplicationRoleMenu, T> {
        public PropertyDomainEvent(final ApplicationRoleMenu source, final Identifier identifier) {
            super(source, identifier);
        }

        public PropertyDomainEvent(final ApplicationRoleMenu source, final Identifier identifier, final T oldValue, final T newValue) {
            super(source, identifier, oldValue, newValue);
        }
    }

    public static abstract class CollectionDomainEvent<T> extends SecurityModule.CollectionDomainEvent<ApplicationRoleMenu, T> {
        public CollectionDomainEvent(final ApplicationRoleMenu source, final Identifier identifier, final Of of) {
            super(source, identifier, of);
        }

        public CollectionDomainEvent(final ApplicationRoleMenu source, final Identifier identifier, final Of of, final T value) {
            super(source, identifier, of, value);
        }
    }

    public static abstract class ActionDomainEvent extends SecurityModule.ActionDomainEvent<ApplicationRoleMenu> {
        public ActionDomainEvent(final ApplicationRoleMenu source, final Identifier identifier) {
            super(source, identifier);
        }

        public ActionDomainEvent(final ApplicationRoleMenu source, final Identifier identifier, final Object... arguments) {
            super(source, identifier, arguments);
        }

        public ActionDomainEvent(final ApplicationRoleMenu source, final Identifier identifier, final List<Object> arguments) {
            super(source, identifier, arguments);
        }
    }
    //endregion

    public static class FindRolesDomainEvent extends ActionDomainEvent {
        public FindRolesDomainEvent(final ApplicationRoleMenu source, final Identifier identifier, final Object... args) {
            super(source, identifier, args);
        }
    }

    @Action(
            domainEvent = FindRolesDomainEvent.class,
            semantics = SemanticsOf.SAFE
    )
    @ActionLayout(
            cssClassFa = "fa-search"
    )
    @MemberOrder(sequence = "100.20.1")
    public List<ApplicationRole> findRoles(
            @Parameter(maxLength = ApplicationRole.MAX_LENGTH_NAME)
            @ParameterLayout(named = "Search", typicalLength = ApplicationRole.TYPICAL_LENGTH_NAME)
            final String search) {
        return applicationRoleRepository.findNameContaining(search);
    }

}
