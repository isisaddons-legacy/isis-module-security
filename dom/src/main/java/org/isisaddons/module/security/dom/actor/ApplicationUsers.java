/*
 *  Copyright 2014 Jeroen van der Wal
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
package org.isisaddons.module.security.dom.actor;

import java.util.List;
import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.annotation.*;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.query.QueryDefault;

@Named("Application Users")
@DomainService(repositoryFor = ApplicationUser.class)
public class ApplicationUsers extends AbstractFactoryAndRepository {

    public String iconName() {
        return "applicationUser";
    }

    @MemberOrder(name = "Security", sequence = "10.1")
    @DescribedAs("Looks up ApplicationUser entity corresponding to your user account")
    @ActionSemantics(Of.SAFE)
    public ApplicationUser me() {
        final String myName = getContainer().getUser().getName();
        return findUserByUsername(myName);
    }

    @MemberOrder(name = "Security", sequence = "10.2")
    @ActionSemantics(Of.SAFE)
    public ApplicationUser findUserByUsername(final String username) {
        return uniqueMatch(new QueryDefault<>(
                ApplicationUser.class,
                "findByUsername", "username", username));
    }

    @MemberOrder(name = "Security", sequence = "10.3")
    @ActionSemantics(Of.SAFE)
    public List<ApplicationUser> findUsersByName(final String name) {
        final String nameRegex = "(?i).*" + name + ".*";
        return allMatches(new QueryDefault<>(
                ApplicationUser.class,
                "findByName", "nameRegex", nameRegex));
    }

    @MemberOrder(name = "Security", sequence = "10.4")
    @ActionSemantics(Of.NON_IDEMPOTENT)
    public ApplicationUser newUser(@Named("Name") String name) {
        ApplicationUser user = newTransientInstance(ApplicationUser.class);
        user.setUsername(name);
        persist(user);
        return user;
    }

    @MemberOrder(name = "Security", sequence = "10.9")
    @Prototype
    @ActionSemantics(Of.SAFE)
    public List<ApplicationUser> allUsers() {
        return allInstances(ApplicationUser.class);
    }

    @Programmatic // not part of metamodel
    public List<ApplicationUser> autoComplete(final String name) {
        return allMatches(
                new QueryDefault<ApplicationUser>(ApplicationUser.class,
                        "findByNameContaining",
                        "name", name));
    }


}