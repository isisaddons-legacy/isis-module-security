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
package org.isisaddons.module.security.fixture.scripts;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.objectstore.jdo.applib.service.support.IsisJdoSupport;

public class SecurityModuleAppTearDown extends FixtureScript {

    @Override
    protected void execute(ExecutionContext executionContext) {
        isisJdoSupport.executeUpdate("delete from IsisAddonsSecurity.\"ApplicationPermission\"");
        isisJdoSupport.executeUpdate("delete from IsisAddonsSecurity.\"ApplicationUserRoles\"");
        isisJdoSupport.executeUpdate("delete from IsisAddonsSecurity.\"ApplicationRole\"");
        isisJdoSupport.executeUpdate("delete from IsisAddonsSecurity.\"ApplicationUser\"");
        isisJdoSupport.executeUpdate("delete from IsisAddonsSecurity.\"ApplicationTenancy\"");

        isisJdoSupport.executeUpdate("delete from \"NonTenantedEntity\"");
        isisJdoSupport.executeUpdate("delete from \"TenantedEntity\"");
    }


    @javax.inject.Inject
    private IsisJdoSupport isisJdoSupport;

}
