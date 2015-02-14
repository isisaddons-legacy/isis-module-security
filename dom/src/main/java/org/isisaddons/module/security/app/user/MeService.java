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
package org.isisaddons.module.security.app.user;

import java.util.List;
import org.isisaddons.module.security.SecurityModule;
import org.isisaddons.module.security.dom.user.ApplicationUser;
import org.isisaddons.module.security.dom.user.ApplicationUsers;
import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.Identifier;
import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.SemanticsOf;

@SuppressWarnings("UnusedDeclaration")
@DomainService()
@DomainServiceLayout(
        menuBar = DomainServiceLayout.MenuBar.TERTIARY,
        menuOrder = "100"
)
public class MeService extends AbstractFactoryAndRepository {

    public static abstract class PropertyDomainEvent<T> extends SecurityModule.PropertyDomainEvent<MeService, T> {
        public PropertyDomainEvent(final MeService source, final Identifier identifier) {
            super(source, identifier);
        }

        public PropertyDomainEvent(final MeService source, final Identifier identifier, final T oldValue, final T newValue) {
            super(source, identifier, oldValue, newValue);
        }
    }

    public static abstract class CollectionDomainEvent<T> extends SecurityModule.CollectionDomainEvent<MeService, T> {
        public CollectionDomainEvent(final MeService source, final Identifier identifier, final Of of) {
            super(source, identifier, of);
        }

        public CollectionDomainEvent(final MeService source, final Identifier identifier, final Of of, final T value) {
            super(source, identifier, of, value);
        }
    }

    public static abstract class ActionDomainEvent extends SecurityModule.ActionDomainEvent<MeService> {
        public ActionDomainEvent(final MeService source, final Identifier identifier) {
            super(source, identifier);
        }

        public ActionDomainEvent(final MeService source, final Identifier identifier, final Object... arguments) {
            super(source, identifier, arguments);
        }

        public ActionDomainEvent(final MeService source, final Identifier identifier, final List<Object> arguments) {
            super(source, identifier, arguments);
        }
    }

    // //////////////////////////////////////

    //region > iconName
    public String iconName() {
        return "applicationUser";
    }
    //endregion

    // //////////////////////////////////////

    //region > me (action)
    public static class MeDomainEvent extends ActionDomainEvent {
        public MeDomainEvent(final MeService source, final Identifier identifier) {
            super(source, identifier);
        }
        public MeDomainEvent(final MeService source, final Identifier identifier, final Object... arguments) {
            super(source, identifier, arguments);
        }
        public MeDomainEvent(final MeService source, final Identifier identifier, final List<Object> arguments) {
            super(source, identifier, arguments);
        }
    }

    @Action(
            domainEvent = MeDomainEvent.class,
            semantics = SemanticsOf.SAFE
    )
    @ActionLayout(
            cssClassFa = "fa-user",
            describedAs = "Looks up ApplicationUser entity corresponding to your user account"
    )
    @MemberOrder(name = "Security", sequence = "100")
    public ApplicationUser me() {
        final String myName = getContainer().getUser().getName();
        return applicationUsers.findOrCreateUserByUsername(myName);
    }

    //endregion

    //region  > services (injected)
    @javax.inject.Inject
    ApplicationUsers applicationUsers;
    //endregion

}
