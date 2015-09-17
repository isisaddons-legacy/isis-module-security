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
package org.isisaddons.module.security.dom.tenancy;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = ApplicationTenancy.class
)
public class ApplicationTenancyRepository {


    //region > init

    @Programmatic
    @PostConstruct
    public void init() {
        if(applicationTenancyFactory == null) {
            applicationTenancyFactory = new ApplicationTenancyFactory.Default(container);
        }
    }

    //endregion

    //region > findTenancyByPath

    @Programmatic
    public List<ApplicationTenancy> findByNameOrPathMatching(
            final String regex) {
        if(regex == null) {
            return null;
        }
        return container.allMatches(new QueryDefault<>(ApplicationTenancy.class, "findByNameOrPathMatching", "regex", regex));
    }

    //endregion

    //region > findTenancyByName
    @Programmatic
    public ApplicationTenancy findTenancyByName(
            @Parameter(maxLength = ApplicationTenancy.MAX_LENGTH_NAME)
            @ParameterLayout(named = "Name", typicalLength = ApplicationTenancy.TYPICAL_LENGTH_NAME)
            final String name) {
        return container.uniqueMatch(new QueryDefault<>(ApplicationTenancy.class, "findByName", "name", name));
    }

    //endregion

    //region > findTenancyByPath

    @Programmatic
    public ApplicationTenancy findTenancyByPath(
            @Parameter(maxLength = ApplicationTenancy.MAX_LENGTH_PATH)
            @ParameterLayout(named = "Path")
            final String path) {
        if(path == null) {
            return null;
        }
        return container.uniqueMatch(new QueryDefault<>(ApplicationTenancy.class, "findByPath", "path", path));
    }

    //endregion


    //region > newTenancy

    @Programmatic
    public ApplicationTenancy newTenancy(
            @Parameter(maxLength = ApplicationTenancy.MAX_LENGTH_NAME)
            @ParameterLayout(named = "Name", typicalLength = ApplicationTenancy.TYPICAL_LENGTH_NAME)
            final String name,
            @Parameter(maxLength = ApplicationTenancy.MAX_LENGTH_PATH)
            @ParameterLayout(named = "Path")
            final String path,
            @Parameter(optionality = Optionality.OPTIONAL)
            @ParameterLayout(named = "Parent")
            final ApplicationTenancy parent) {
        ApplicationTenancy tenancy = findTenancyByName(name);
        if (tenancy == null){
            tenancy = applicationTenancyFactory.newApplicationTenancy();
            tenancy.setName(name);
            tenancy.setPath(path);
            tenancy.setParent(parent);
            container.persist(tenancy);
        }
        return tenancy;
    }

    //endregion

    //region > allTenancies
    @Programmatic
    public List<ApplicationTenancy> allTenancies() {
        return container.allInstances(ApplicationTenancy.class);
    }

    //endregion

    //region > autoComplete
    @Programmatic // not part of metamodel
    public List<ApplicationTenancy> autoComplete(final String name) {
        return container.allMatches(new QueryDefault<>(
                ApplicationTenancy.class,
                "findByNameContaining", "name", name));
    }
    //endregion

    //region > injected
    /**
     * Will only be injected to if the programmer has supplied an implementation.  Otherwise
     * this class will install a default implementation in {@link #init()}.
     */
    @Inject
    ApplicationTenancyFactory applicationTenancyFactory;
    @Inject
    DomainObjectContainer container;
    //endregion

}
