/*
 *  Copyright 2015 Jeroen van der Wal
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
package org.isisaddons.module.security.dom.tenancy;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import static org.hamcrest.CoreMatchers.is;

public class ApplicationTenancyMenuTest {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    ApplicationTenancyMenu applicationTenancyMenu;

    @Mock
    ApplicationTenancies applicationTenancies;

    public static class IconName extends ApplicationTenancyMenuTest {

        @Test
        public void happyCase() throws Exception {

            applicationTenancies = new ApplicationTenancies();
            Assert.assertThat(applicationTenancies.iconName(), is("applicationTenancy"));
        }

    }

    public static class FindTenancies extends ApplicationTenancyMenuTest {

        @Test
        public void happyCase() throws Exception {

            // given
            applicationTenancyMenu = new ApplicationTenancyMenu();
            applicationTenancyMenu.applicationTenancyRepository = applicationTenancies;

            // then
            context.checking(new Expectations() {{
                oneOf(applicationTenancies).findByNameOrPathMatching(with("(?i).*test.*"));
            }});

            // when
            applicationTenancyMenu.findTenancies("*test*");

        }

    }

}