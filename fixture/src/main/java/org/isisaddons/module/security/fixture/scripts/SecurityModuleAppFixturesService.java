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

import java.util.List;
import org.isisaddons.module.security.dom.role.ApplicationRole;
import org.apache.isis.applib.Identifier;
import org.apache.isis.applib.annotation.ActionInteraction;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.fixturescripts.FixtureResult;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.fixturescripts.FixtureScripts;
import org.apache.isis.applib.services.eventbus.ActionInteractionEvent;

/**
 * Enables fixtures to be installed from the application.
 */
@DomainService()
@DomainServiceLayout(named="Prototyping", menuOrder = "99", menuBar = DomainServiceLayout.MenuBar.SECONDARY)
public class SecurityModuleAppFixturesService extends FixtureScripts {

    //region > constructor
    public SecurityModuleAppFixturesService() {
        super(SecurityModuleAppFixturesService.class.getPackage().getName());
    }
    //endregion

    //region > runFixtureScript
    public static class RunFixtureScriptEvent extends ActionInteractionEvent<SecurityModuleAppFixturesService> {
        public RunFixtureScriptEvent(SecurityModuleAppFixturesService source, Identifier identifier, Object... args) {
            super(source, identifier, args);
        }
    }

    @ActionInteraction(RunFixtureScriptEvent.class)
    @Override
    public List<FixtureResult> runFixtureScript(
            final FixtureScript fixtureScript,
            final @ParameterLayout(
                named = "Parameters",
                describedAs = "Script-specific parameters (if any).  The format depends on the script implementation (eg key=value, CSV, JSON, XML etc)",
                multiLine = 10
            )
            @Optional
            String parameters) {
        return super.runFixtureScript(fixtureScript, parameters);
    }

    @Override
    public FixtureScript default0RunFixtureScript() {
        return findFixtureScriptFor(SecurityModuleAppSetUp.class);
    }

    /**
     * Raising visibility to <tt>public</tt> so that choices are available for first param
     * of {@link #runFixtureScript(FixtureScript, String)}.
     */
    @Override
    public List<FixtureScript> choices0RunFixtureScript() {
        return super.choices0RunFixtureScript();
    }
    //endregion

    //region > installFixturesAndReturnFirstRole
    public static class InstallFixturesAndReturnFirstRoleEvent extends ActionInteractionEvent<SecurityModuleAppFixturesService> {
        public InstallFixturesAndReturnFirstRoleEvent(SecurityModuleAppFixturesService source, Identifier identifier, Object... args) {
            super(source, identifier, args);
        }
    }

    @ActionInteraction(InstallFixturesAndReturnFirstRoleEvent.class)
    @ActionSemantics(ActionSemantics.Of.NON_IDEMPOTENT)
    @ActionLayout(prototype = true)
    @MemberOrder(sequence="20")
    public Object installFixturesAndReturnFirstRole() {
        final List<FixtureResult> fixtureResultList = findFixtureScriptFor(SecurityModuleAppSetUp.class).run(null);
        for (FixtureResult fixtureResult : fixtureResultList) {
            final Object object = fixtureResult.getObject();
            if(object instanceof ApplicationRole) {
                return object;
            }
        }
        getContainer().warnUser("No rules found in fixture; returning all results");
        return fixtureResultList;
    }
    //endregion

}
