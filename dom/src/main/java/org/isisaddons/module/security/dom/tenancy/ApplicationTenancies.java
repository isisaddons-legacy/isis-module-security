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
import org.isisaddons.module.security.SecurityModule;
import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.Identifier;
import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.RestrictTo;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.query.QueryDefault;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = ApplicationTenancy.class
)
public class ApplicationTenancies extends AbstractFactoryAndRepository {

    //region > iconName

    public String iconName() {
        return "applicationTenancy";
    }

    //endregion

    //region > init

    @Programmatic
    @PostConstruct
    public void init() {
        if(applicationTenancyFactory == null) {
            applicationTenancyFactory = new ApplicationTenancyFactory.Default(getContainer());
        }
    }

    //endregion

    //region > findTenancyByName

    @Programmatic
    public ApplicationTenancy findTenancyByName(
            final String name) {
        return uniqueMatch(new QueryDefault<>(ApplicationTenancy.class, "findByName", "name", name));
    }
    //endregion

    //region > findTenancyByPath

    @Programmatic
    public ApplicationTenancy findTenancyByPath(
            final String path) {
        if(path == null) {
            return null;
        }
        return uniqueMatch(new QueryDefault<>(ApplicationTenancy.class, "findByPath", "path", path));
    }

    //endregion

    //region > findTenancyByPath

    @Programmatic
    public List<ApplicationTenancy> findByNameOrPathMatching(
            final String regex) {
        if(regex == null) {
            return null;
        }
        return allMatches(new QueryDefault<>(ApplicationTenancy.class, "findByNameOrPathMatching", "regex", regex));
    }

    //endregion


    //region > newTenancy

    public ApplicationTenancy newTenancy(
            final String name,
            final String path,
            final ApplicationTenancy parent) {
        ApplicationTenancy tenancy = findTenancyByName(name);
        if (tenancy == null){
            tenancy = applicationTenancyFactory.newApplicationTenancy();
            tenancy.setName(name);
            tenancy.setPath(path);
            tenancy.setParent(parent);
            persist(tenancy);
        }
        return tenancy;
    }

    //endregion

    //region > allTenancies
    public List<ApplicationTenancy> allTenancies() {
        return allInstances(ApplicationTenancy.class);
    }

    //endregion

    //region > autoComplete
    @Programmatic // not part of metamodel
    public List<ApplicationTenancy> autoComplete(final String name) {
        return allMatches(new QueryDefault<>(
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
    //endregion

}
