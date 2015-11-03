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
package org.isisaddons.module.security.dom.user;

import java.util.List;

import org.apache.isis.applib.Identifier;
import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.isisaddons.module.security.SecurityModule;

@DomainService(
        nature = NatureOfService.VIEW_MENU_ONLY
)
@DomainServiceLayout(
        named="Security",
        menuBar = DomainServiceLayout.MenuBar.SECONDARY,
        menuOrder = "100.10"
)
public class ApplicationUserMenu extends ApplicationUsers {

        public static abstract class PropertyDomainEvent<T> extends SecurityModule.PropertyDomainEvent<ApplicationUserMenu, T> {
                public PropertyDomainEvent(final ApplicationUserMenu source, final Identifier identifier) {
                        super(source, identifier);
                }

                public PropertyDomainEvent(final ApplicationUserMenu source, final Identifier identifier, final T oldValue, final T newValue) {
                        super(source, identifier, oldValue, newValue);
                }
        }

        public static abstract class CollectionDomainEvent<T> extends SecurityModule.CollectionDomainEvent<ApplicationUserMenu, T> {
                public CollectionDomainEvent(final ApplicationUserMenu source, final Identifier identifier, final Of of) {
                        super(source, identifier, of);
                }

                public CollectionDomainEvent(final ApplicationUserMenu source, final Identifier identifier, final Of of, final T value) {
                        super(source, identifier, of, value);
                }
        }

        public static abstract class ActionDomainEvent extends SecurityModule.ActionDomainEvent<ApplicationUserMenu> {
                public ActionDomainEvent(final ApplicationUserMenu source, final Identifier identifier) {
                        super(source, identifier);
                }

                public ActionDomainEvent(final ApplicationUserMenu source, final Identifier identifier, final Object... arguments) {
                        super(source, identifier, arguments);
                }

                public ActionDomainEvent(final ApplicationUserMenu source, final Identifier identifier, final List<Object> arguments) {
                        super(source, identifier, arguments);
                }
        }


        public static class FindUsersByNameDomainEvent extends ActionDomainEvent {
                public FindUsersByNameDomainEvent(final ApplicationUserMenu source, final Identifier identifier, final Object... args) {
                        super(source, identifier, args);
                }
        }

        @Action(
                domainEvent = FindUsersByNameDomainEvent.class,
                semantics = SemanticsOf.SAFE
        )
        @MemberOrder(sequence = "100.10.2")
        public List<ApplicationUser> findUsers(
                final @ParameterLayout(named="Search") String search) {
                return applicationUserRepository.find(search);
        }

}
