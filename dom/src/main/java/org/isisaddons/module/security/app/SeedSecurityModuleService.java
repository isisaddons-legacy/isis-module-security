/*
 *  Copyright 2014 Dan Haywood
 *
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
package org.isisaddons.module.security.app;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import com.google.common.collect.Lists;
import org.isisaddons.module.security.dom.feature.ApplicationFeatureType;
import org.isisaddons.module.security.dom.permission.ApplicationPermissionMode;
import org.isisaddons.module.security.dom.permission.ApplicationPermissionRule;
import org.isisaddons.module.security.dom.permission.ApplicationPermissions;
import org.isisaddons.module.security.dom.role.ApplicationRole;
import org.isisaddons.module.security.dom.role.ApplicationRoles;
import org.isisaddons.module.security.dom.user.ApplicationUser;
import org.isisaddons.module.security.dom.user.ApplicationUsers;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.fixturescripts.FixtureScripts;

@Hidden
@DomainService
public class SeedSecurityModuleService {

    //region > init
    @Programmatic
    @PostConstruct
    public void init() {
        fixtureScripts.runFixtureScript(new SeedUsersAndRolesFixtureScript(), null);
    }
    //endregion

    /**
     * This fixture script will be run automatically on start-up by virtue of the fact that the
     * {@link SeedSecurityModuleService} is a
     * {@link org.apache.isis.applib.annotation.DomainService} and calls the setup during its
     * {@link SeedSecurityModuleService#init() init} ({@link javax.annotation.PostConstruct}) method.
     */
    public static class SeedUsersAndRolesFixtureScript extends FixtureScript {
        @Override
        protected void execute(ExecutionContext executionContext) {
            execute(new AdminRoleAndPermissions(), executionContext);
            execute(new AdminUserFixtureScript(), executionContext);

            execute(new MeServiceRoleAndPermissions(), executionContext);
            execute(new ApplibFixtureResultsRoleAndPermissions(), executionContext);
        }
    }

    public abstract static class RoleAndPermissionsFixtureScriptAbstract extends FixtureScript {

        private final String roleName;
        private final String roleDescription;
        private final List<String> packages;

        protected RoleAndPermissionsFixtureScriptAbstract(
                final String roleName,
                final String roleDescriptionIfAny,
                final List<String> packages) {
            this.roleName = roleName;
            this.roleDescription = roleDescriptionIfAny;
            this.packages = Collections.unmodifiableList(Lists.newArrayList(packages));
        }

        @Override
        protected void execute(ExecutionContext executionContext) {

            // create role if does not exist, assign permissions to the security module
            ApplicationRole securityAdminRole = applicationRoles.findRoleByName(roleName);
            if(securityAdminRole == null) {
                securityAdminRole = applicationRoles.newRole(roleName, roleDescription);

                for (String pkg : packages) {
                    // can't use role#addPackage because that does a check for existence of the package, which is
                    // not guaranteed to exist yet (the SecurityFeatures#init() may not have run).
                    applicationPermissions.newPermissionNoCheck(
                            securityAdminRole,
                            ApplicationPermissionRule.ALLOW,
                            ApplicationPermissionMode.CHANGING,
                            ApplicationFeatureType.PACKAGE, pkg);
                }
            }
        }

        //region  >  (injected)
        @Inject
        ApplicationRoles applicationRoles;
        @Inject
        ApplicationPermissions applicationPermissions;
        //endregion
    }

    public static class AdminRoleAndPermissions extends RoleAndPermissionsFixtureScriptAbstract {

        public static final String ROLE_NAME = "isis-security-module-admin";
        private static final String SECURITY_MODULE_PACKAGE = "org.isisaddons.module.security";

        public AdminRoleAndPermissions() {
            super(ROLE_NAME, "Administer security", Arrays.asList(SECURITY_MODULE_PACKAGE));
        }
    }

    public static class MeServiceRoleAndPermissions extends RoleAndPermissionsFixtureScriptAbstract {

        public static final String ROLE_NAME = "isis-security-module-me-service";
        private static final String ME_SERVICE_CLASS = "org.isisaddons.module.security.app.MeService";

        public MeServiceRoleAndPermissions() {
            super(ROLE_NAME, "Access MeService", Arrays.asList(ME_SERVICE_CLASS));
        }
    }

    public static class ApplibFixtureResultsRoleAndPermissions extends RoleAndPermissionsFixtureScriptAbstract {

        public static final String ROLE_NAME = "isis-applib-fixtureresults";
        private static final String ME_SERVICE_CLASS = "org.apache.isis.applib.fixturescripts.FixtureResults";

        public ApplibFixtureResultsRoleAndPermissions() {
            super(ROLE_NAME, "Access results of running Fixture Scripts", Arrays.asList(ME_SERVICE_CLASS));
        }
    }

    public static class UserAndRolesFixtureScriptAbstract extends FixtureScript {

        private final String username;
        private final List<String> roleNames;

        public UserAndRolesFixtureScriptAbstract(String username, List<String> roleNames) {
            this.username = username;
            this.roleNames = Collections.unmodifiableList(Lists.newArrayList(roleNames));
        }

        @Override
        protected void execute(ExecutionContext executionContext) {

            // create user if does not exist, and assign to the role
            ApplicationUser adminUser = applicationUsers.findUserByUsernameNoAutocreate(username);
            if(adminUser == null) {
                adminUser = applicationUsers.newUser(username);

                for (String roleName : roleNames) {
                    ApplicationRole securityAdminRole = applicationRoles.findRoleByName(roleName);
                    adminUser.addRole(securityAdminRole);
                }
            }
        }

        //region  >  (injected)
        @Inject
        ApplicationUsers applicationUsers;
        @Inject
        ApplicationRoles applicationRoles;
        //endregion

    }

    public static class AdminUserFixtureScript extends UserAndRolesFixtureScriptAbstract {

        public static final String USER_NAME = "admin";

        public AdminUserFixtureScript() {
            super(USER_NAME, Arrays.asList(AdminRoleAndPermissions.ROLE_NAME));
        }
    }

    //region  >  (injected)
    @Inject
    FixtureScripts fixtureScripts;
    //endregion

}
