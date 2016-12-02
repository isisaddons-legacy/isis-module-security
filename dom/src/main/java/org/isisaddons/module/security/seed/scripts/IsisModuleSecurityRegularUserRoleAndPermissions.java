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

import org.isisaddons.module.security.app.user.MeService;
import org.isisaddons.module.security.dom.permission.ApplicationPermissionMode;
import org.isisaddons.module.security.dom.permission.ApplicationPermissionRule;
import org.isisaddons.module.security.dom.role.ApplicationRole;
import org.isisaddons.module.security.dom.user.ApplicationUser;

/**
 * Role for regular users of the security module, providing the ability to lookup their user account using the
 * {@link org.isisaddons.module.security.app.user.MeService}, and for viewing and maintaining their user details.
 */
public class IsisModuleSecurityRegularUserRoleAndPermissions extends AbstractRoleAndPermissionsFixtureScript {

    public static final String ROLE_NAME = "isis-module-security-regular-user";

    public IsisModuleSecurityRegularUserRoleAndPermissions() {
        super(ROLE_NAME, "Regular user of the security module");
    }

    @Override
    protected void execute(ExecutionContext executionContext) {

        newMemberPermissions(
                ApplicationPermissionRule.ALLOW,
                ApplicationPermissionMode.CHANGING,
                MeService.class,
                "me");

        newClassPermissions(
                ApplicationPermissionRule.ALLOW,
                ApplicationPermissionMode.VIEWING,
                ApplicationUser.class);
        newMemberPermissions(
                ApplicationPermissionRule.ALLOW,
                ApplicationPermissionMode.CHANGING,
                ApplicationUser.class,
                "updateName",
                "updatePassword",
                "updateEmailAddress",
                "updatePhoneNumber",
                "updateFaxNumber");
        newMemberPermissions(
                ApplicationPermissionRule.VETO,
                ApplicationPermissionMode.VIEWING,
                ApplicationUser.class,
                "filterPermissions",
                "resetPassword",
                "updateTenancy",
                "lock", // renamed as 'enable' in the UI
                "unlock", // renamed as 'disable' in the UI
                "addRole",
                "removeRole");
        newMemberPermissions(
                ApplicationPermissionRule.ALLOW,
                ApplicationPermissionMode.VIEWING,
                ApplicationRole.class,
                "name",
                "description");

//        // for adhoc testing of #42
//        newMemberPermissions(
//                ApplicationPermissionRule.ALLOW,
//                ApplicationPermissionMode.CHANGING,
//                ApplicationUser.class,
//                "orphanedUpdateEmailAddress",
//                "orphanedUpdatePhoneNumber",
//                "orphanedUpdateFaxNumber");

    }

}
