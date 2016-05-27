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
import java.util.concurrent.Callable;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = ApplicationRole.class
)
public class ApplicationRoleRepository  {


    //region > findByName

    @Programmatic
    public ApplicationRole findByNameCached(final String name) {
        return queryResultsCache.execute(new Callable<ApplicationRole>() {
            @Override public ApplicationRole call() throws Exception {
                return findByName(name);
            }
        }, ApplicationRoleRepository.class, "findByNameCached", name);
    }

    @Programmatic
    public ApplicationRole findByName(final String name) {
        if(name == null) {
            return null;
        }
        return container.uniqueMatch(new QueryDefault<>(ApplicationRole.class, "findByName", "name", name));
    }

    @Programmatic
    public List<ApplicationRole> findNameContaining(final String search) {
        if(search != null && search.length() > 0) {
            String nameRegex = String.format("(?i).*%s.*", search.replace("*", ".*").replace("?", "."));
            return container.allMatches(new QueryDefault<>(ApplicationRole.class, "findByNameContaining", "nameRegex", nameRegex));
        }
        return Lists.newArrayList();
    }

    //endregion

    //region > newRole

    @Programmatic
    public ApplicationRole newRole(
            final String name,
            final String description) {
        ApplicationRole role = findByName(name);
        if (role == null){
            role = getApplicationRoleFactory().newApplicationRole();
            role.setName(name);
            role.setDescription(description);
            container.persist(role);
        }
        return role;
    }

    //endregion

    //region > allRoles

    @Programmatic
    public List<ApplicationRole> allRoles() {
        return container.allInstances(ApplicationRole.class);
    }

    //endregion

    //region > injected
    @Inject
    DomainObjectContainer container;

    /**
     * Will only be injected to if the programmer has supplied an implementation.  Otherwise
     * this class will install a default implementation in {@link #getApplicationRoleFactory()}.
     */
    @Inject
    ApplicationRoleFactory applicationRoleFactory;

    private ApplicationRoleFactory getApplicationRoleFactory() {
        return applicationRoleFactory != null
                ? applicationRoleFactory
                : (applicationRoleFactory = new ApplicationRoleFactory.Default(container));
    }

    @Inject
    QueryResultsCache queryResultsCache;
    //endregion

    //region > findByName

    @Action(semantics = SemanticsOf.SAFE)
    public List<ApplicationRole> findMatching(String search) {
        if (search != null && search.length() > 0 ) {
            return findNameContaining(search);
        }
        return Lists.newArrayList();
    }

    //endregion
}
