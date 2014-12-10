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
import org.apache.isis.applib.annotation.*;
import org.apache.isis.applib.fixturescripts.FixtureResult;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.fixturescripts.FixtureScripts;

/**
 * Enables fixtures to be installed from the application.
 */
@DomainService()
@DomainServiceLayout(named="Prototyping", menuOrder = "99")
public class SecurityModuleAppFixturesService extends FixtureScripts {

    public SecurityModuleAppFixturesService() {
        super(SecurityModuleAppFixturesService.class.getPackage().getName());
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


    // //////////////////////////////////////


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

}
