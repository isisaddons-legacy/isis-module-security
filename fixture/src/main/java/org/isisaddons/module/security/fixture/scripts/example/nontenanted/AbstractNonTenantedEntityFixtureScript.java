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
package org.isisaddons.module.security.fixture.scripts.example.nontenanted;

import org.isisaddons.module.security.fixture.dom.example.nontenanted.NonTenantedEntities;
import org.isisaddons.module.security.fixture.dom.example.nontenanted.NonTenantedEntity;
import org.apache.isis.applib.fixturescripts.FixtureScript;

public abstract class AbstractNonTenantedEntityFixtureScript extends FixtureScript {

    protected NonTenantedEntity create(
            final String name,
            final ExecutionContext executionContext) {
        final NonTenantedEntity entity = exampleNonTenantedEntities.create(name);
        executionContext.addResult(this, name, entity);
        return entity;
    }

    @javax.inject.Inject
    private NonTenantedEntities exampleNonTenantedEntities;

}
