package org.isisaddons.module.security.seed.scripts;

import java.util.Collections;
import java.util.List;
import javax.inject.Inject;
import com.google.common.collect.Lists;
import org.isisaddons.module.security.dom.role.ApplicationRole;
import org.isisaddons.module.security.dom.role.ApplicationRoles;
import org.isisaddons.module.security.dom.user.ApplicationUser;
import org.isisaddons.module.security.dom.user.ApplicationUserStatus;
import org.isisaddons.module.security.dom.user.ApplicationUsers;
import org.apache.isis.applib.fixturescripts.FixtureScript;

public class AbstractUserAndRolesFixtureScript extends FixtureScript {

    private final String username;
    private final String password;
    private final List<String> roleNames;

    public AbstractUserAndRolesFixtureScript(
            final String username,
            final String password,
            final List<String> roleNames) {
        this.username = username;
        this.password = password;
        this.roleNames = Collections.unmodifiableList(Lists.newArrayList(roleNames));
    }

    @Override
    protected void execute(ExecutionContext executionContext) {

        // create user if does not exist, and assign to the role
        ApplicationUser adminUser = applicationUsers.findUserByUsernameNoAutocreate(username);
        if(adminUser == null) {
            adminUser = applicationUsers.newUser(username, null , null);
            adminUser.setStatus(ApplicationUserStatus.ENABLED);

            if(applicationUsers.isPasswordsFeatureEnabled() && password != null) {
                adminUser.updatePassword(password);
            }

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
