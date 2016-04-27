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
package org.isisaddons.module.security.dom.permission;

import java.util.Arrays;
import java.util.List;

import com.danhaywood.java.testsupport.coverage.PrivateConstructorTester;

import org.junit.Test;

import org.apache.isis.core.metamodel.services.appfeat.ApplicationFeatureId;
import org.apache.isis.core.unittestsupport.value.ValueTypeContractTestAbstract;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ApplicationPermissionValueTest {

    private static ApplicationFeatureId pCom = ApplicationFeatureId.newPackage("com");
    private static ApplicationFeatureId pComFoo = ApplicationFeatureId.newPackage("com.foo");
    private static ApplicationFeatureId cComFooBar = ApplicationFeatureId.newClass("com.foo.Bar");
    private static ApplicationFeatureId mComFooBar_bop = ApplicationFeatureId.newMember("com.foo.Bar", "bop");
    private static ApplicationFeatureId mComFooBar_bip = ApplicationFeatureId.newMember("com.foo.Bar", "bip");
    private static ApplicationFeatureId mComFooBar_bup = ApplicationFeatureId.newMember("com.foo.Bar", "bup");

    public static class ValueTypeContractTest extends ValueTypeContractTestAbstract<ApplicationPermissionValue> {

        @Override
        protected List<ApplicationPermissionValue> getObjectsWithSameValue() {
            return Arrays.asList(
                    new ApplicationPermissionValue(ApplicationFeatureId.newPackage("com.mycompany"), ApplicationPermissionRule.ALLOW, ApplicationPermissionMode.CHANGING),
                    new ApplicationPermissionValue(ApplicationFeatureId.newPackage("com.mycompany"), ApplicationPermissionRule.ALLOW, ApplicationPermissionMode.CHANGING)
            );
        }

        @Override
        protected List<ApplicationPermissionValue> getObjectsWithDifferentValue() {
            return Arrays.asList(
                    new ApplicationPermissionValue(ApplicationFeatureId.newPackage("com.mycompany2"), ApplicationPermissionRule.ALLOW, ApplicationPermissionMode.CHANGING),
                    new ApplicationPermissionValue(ApplicationFeatureId.newPackage("com.mycompany"), ApplicationPermissionRule.VETO, ApplicationPermissionMode.CHANGING),
                    new ApplicationPermissionValue(ApplicationFeatureId.newPackage("com.mycompany"), ApplicationPermissionRule.ALLOW, ApplicationPermissionMode.VIEWING)
            );
        }

    }


    public static class Implies_and_Refutes extends ApplicationPermissionValueTest {

        public static class GivenMember extends Implies_and_Refutes {

            @Test
            public void allowChanging() throws Exception {
                // given
                final ApplicationPermissionValue apv = allowChanging(mComFooBar_bip);

                // when, then
                assertThat(apv.implies(mComFooBar_bip, changing()), is(true));
                assertThat(apv.implies(mComFooBar_bip, viewing()), is(true));

                assertThat(apv.refutes(mComFooBar_bip, changing()), is(false));
                assertThat(apv.refutes(mComFooBar_bip, viewing()), is(false));

                // when, then (for some other member)
                assertThat(apv.implies(mComFooBar_bop, changing()), is(false));
                assertThat(apv.implies(mComFooBar_bop, viewing()), is(false));
            }

            @Test
            public void allowViewing() throws Exception {
                // given
                final ApplicationPermissionValue apv = allowViewing(mComFooBar_bip);

                // when, then
                assertThat(apv.implies(mComFooBar_bip, changing()), is(false));
                assertThat(apv.implies(mComFooBar_bip, viewing()), is(true));

                assertThat(apv.refutes(mComFooBar_bip, changing()), is(false));
                assertThat(apv.refutes(mComFooBar_bip, viewing()), is(false));

                // when, then (for some other member)
                assertThat(apv.implies(mComFooBar_bop, viewing()), is(false));
            }

            @Test
            public void vetoChanging() throws Exception {
                // given
                final ApplicationPermissionValue apv = vetoChanging(mComFooBar_bip);

                // when, then
                assertThat(apv.implies(mComFooBar_bip, changing()), is(false));
                assertThat(apv.implies(mComFooBar_bip, viewing()), is(false));

                assertThat(apv.refutes(mComFooBar_bip, changing()), is(true));
                assertThat(apv.refutes(mComFooBar_bip, viewing()), is(false));

                // when, then (for some other member)
                assertThat(apv.refutes(mComFooBar_bop, viewing()), is(false));
            }

            @Test
            public void vetoViewing() throws Exception {
                // given
                final ApplicationPermissionValue apv = vetoViewing(mComFooBar_bip);

                // when, then
                assertThat(apv.implies(mComFooBar_bip, changing()), is(false));
                assertThat(apv.implies(mComFooBar_bip, viewing()), is(false));

                assertThat(apv.refutes(mComFooBar_bip, changing()), is(true));
                assertThat(apv.refutes(mComFooBar_bip, viewing()), is(true));

                // when, then (for some other member)
                assertThat(apv.refutes(mComFooBar_bop, changing()), is(false));
                assertThat(apv.refutes(mComFooBar_bop, viewing()), is(false));
            }
        }

        public static class GivenClass extends Implies_and_Refutes {

            @Test
            public void allowChanging() throws Exception {
                // given
                final ApplicationPermissionValue apv = allowChanging(cComFooBar);

                // when, then
                assertThat(apv.implies(mComFooBar_bip, changing()), is(true));
                assertThat(apv.implies(mComFooBar_bip, viewing()), is(true));

                assertThat(apv.refutes(mComFooBar_bip, changing()), is(false));
                assertThat(apv.refutes(mComFooBar_bip, viewing()), is(false));

                // when, then (for some other member)
                assertThat(apv.implies(mComFooBar_bop, changing()), is(true));
                assertThat(apv.implies(mComFooBar_bop, viewing()), is(true));

                assertThat(apv.refutes(mComFooBar_bop, changing()), is(false));
                assertThat(apv.refutes(mComFooBar_bop, viewing()), is(false));
            }

            @Test
            public void allowViewing() throws Exception {
                // given
                final ApplicationPermissionValue apv = allowViewing(cComFooBar);

                // when, then
                assertThat(apv.implies(mComFooBar_bip, changing()), is(false));
                assertThat(apv.implies(mComFooBar_bip, viewing()), is(true));

                assertThat(apv.refutes(mComFooBar_bip, changing()), is(false));
                assertThat(apv.refutes(mComFooBar_bip, viewing()), is(false));

                // when, then (for some other member)
                assertThat(apv.implies(mComFooBar_bop, changing()), is(false));
                assertThat(apv.implies(mComFooBar_bop, viewing()), is(true));

                assertThat(apv.refutes(mComFooBar_bop, changing()), is(false));
                assertThat(apv.refutes(mComFooBar_bop, viewing()), is(false));
            }

            @Test
            public void vetoChanging() throws Exception {
                // given
                final ApplicationPermissionValue apv = vetoChanging(cComFooBar);

                // when, then
                assertThat(apv.implies(mComFooBar_bip, changing()), is(false));
                assertThat(apv.implies(mComFooBar_bip, viewing()), is(false));

                assertThat(apv.refutes(mComFooBar_bip, changing()), is(true));
                assertThat(apv.refutes(mComFooBar_bip, viewing()), is(false));

                // when, then (for some other member)
                assertThat(apv.implies(mComFooBar_bop, changing()), is(false));
                assertThat(apv.implies(mComFooBar_bop, viewing()), is(false));

                assertThat(apv.refutes(mComFooBar_bop, changing()), is(true));
                assertThat(apv.refutes(mComFooBar_bop, viewing()), is(false));
            }

            @Test
            public void vetoViewing() throws Exception {
                // given
                final ApplicationPermissionValue apv = vetoViewing(cComFooBar);

                // when, then
                assertThat(apv.implies(mComFooBar_bip, changing()), is(false));
                assertThat(apv.implies(mComFooBar_bip, viewing()), is(false));

                assertThat(apv.refutes(mComFooBar_bip, changing()), is(true));
                assertThat(apv.refutes(mComFooBar_bip, viewing()), is(true));

                // when, then (for some other member)
                assertThat(apv.implies(mComFooBar_bop, changing()), is(false));
                assertThat(apv.implies(mComFooBar_bop, viewing()), is(false));

                assertThat(apv.refutes(mComFooBar_bop, changing()), is(true));
                assertThat(apv.refutes(mComFooBar_bop, viewing()), is(true));
            }
        }

        public static class GivenPackage extends Implies_and_Refutes {

            @Test
            public void allowChanging() throws Exception {
                // given
                final ApplicationPermissionValue apv = allowChanging(pCom);

                // when, then
                assertThat(apv.implies(mComFooBar_bip, changing()), is(true));
                assertThat(apv.implies(mComFooBar_bip, viewing()), is(true));

                assertThat(apv.refutes(mComFooBar_bip, changing()), is(false));
                assertThat(apv.refutes(mComFooBar_bip, viewing()), is(false));

                // when, then (for some other member)
                assertThat(apv.implies(mComFooBar_bop, changing()), is(true));
                assertThat(apv.implies(mComFooBar_bop, viewing()), is(true));

                assertThat(apv.refutes(mComFooBar_bop, changing()), is(false));
                assertThat(apv.refutes(mComFooBar_bop, viewing()), is(false));
            }

            @Test
            public void allowViewing() throws Exception {
                // given
                final ApplicationPermissionValue apv = allowViewing(pCom);

                // when, then
                assertThat(apv.implies(mComFooBar_bip, changing()), is(false));
                assertThat(apv.implies(mComFooBar_bip, viewing()), is(true));

                assertThat(apv.refutes(mComFooBar_bip, changing()), is(false));
                assertThat(apv.refutes(mComFooBar_bip, viewing()), is(false));

                // when, then (for some other member)
                assertThat(apv.implies(mComFooBar_bop, changing()), is(false));
                assertThat(apv.implies(mComFooBar_bop, viewing()), is(true));

                assertThat(apv.refutes(mComFooBar_bop, changing()), is(false));
                assertThat(apv.refutes(mComFooBar_bop, viewing()), is(false));
            }

            @Test
            public void vetoChanging() throws Exception {
                // given
                final ApplicationPermissionValue apv = vetoChanging(pCom);

                // when, then
                assertThat(apv.implies(mComFooBar_bip, changing()), is(false));
                assertThat(apv.implies(mComFooBar_bip, viewing()), is(false));

                assertThat(apv.refutes(mComFooBar_bip, changing()), is(true));
                assertThat(apv.refutes(mComFooBar_bip, viewing()), is(false));

                // when, then (for some other member)
                assertThat(apv.implies(mComFooBar_bop, changing()), is(false));
                assertThat(apv.implies(mComFooBar_bop, viewing()), is(false));

                assertThat(apv.refutes(mComFooBar_bop, changing()), is(true));
                assertThat(apv.refutes(mComFooBar_bop, viewing()), is(false));
            }

            @Test
            public void vetoViewing() throws Exception {
                // given
                final ApplicationPermissionValue apv = vetoViewing(pCom);

                // when, then
                assertThat(apv.implies(mComFooBar_bip, changing()), is(false));
                assertThat(apv.implies(mComFooBar_bip, viewing()), is(false));

                assertThat(apv.refutes(mComFooBar_bip, changing()), is(true));
                assertThat(apv.refutes(mComFooBar_bip, viewing()), is(true));

                // when, then (for some other member)
                assertThat(apv.implies(mComFooBar_bop, changing()), is(false));
                assertThat(apv.implies(mComFooBar_bop, viewing()), is(false));

                assertThat(apv.refutes(mComFooBar_bop, changing()), is(true));
                assertThat(apv.refutes(mComFooBar_bop, viewing()), is(true));
            }
        }

        //region > helpers just to make tests easier to read


        static ApplicationPermissionValue allowChanging(ApplicationFeatureId featureId) {
            return new ApplicationPermissionValue(featureId, ApplicationPermissionRule.ALLOW, ApplicationPermissionMode.CHANGING);
        }

        static ApplicationPermissionValue vetoChanging(ApplicationFeatureId featureId) {
            return new ApplicationPermissionValue(featureId, ApplicationPermissionRule.VETO, ApplicationPermissionMode.CHANGING);
        }

        static ApplicationPermissionValue allowViewing(ApplicationFeatureId featureId) {
            return new ApplicationPermissionValue(featureId, ApplicationPermissionRule.ALLOW, ApplicationPermissionMode.VIEWING);
        }

        static ApplicationPermissionValue vetoViewing(ApplicationFeatureId featureId) {
            return new ApplicationPermissionValue(featureId, ApplicationPermissionRule.VETO, ApplicationPermissionMode.VIEWING);
        }

        static ApplicationPermissionMode changing() {
            return ApplicationPermissionMode.CHANGING;
        }

        static ApplicationPermissionMode viewing() {
            return ApplicationPermissionMode.VIEWING;
        }
        //endregion

    }

    public static class PrivateConstructors extends ApplicationPermissionValueTest {

        @Test
        public void forComparators() throws Exception {
            new PrivateConstructorTester(ApplicationPermissionValue.Comparators.class).exercise();
        }

    }



}
