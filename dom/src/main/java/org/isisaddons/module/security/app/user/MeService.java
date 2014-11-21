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

import org.isisaddons.module.security.dom.user.ApplicationUser;
import org.isisaddons.module.security.dom.user.ApplicationUsers;
import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.Identifier;
import org.apache.isis.applib.annotation.ActionInteraction;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.DescribedAs;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.services.eventbus.ActionInteractionEvent;

public class MeService extends AbstractFactoryAndRepository {

    //region > iconName

    public String iconName() {
        return "applicationUser";
    }
    //endregion

    //region > me (action)

    public static class MeEvent extends ActionInteractionEvent<MeService> {
        public MeEvent(MeService source, Identifier identifier, Object... args) {
            super(source, identifier, args);
        }
    }

    @ActionInteraction(MeEvent.class)
    @MemberOrder(name = "Users", sequence = "1")
    @DescribedAs("Looks up ApplicationUser entity corresponding to your user account")
    @ActionSemantics(Of.SAFE)
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
