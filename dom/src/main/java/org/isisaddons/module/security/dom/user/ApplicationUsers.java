/*
 *  Copyright 2014 Dan Haywood
 *
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
import java.util.concurrent.Callable;
import javax.inject.Inject;
import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.annotation.*;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;

@Named("Users")
@DomainService(menuOrder = "90.1", repositoryFor = ApplicationUser.class)
public class ApplicationUsers extends AbstractFactoryAndRepository {

    //region > identification
    public String iconName() {
        return "applicationUser";
    }
    //endregion

    /**
     * Uses the {@link org.apache.isis.applib.services.queryresultscache.QueryResultsCache} in order to support
     * multiple lookups from <code>org.isisaddons.module.security.app.user.UserPermissionViewModel</code>.
     *
     * <p>
     *     If the user does not exist, it will be automatically created.
     * </p>
     */
    @MemberOrder(sequence = "10.2")
    @ActionSemantics(Of.IDEMPOTENT)
    public ApplicationUser findUserByUsername(
            final @Named("Username") String username) {
        return queryResultsCache.execute(new Callable<ApplicationUser>() {
            @Override
            public ApplicationUser call() throws Exception {
                ApplicationUser applicationUser = findUserByUsernameNoAutocreate(username);
                if (applicationUser != null) {
                    return applicationUser;
                }
                return newUser(username, null);
            }
        }, ApplicationUsers.class, "findUserByUsername", username );
    }

    /**
     * Uses the {@link org.apache.isis.applib.services.queryresultscache.QueryResultsCache} in order to support
     * multiple lookups from <code>org.isisaddons.module.security.app.user.UserPermissionViewModel</code>.
     */
    @Programmatic
    public ApplicationUser findUserByUsernameNoAutocreate(
            final @Named("Username") String username) {
        return uniqueMatch(new QueryDefault<>(
                ApplicationUser.class,
                "findByUsername", "username", username));
    }

    @MemberOrder(sequence = "10.3")
    @ActionSemantics(Of.SAFE)
    public List<ApplicationUser> findUsersByName(
            final @Named("Name") String name) {
        final String nameRegex = "(?i).*" + name + ".*";
        return allMatches(new QueryDefault<>(
                ApplicationUser.class,
                "findByName", "nameRegex", nameRegex));
    }

    @MemberOrder(sequence = "10.4")
    @ActionSemantics(Of.NON_IDEMPOTENT)
    public ApplicationUser newUser(
            final @Named("Name") String username,
            final @Named("Enabled?") @Optional Boolean enabled) {
        ApplicationUser user = newTransientInstance(ApplicationUser.class);
        user.setUsername(username);
        user.setStatus(ApplicationUserStatus.parse(enabled));
        persist(user);
        return user;
    }

    @MemberOrder(sequence = "10.9")
    @ActionSemantics(Of.SAFE)
    public List<ApplicationUser> allUsers() {
        return allInstances(ApplicationUser.class);
    }

    @Programmatic // not part of metamodel
    public List<ApplicationUser> autoComplete(final String name) {
        return findUsersByName(name);
    }



    //region  >  (injected)
    @Inject
    QueryResultsCache queryResultsCache;
    //endregion

}
