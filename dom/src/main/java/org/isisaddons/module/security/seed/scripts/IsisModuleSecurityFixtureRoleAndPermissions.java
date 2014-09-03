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
package org.isisaddons.module.security.seed.scripts;

import org.isisaddons.module.security.dom.permission.ApplicationPermissionMode;
import org.isisaddons.module.security.dom.permission.ApplicationPermissionRule;

/**
 * Role to run in the prototype fixture scripts for the example webapp for the security module.
 */
public class IsisModuleSecurityFixtureRoleAndPermissions extends AbstractRoleAndPermissionsFixtureScript {

    public static final String ROLE_NAME = "isis-module-security-fixtures";

    public IsisModuleSecurityFixtureRoleAndPermissions() {
        super(ROLE_NAME, "Security module fixtures");
    }

    @Override
    protected void execute(ExecutionContext executionContext) {
        newPackagePermissions(
                ApplicationPermissionRule.ALLOW,
                ApplicationPermissionMode.CHANGING,
                "org.isisaddons.module.security.fixture");
    }
}
