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
package org.isisaddons.module.security.fixture.scripts.rolesandperms;

import org.apache.isis.applib.fixturescripts.FixtureScript;

public class AllExampleRolesAndPermissions extends FixtureScript {

    @Override
    protected void execute(ExecutionContext executionContext) {
        execute(new ExampleGuestRoleAndPremissions(), executionContext);
        execute(new ExampleRegularRoleAndPermissions(), executionContext);
    }

}
