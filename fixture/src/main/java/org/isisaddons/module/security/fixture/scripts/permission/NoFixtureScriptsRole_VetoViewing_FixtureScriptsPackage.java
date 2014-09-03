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
package org.isisaddons.module.security.fixture.scripts.permission;

import javax.inject.Inject;
import org.isisaddons.module.security.dom.permission.ApplicationPermissionMode;
import org.isisaddons.module.security.dom.permission.ApplicationPermissionRule;
import org.isisaddons.module.security.dom.role.ApplicationRole;
import org.isisaddons.module.security.dom.role.ApplicationRoles;
import org.isisaddons.module.security.fixture.scripts.roles.NoFixtureScriptsRoleFixture;
import org.apache.isis.applib.fixturescripts.FixtureScript;

public class NoFixtureScriptsRole_VetoViewing_FixtureScriptsPackage extends FixtureScript {

    @Override
    protected void execute(ExecutionContext executionContext) {

        final ApplicationRole role = applicationRoles.findRoleByName(NoFixtureScriptsRoleFixture.ROLE_NAME);

        executionContext.add(this, role.addPackage(ApplicationPermissionRule.VETO, ApplicationPermissionMode.VIEWING, "org.apache.isis.applib.fixturescripts"));
    }

    @Inject
    private ApplicationRoles applicationRoles;

}
