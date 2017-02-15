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

import java.util.concurrent.Callable;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;
import org.apache.isis.applib.services.user.UserService;

import org.isisaddons.module.security.SecurityModule;
import org.isisaddons.module.security.dom.user.ApplicationUser;
import org.isisaddons.module.security.dom.user.ApplicationUserRepository;

@SuppressWarnings("UnusedDeclaration")
@DomainService(
        nature = NatureOfService.VIEW_MENU_ONLY,
        objectType = "isissecurity.MeService"
)
@DomainServiceLayout(
        menuBar = DomainServiceLayout.MenuBar.TERTIARY,
        menuOrder = "100"
)
public class MeService {

    public static abstract class PropertyDomainEvent<T> extends SecurityModule.PropertyDomainEvent<MeService, T> {}

    public static abstract class CollectionDomainEvent<T> extends SecurityModule.CollectionDomainEvent<MeService, T> {}

    public static abstract class ActionDomainEvent extends SecurityModule.ActionDomainEvent<MeService> {}

    // //////////////////////////////////////

    //region > iconName
    public String iconName() {
        return "applicationUser";
    }
    //endregion

    // //////////////////////////////////////

    //region > me (action)
    public static class MeDomainEvent extends ActionDomainEvent {}

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
        return queryResultsCache.execute(new Callable<ApplicationUser>() {
            @Override
            public ApplicationUser call() throws Exception {
                return doMe();
            }
        }, MeService.class, "me");
    }

    protected ApplicationUser doMe() {
        final String myName = userService.getUser().getName();
        return applicationUserRepository.findOrCreateUserByUsername(myName);
    }

    protected ApplicationUser doMe(final String myName) {
        return applicationUserRepository.findOrCreateUserByUsername(myName);
    }

    //endregion

    //region  > services (injected)
    @javax.inject.Inject
    ApplicationUserRepository applicationUserRepository;
    @javax.inject.Inject
    UserService userService;
    @javax.inject.Inject
    QueryResultsCache queryResultsCache;
    //endregion

}
