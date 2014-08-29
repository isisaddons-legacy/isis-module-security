package org.isisaddons.module.security.fixture.scripts.roles;

public class NoFixtureScriptsRoleFixture extends AbstractRoleFixture {

    public static final String ROLE_NAME = "noFixtureScripts";

    @Override
    protected void execute(ExecutionContext executionContext) {
        create(ROLE_NAME, "No ability to use Apache Isis' fixture scripts", executionContext);
    }

}
