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

import javax.inject.Inject;

import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.Identifier;
import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.HasUsername;
import org.apache.isis.applib.services.i18n.TranslatableString;

import org.isisaddons.module.security.SecurityModule;

@DomainService(
        nature = NatureOfService.VIEW_CONTRIBUTIONS_ONLY
)
public class HasUsernameContributions extends AbstractFactoryAndRepository {

    public static abstract class ActionDomainEvent extends SecurityModule.ActionDomainEvent<HasUsernameContributions> {
        public ActionDomainEvent(final HasUsernameContributions source, final Identifier identifier) {
            super(source, identifier);
        }

        public ActionDomainEvent(final HasUsernameContributions source, final Identifier identifier, final Object... arguments) {
            super(source, identifier, arguments);
        }

        public ActionDomainEvent(final HasUsernameContributions source, final Identifier identifier, final List<Object> arguments) {
            super(source, identifier, arguments);
        }
    }


    @Action(
            semantics = SemanticsOf.SAFE
    )
    @ActionLayout(
            contributed = Contributed.AS_ACTION
    )
    @MemberOrder(name = "User", sequence = "1") // associate with a 'User' property (if any)
    public ApplicationUser open(final HasUsername hasUsername) {
        if (hasUsername == null || hasUsername.getUsername() == null) {
            return null;
        }
        return applicationUserRepository.findByUsername(hasUsername.getUsername());
    }
    public boolean hideOpen(final HasUsername hasUsername) {
        return hasUsername instanceof ApplicationUser;
    }
    public TranslatableString disableOpen(final HasUsername hasUsername) {
        if (hasUsername == null || hasUsername.getUsername() == null) {
            return TranslatableString.tr("No username");
        }
        return null;
    }


    @Inject
    private ApplicationUserRepository applicationUserRepository;

}
