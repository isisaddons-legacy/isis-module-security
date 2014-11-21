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
import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.Identifier;
import org.apache.isis.applib.annotation.ActionInteraction;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.MaxLength;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.TypicalLength;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.eventbus.ActionInteractionEvent;

@Named("User Tenancies")
@DomainService(menuOrder = "90.5", repositoryFor = ApplicationTenancy.class)
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

    public static class FindTenancyByNameEvent extends ActionInteractionEvent<ApplicationTenancies> {
        public FindTenancyByNameEvent(ApplicationTenancies source, Identifier identifier, Object... args) {
            super(source, identifier, args);
        }
    }

    @ActionInteraction(FindTenancyByNameEvent.class)
    @MemberOrder(sequence = "90.1")
    @ActionSemantics(Of.SAFE)
    public ApplicationTenancy findTenancyByName(
            final @Named("Name") @TypicalLength(ApplicationTenancy.TYPICAL_LENGTH_NAME) @MaxLength(ApplicationTenancy.MAX_LENGTH_NAME) String name) {
        return uniqueMatch(new QueryDefault<>(ApplicationTenancy.class, "findByName", "name", name));
    }

    //endregion

    //region > newTenancy

    public static class NewTenancyEvent extends ActionInteractionEvent<ApplicationTenancies> {
        public NewTenancyEvent(ApplicationTenancies source, Identifier identifier, Object... args) {
            super(source, identifier, args);
        }
    }

    @ActionInteraction(NewTenancyEvent.class)
    @MemberOrder(sequence = "90.2")
    @ActionSemantics(Of.IDEMPOTENT)
    public ApplicationTenancy newTenancy(
            final @Named("Name") @TypicalLength(ApplicationTenancy.TYPICAL_LENGTH_NAME) @MaxLength(ApplicationTenancy.MAX_LENGTH_NAME) String name) {
        ApplicationTenancy tenancy = findTenancyByName(name);
        if (tenancy == null){
            tenancy = applicationTenancyFactory.newApplicationTenancy();
            tenancy.setName(name);
            persist(tenancy);
        }
        return tenancy;
    }

    //endregion

    //region > allTenancies

    public static class AllTenanciesEvent extends ActionInteractionEvent<ApplicationTenancies> {
        public AllTenanciesEvent(ApplicationTenancies source, Identifier identifier, Object... args) {
            super(source, identifier, args);
        }
    }

    @ActionInteraction(AllTenanciesEvent.class)
    @MemberOrder(sequence = "90.3")
    @ActionSemantics(Of.SAFE)
    public List<ApplicationTenancy> allTenancies() {
        return allInstances(ApplicationTenancy.class);
    }

    //endregion

    //region > injected
    /**
     * Will only be injected to if the programmer has supplied an implementation.  Otherwise
     * this class will install a default implementation in {@link #postConstruct()}.
     */
    @Inject
    ApplicationTenancyFactory applicationTenancyFactory;
    //endregion

}
