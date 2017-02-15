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
package org.isisaddons.module.security.fixture.dom.example.tenanted;

import java.util.List;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.isisaddons.module.security.fixture.dom.example.nontenanted.NonTenantedEntity;

@DomainService(
        nature = NatureOfService.VIEW_MENU_ONLY,
        objectType = "isissecurityDemo.TenantedEntities",
        repositoryFor = TenantedEntity.class
)
@DomainServiceLayout(menuOrder = "20")
public class TenantedEntities {

    @Programmatic
    public String getId() {
        return "TenantedEntities";
    }

    public String iconName() {
        return "TenantedEntity";
    }



    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(bookmarking = BookmarkPolicy.AS_ROOT)
    @MemberOrder(sequence = "1")
    public List<TenantedEntity> listAll() {
        return container.allInstances(TenantedEntity.class);
    }



    @MemberOrder(sequence = "2")
    public TenantedEntity create(
            @Parameter(maxLength = NonTenantedEntity.MAX_LENGTH_NAME)
            @ParameterLayout(named="Name")
            final String name,
            final ApplicationTenancy tenancy) {
        final TenantedEntity obj = new TenantedEntity(name, null, tenancy.getPath());
        container.persistIfNotAlready(obj);
        return obj;
    }


    @javax.inject.Inject 
    DomainObjectContainer container;

}
