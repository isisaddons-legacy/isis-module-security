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
import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.annotation.*;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.query.QueryDefault;

@Named("User Tenancies")
@DomainService(menuOrder = "90.5", repositoryFor = ApplicationTenancy.class)
public class ApplicationTenancies extends AbstractFactoryAndRepository {

    public String iconName() {
        return "applicationTenancy";
    }

    @MemberOrder(sequence = "90.1")
    @ActionSemantics(Of.SAFE)
    public ApplicationTenancy findTenancyByName(
            final @Named("Name") @TypicalLength(ApplicationTenancy.TYPICAL_LENGTH_NAME) @MaxLength(ApplicationTenancy.MAX_LENGTH_NAME) String name) {
        return uniqueMatch(new QueryDefault<>(ApplicationTenancy.class, "findByName", "name", name));
    }

    @MemberOrder(sequence = "90.2")
    @ActionSemantics(Of.IDEMPOTENT)
    public ApplicationTenancy newTenancy(
            final @Named("Name") @TypicalLength(ApplicationTenancy.TYPICAL_LENGTH_NAME) @MaxLength(ApplicationTenancy.MAX_LENGTH_NAME) String name) {
        ApplicationTenancy tenancy = findTenancyByName(name);
        if (tenancy == null){
            tenancy = newTransientInstance(ApplicationTenancy.class);
            tenancy.setName(name);
            persist(tenancy);
        }
        return tenancy;
    }

    @MemberOrder(sequence = "90.3")
    @ActionSemantics(Of.SAFE)
    public List<ApplicationTenancy> allTenancies() {
        return allInstances(ApplicationTenancy.class);
    }

}
