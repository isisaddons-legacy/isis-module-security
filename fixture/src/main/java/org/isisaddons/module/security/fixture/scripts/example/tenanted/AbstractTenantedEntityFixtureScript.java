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
package org.isisaddons.module.security.fixture.scripts.example.tenanted;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyRepository;
import org.isisaddons.module.security.fixture.dom.example.tenanted.TenantedEntities;
import org.isisaddons.module.security.fixture.dom.example.tenanted.TenantedEntity;

public abstract class AbstractTenantedEntityFixtureScript extends FixtureScript {

    protected TenantedEntity create(
            final String name,
            final String tenancyPath,
            final ExecutionContext executionContext) {
        final ApplicationTenancy tenancy = applicationTenancyRepository.findByPath(tenancyPath);
        final TenantedEntity entity = exampleTenantedEntities.create(name, tenancy);
        executionContext.addResult(this, name, entity);
        return entity;
    }

    @javax.inject.Inject
    private TenantedEntities exampleTenantedEntities;

    @javax.inject.Inject
    private ApplicationTenancyRepository applicationTenancyRepository;

}
