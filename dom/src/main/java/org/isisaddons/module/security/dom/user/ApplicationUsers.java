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
package org.isisaddons.module.security.dom.user;

import java.util.List;

import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.Named;

@Named("Security")
@DomainService
public class ApplicationUsers extends AbstractFactoryAndRepository {

    @ActionSemantics(Of.SAFE)
    public List<ApplicationUser> allUsers() {
        return allInstances(ApplicationUser.class);
    }

    @ActionSemantics(Of.NON_IDEMPOTENT)
    public ApplicationUser addUser(@Named("Name") String name) {
        ApplicationUser user = newTransientInstance(ApplicationUser.class);
        user.setName(name);
        persist(user);
        return user;
    }

}
