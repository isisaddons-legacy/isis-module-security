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

import org.junit.Test;

import org.apache.isis.core.metamodel.services.appfeat.ApplicationFeatureId;
import org.apache.isis.core.unittestsupport.value.ValueTypeContractTestAbstract;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ApplicationPermissionSetValueTest {

    private static ApplicationFeatureId pCom = ApplicationFeatureId.newPackage("com");
    private static ApplicationFeatureId pComFoo = ApplicationFeatureId.newPackage("com.foo");
    private static ApplicationFeatureId cComFooBar = ApplicationFeatureId.newClass("com.foo.Bar");
    private static ApplicationFeatureId mComFooBar_bop = ApplicationFeatureId.newMember("com.foo.Bar", "bop");
    private static ApplicationFeatureId mComFooBar_bip = ApplicationFeatureId.newMember("com.foo.Bar", "bip");

    public static class Grants extends ApplicationPermissionSetValueTest {

        public static class GivenSetWithSingleMember extends Grants {

            @Test
            public void allowChanging() throws Exception {
                // given
                final ApplicationPermissionValueSet apv = newSet(allowChanging(mComFooBar_bip));

                // when, then
                assertThat(apv.grants(mComFooBar_bip, changing()), is(true));
                assertThat(apv.grants(mComFooBar_bip, viewing()), is(true));

                // when, then (for some other member)
                assertThat(apv.grants(mComFooBar_bop, changing()), is(false));
                assertThat(apv.grants(mComFooBar_bop, viewing()), is(false));
            }

            @Test
            public void allowViewing() throws Exception {
                // given
                final ApplicationPermissionValueSet apv = newSet(allowViewing(mComFooBar_bip));

                // when, then
                assertThat(apv.grants(mComFooBar_bip, changing()), is(false));
                assertThat(apv.grants(mComFooBar_bip, viewing()), is(true));

                // when, then (for some other member)
                assertThat(apv.grants(mComFooBar_bop, viewing()), is(false));
            }

            @Test
            public void vetoChanging() throws Exception {
                // given
                final ApplicationPermissionValueSet apv = newSet(vetoChanging(mComFooBar_bip));

                // when, then
                assertThat(apv.grants(mComFooBar_bip, changing()), is(false));
                assertThat(apv.grants(mComFooBar_bip, viewing()), is(false));
            }

            @Test
            public void vetoViewing() throws Exception {
                // given
                final ApplicationPermissionValueSet apv = newSet(vetoViewing(mComFooBar_bip));

                // when, then
                assertThat(apv.grants(mComFooBar_bip, changing()), is(false));
                assertThat(apv.grants(mComFooBar_bip, viewing()), is(false));
            }
        }

        public static class GivenSetWithSingleClass extends Grants {

            @Test
            public void allowChanging() throws Exception {
                // given
                final ApplicationPermissionValueSet apv = newSet(allowChanging(cComFooBar));

                // when, then
                assertThat(apv.grants(mComFooBar_bip, changing()), is(true));
                assertThat(apv.grants(mComFooBar_bip, viewing()), is(true));
            }

            @Test
            public void allowViewing() throws Exception {
                // given
                final ApplicationPermissionValueSet apv = newSet(allowViewing(cComFooBar));

                // when, then
                assertThat(apv.grants(mComFooBar_bip, changing()), is(false));
                assertThat(apv.grants(mComFooBar_bip, viewing()), is(true));
            }

            @Test
            public void vetoChanging() throws Exception {
                // given
                final ApplicationPermissionValueSet apv = newSet(vetoChanging(cComFooBar));

                // when, then
                assertThat(apv.grants(mComFooBar_bip, changing()), is(false));
                assertThat(apv.grants(mComFooBar_bip, viewing()), is(false));
            }

            @Test
            public void vetoViewing() throws Exception {
                // given
                final ApplicationPermissionValueSet apv = newSet(vetoViewing(cComFooBar));

                // when, then
                assertThat(apv.grants(mComFooBar_bip, changing()), is(false));
                assertThat(apv.grants(mComFooBar_bip, viewing()), is(false));
            }
        }

        public static class GivenSetWithSinglePackage extends Grants {

            @Test
            public void allowChanging() throws Exception {
                // given
                final ApplicationPermissionValueSet apv = newSet(allowChanging(pCom));

                // when, then
                assertThat(apv.grants(mComFooBar_bip, changing()), is(true));
                assertThat(apv.grants(mComFooBar_bip, viewing()), is(true));
            }

            @Test
            public void allowViewing() throws Exception {
                // given
                final ApplicationPermissionValueSet apv = newSet(allowViewing(pCom));

                // when, then
                assertThat(apv.grants(mComFooBar_bip, changing()), is(false));
                assertThat(apv.grants(mComFooBar_bip, viewing()), is(true));
            }

            @Test
            public void vetoChanging() throws Exception {
                // given
                final ApplicationPermissionValueSet apv = newSet(vetoChanging(pCom));

                // when, then
                assertThat(apv.grants(mComFooBar_bip, changing()), is(false));
                assertThat(apv.grants(mComFooBar_bip, viewing()), is(false));
            }

            @Test
            public void vetoViewing() throws Exception {
                // given
                final ApplicationPermissionValueSet apv = newSet(vetoViewing(pCom));

                // when, then
                assertThat(apv.grants(mComFooBar_bip, changing()), is(false));
                assertThat(apv.grants(mComFooBar_bip, viewing()), is(false));
            }
        }

        public static class GivenSetWithMemberAndMember extends Grants {

            @Test
            public void allowChanging_and_vetoChanging() throws Exception {
                // given
                final ApplicationPermissionValueSet apv = newSet(allowChanging(mComFooBar_bip), vetoChanging(mComFooBar_bip));

                // when, then
                assertThat(apv.grants(mComFooBar_bip, changing()), is(true));
                assertThat(apv.grants(mComFooBar_bip, viewing()), is(true));
            }

            @Test
            public void allowChanging_and_vetoViewing() throws Exception {
                // given
                final ApplicationPermissionValueSet apv = newSet(allowChanging(mComFooBar_bip), vetoViewing(mComFooBar_bip));

                // when, then
                assertThat(apv.grants(mComFooBar_bip, changing()), is(true));
                assertThat(apv.grants(mComFooBar_bip, viewing()), is(true));
            }

            @Test
            public void allowViewing_and_vetoChanging() throws Exception {
                // given
                final ApplicationPermissionValueSet apv = newSet(allowViewing(mComFooBar_bip), vetoChanging(mComFooBar_bip));

                // when, then
                assertThat(apv.grants(mComFooBar_bip, changing()), is(false));
                assertThat(apv.grants(mComFooBar_bip, viewing()), is(true));
            }

            @Test
            public void allowViewing_and_vetoViewing() throws Exception {
                // given
                final ApplicationPermissionValueSet apv = newSet(allowViewing(mComFooBar_bip), vetoViewing(mComFooBar_bip));

                // when, then
                assertThat(apv.grants(mComFooBar_bip, changing()), is(false));
                assertThat(apv.grants(mComFooBar_bip, viewing()), is(true)); // veto doesn't win against allow
            }

        }

        public static class GivenSetWithMemberAndClass extends Grants {

            @Test
            public void memberAllowChanging_and_classVetoChanging() throws Exception {
                // given
                final ApplicationPermissionValueSet apv = newSet(allowChanging(mComFooBar_bip), vetoChanging(cComFooBar));

                // when, then
                assertThat(apv.grants(mComFooBar_bip, changing()), is(true));
                assertThat(apv.grants(mComFooBar_bip, viewing()), is(true));
            }

            @Test
            public void memberAllowChanging_and_classVetoViewing() throws Exception {
                // given
                final ApplicationPermissionValueSet apv = newSet(allowChanging(mComFooBar_bip), vetoViewing(cComFooBar));

                // when, then
                assertThat(apv.grants(mComFooBar_bip, changing()), is(true));
                assertThat(apv.grants(mComFooBar_bip, viewing()), is(true));
            }

            @Test
            public void memberAllowViewing_and_classVetoChanging() throws Exception {
                // given
                final ApplicationPermissionValueSet apv = newSet(allowViewing(mComFooBar_bip), vetoChanging(cComFooBar));

                // when, then
                assertThat(apv.grants(mComFooBar_bip, changing()), is(false));
                assertThat(apv.grants(mComFooBar_bip, viewing()), is(true));
            }

            @Test
            public void memberAllowViewing_and_classVetoViewing() throws Exception {
                // given
                final ApplicationPermissionValueSet apv = newSet(allowViewing(mComFooBar_bip), vetoViewing(cComFooBar));

                // when, then
                assertThat(apv.grants(mComFooBar_bip, changing()), is(false));
                assertThat(apv.grants(mComFooBar_bip, viewing()), is(true));
            }

        }

        public static class GivenSetWithMemberAndPackage extends Grants {

            @Test
            public void memberAllowChanging_and_packageVetoChanging() throws Exception {
                // given
                final ApplicationPermissionValueSet apv = newSet(allowChanging(mComFooBar_bip), vetoChanging(pComFoo));

                // when, then
                assertThat(apv.grants(mComFooBar_bip, changing()), is(true));
                assertThat(apv.grants(mComFooBar_bip, viewing()), is(true));
            }

            @Test
            public void memberAllowChanging_and_packageVetoViewing() throws Exception {
                // given
                final ApplicationPermissionValueSet apv = newSet(allowChanging(mComFooBar_bip), vetoViewing(pComFoo));

                // when, then
                assertThat(apv.grants(mComFooBar_bip, changing()), is(true));
                assertThat(apv.grants(mComFooBar_bip, viewing()), is(true));
            }

            @Test
            public void memberAllowViewing_and_packageVetoChanging() throws Exception {
                // given
                final ApplicationPermissionValueSet apv = newSet(allowViewing(mComFooBar_bip), vetoChanging(pComFoo));

                // when, then
                assertThat(apv.grants(mComFooBar_bip, changing()), is(false));
                assertThat(apv.grants(mComFooBar_bip, viewing()), is(true));
            }

            @Test
            public void memberAllowViewing_and_packageVetoViewing() throws Exception {
                // given
                final ApplicationPermissionValueSet apv = newSet(allowViewing(mComFooBar_bip), vetoViewing(pComFoo));

                // when, then
                assertThat(apv.grants(mComFooBar_bip, changing()), is(false));
                assertThat(apv.grants(mComFooBar_bip, viewing()), is(true));
            }

        }

        public static class GivenSetWithClassAndMember extends Grants {

            @Test
            public void classAllowChanging_and_memberVetoChanging() throws Exception {
                // given
                final ApplicationPermissionValueSet apv = newSet(allowChanging(cComFooBar), vetoChanging(mComFooBar_bip));

                // when, then
                assertThat(apv.grants(mComFooBar_bip, changing()), is(false)); // member-level veto change > class-level allow change
                assertThat(apv.grants(mComFooBar_bip, viewing()), is(true)); // .. but veto doesn't prevent viewing

                // when, then (for some other member)
                assertThat(apv.grants(mComFooBar_bop, changing()), is(true));
                assertThat(apv.grants(mComFooBar_bop, viewing()), is(true));
            }

            @Test
            public void classAllowChanging_and_memberVetoViewing() throws Exception {
                // given
                final ApplicationPermissionValueSet apv = newSet(allowChanging(cComFooBar), vetoViewing(mComFooBar_bip));

                // when, then
                assertThat(apv.grants(mComFooBar_bip, changing()), is(false)); // member-level veto view > class-level allow change
                assertThat(apv.grants(mComFooBar_bip, viewing()), is(false)); // member-level veto view > class-level allow view

                // when, then (for some other member)
                assertThat(apv.grants(mComFooBar_bop, changing()), is(true));
                assertThat(apv.grants(mComFooBar_bop, viewing()), is(true));
            }

            @Test
            public void classAllowViewing_and_memberVetoChanging() throws Exception {
                // given
                final ApplicationPermissionValueSet apv = newSet(allowViewing(cComFooBar), vetoChanging(mComFooBar_bip));

                // when, then
                assertThat(apv.grants(mComFooBar_bip, changing()), is(false));
                assertThat(apv.grants(mComFooBar_bip, viewing()), is(true)); // veto doesn't prevent viewing

                // when, then (for some other member)
                assertThat(apv.grants(mComFooBar_bop, changing()), is(false));
                assertThat(apv.grants(mComFooBar_bop, viewing()), is(true));
            }

            @Test
            public void classAllowViewing_and_memberVetoViewing() throws Exception {
                // given
                final ApplicationPermissionValueSet apv = newSet(allowViewing(cComFooBar), vetoViewing(mComFooBar_bip));

                // when, then
                assertThat(apv.grants(mComFooBar_bip, changing()), is(false)); // member-level veto view > class-level allow change
                assertThat(apv.grants(mComFooBar_bip, viewing()), is(false)); // member-level veto view > class-level allow change

                // when, then (for some other member)
                assertThat(apv.grants(mComFooBar_bop, changing()), is(false));
                assertThat(apv.grants(mComFooBar_bop, viewing()), is(true));
            }

        }

        public static class GivenSetWithClassAndClass extends Grants {

            @Test
            public void classAllowChanging_and_classVetoChanging() throws Exception {
                // given
                final ApplicationPermissionValueSet apv = newSet(allowChanging(cComFooBar), vetoChanging(cComFooBar));

                // when, then
                assertThat(apv.grants(mComFooBar_bip, changing()), is(true));
                assertThat(apv.grants(mComFooBar_bip, viewing()), is(true));
            }

            @Test
            public void classAllowChanging_and_classVetoViewing() throws Exception {
                // given
                final ApplicationPermissionValueSet apv = newSet(allowChanging(cComFooBar), vetoViewing(cComFooBar));

                // when, then
                assertThat(apv.grants(mComFooBar_bip, changing()), is(true));
                assertThat(apv.grants(mComFooBar_bip, viewing()), is(true));
            }

            @Test
            public void classAllowViewing_and_classVetoChanging() throws Exception {
                // given
                final ApplicationPermissionValueSet apv = newSet(allowViewing(cComFooBar), vetoChanging(cComFooBar));

                // when, then
                assertThat(apv.grants(mComFooBar_bip, changing()), is(false));
                assertThat(apv.grants(mComFooBar_bip, viewing()), is(true));
            }

            @Test
            public void classAllowViewing_and_classVetoViewing() throws Exception {
                // given
                final ApplicationPermissionValueSet apv = newSet(allowViewing(cComFooBar), vetoViewing(cComFooBar));

                // when, then
                assertThat(apv.grants(mComFooBar_bip, changing()), is(false));
                assertThat(apv.grants(mComFooBar_bip, viewing()), is(true));
            }

        }

        public static class GivenSetWithClassAndPackage extends Grants {

            @Test
            public void classAllowChanging_and_packageVetoChanging() throws Exception {
                // given
                final ApplicationPermissionValueSet apv = newSet(allowChanging(cComFooBar), vetoChanging(pComFoo));

                // when, then
                assertThat(apv.grants(mComFooBar_bip, changing()), is(true));
                assertThat(apv.grants(mComFooBar_bip, viewing()), is(true));
            }

            @Test
            public void classAllowChanging_and_packageVetoViewing() throws Exception {
                // given
                final ApplicationPermissionValueSet apv = newSet(allowChanging(cComFooBar), vetoViewing(pComFoo));

                // when, then
                assertThat(apv.grants(mComFooBar_bip, changing()), is(true));
                assertThat(apv.grants(mComFooBar_bip, viewing()), is(true));
            }

            @Test
            public void classAllowViewing_and_packageVetoChanging() throws Exception {
                // given
                final ApplicationPermissionValueSet apv = newSet(allowViewing(cComFooBar), vetoChanging(pComFoo));

                // when, then
                assertThat(apv.grants(mComFooBar_bip, changing()), is(false));
                assertThat(apv.grants(mComFooBar_bip, viewing()), is(true));
            }

            @Test
            public void classAllowViewing_and_packageVetoViewing() throws Exception {
                // given
                final ApplicationPermissionValueSet apv = newSet(allowViewing(cComFooBar), vetoViewing(pComFoo));

                // when, then
                assertThat(apv.grants(mComFooBar_bip, changing()), is(false));
                assertThat(apv.grants(mComFooBar_bip, viewing()), is(true));
            }

        }

        public static class GivenSetWithPackageAndMember extends Grants {

            @Test
            public void packageAllowChanging_and_memberVetoChanging() throws Exception {
                // given
                final ApplicationPermissionValueSet apv = newSet(allowChanging(pCom), vetoChanging(mComFooBar_bip));

                // when, then
                assertThat(apv.grants(mComFooBar_bip, changing()), is(false)); // member-level veto change > class-level allow change
                assertThat(apv.grants(mComFooBar_bip, viewing()), is(true)); // .. but veto doesn't prevent viewing

                // when, then (for some other member)
                assertThat(apv.grants(mComFooBar_bop, changing()), is(true));
                assertThat(apv.grants(mComFooBar_bop, viewing()), is(true));
            }

            @Test
            public void packageAllowChanging_and_memberVetoViewing() throws Exception {
                // given
                final ApplicationPermissionValueSet apv = newSet(allowChanging(pCom), vetoViewing(mComFooBar_bip));

                // when, then
                assertThat(apv.grants(mComFooBar_bip, changing()), is(false)); // member-level veto view > class-level allow change
                assertThat(apv.grants(mComFooBar_bip, viewing()), is(false)); // member-level veto view > class-level allow view

                // when, then (for some other member)
                assertThat(apv.grants(mComFooBar_bop, changing()), is(true));
                assertThat(apv.grants(mComFooBar_bop, viewing()), is(true));
            }

            @Test
            public void packageAllowViewing_and_memberVetoChanging() throws Exception {
                // given
                final ApplicationPermissionValueSet apv = newSet(allowViewing(pCom), vetoChanging(mComFooBar_bip));

                // when, then
                assertThat(apv.grants(mComFooBar_bip, changing()), is(false));
                assertThat(apv.grants(mComFooBar_bip, viewing()), is(true)); // veto doesn't prevent viewing

                // when, then (for some other member)
                assertThat(apv.grants(mComFooBar_bop, changing()), is(false));
                assertThat(apv.grants(mComFooBar_bop, viewing()), is(true));
            }

            @Test
            public void packageAllowViewing_and_memberVetoViewing() throws Exception {
                // given
                final ApplicationPermissionValueSet apv = newSet(allowViewing(pCom), vetoViewing(mComFooBar_bip));

                // when, then
                assertThat(apv.grants(mComFooBar_bip, changing()), is(false)); // member-level veto view > class-level allow change
                assertThat(apv.grants(mComFooBar_bip, viewing()), is(false)); // member-level veto view > class-level allow change

                // when, then (for some other member)
                assertThat(apv.grants(mComFooBar_bop, changing()), is(false));
                assertThat(apv.grants(mComFooBar_bop, viewing()), is(true));
            }

        }

        public static class GivenSetWithPackageAndPackage extends Grants {

            @Test
            public void packageAllowChanging_and_packageVetoChanging() throws Exception {
                // given
                final ApplicationPermissionValueSet apv = newSet(allowChanging(pComFoo), vetoChanging(pComFoo));

                // when, then
                assertThat(apv.grants(mComFooBar_bip, changing()), is(true));
                assertThat(apv.grants(mComFooBar_bip, viewing()), is(true));
            }

            @Test
            public void packageAllowChanging_and_packageVetoViewing() throws Exception {
                // given
                final ApplicationPermissionValueSet apv = newSet(allowChanging(pComFoo), vetoViewing(pComFoo));

                // when, then
                assertThat(apv.grants(mComFooBar_bip, changing()), is(true));
                assertThat(apv.grants(mComFooBar_bip, viewing()), is(true));
            }

            @Test
            public void packageAllowViewing_and_packageVetoChanging() throws Exception {
                // given
                final ApplicationPermissionValueSet apv = newSet(allowViewing(pComFoo), vetoChanging(pComFoo));

                // when, then
                assertThat(apv.grants(mComFooBar_bip, changing()), is(false));
                assertThat(apv.grants(mComFooBar_bip, viewing()), is(true));
            }

            @Test
            public void packageAllowViewing_and_packageVetoViewing() throws Exception {
                // given
                final ApplicationPermissionValueSet apv = newSet(allowViewing(pComFoo), vetoViewing(pComFoo));

                // when, then
                assertThat(apv.grants(mComFooBar_bip, changing()), is(false));
                assertThat(apv.grants(mComFooBar_bip, viewing()), is(true));
            }
        }

        public static class GivenSetWithPackageAndSuperPackage extends Grants {

            @Test
            public void packageAllowChanging_and_superPackageVetoChanging() throws Exception {
                // given
                final ApplicationPermissionValueSet apv = newSet(allowChanging(pComFoo), vetoChanging(pCom));

                // when, then
                assertThat(apv.grants(mComFooBar_bip, changing()), is(true));
                assertThat(apv.grants(mComFooBar_bip, viewing()), is(true));
            }

            @Test
            public void packageAllowChanging_and_superPackageVetoViewing() throws Exception {
                // given
                final ApplicationPermissionValueSet apv = newSet(allowChanging(pComFoo), vetoViewing(pCom));

                // when, then
                assertThat(apv.grants(mComFooBar_bip, changing()), is(true));
                assertThat(apv.grants(mComFooBar_bip, viewing()), is(true));
            }

            @Test
            public void packageAllowViewing_and_superPackageVetoChanging() throws Exception {
                // given
                final ApplicationPermissionValueSet apv = newSet(allowViewing(pComFoo), vetoChanging(pCom));

                // when, then
                assertThat(apv.grants(mComFooBar_bip, changing()), is(false));
                assertThat(apv.grants(mComFooBar_bip, viewing()), is(true));
            }

            @Test
            public void packageAllowViewing_and_superPackageVetoViewing() throws Exception {
                // given
                final ApplicationPermissionValueSet apv = newSet(allowViewing(pComFoo), vetoViewing(pCom));

                // when, then
                assertThat(apv.grants(mComFooBar_bip, changing()), is(false));
                assertThat(apv.grants(mComFooBar_bip, viewing()), is(true));
            }
        }

        public static class GivenSetWithSuperPackageAndPackage extends Grants {

            @Test
            public void superPackageAllowChanging_and_packageVetoChanging() throws Exception {
                // given
                final ApplicationPermissionValueSet apv = newSet(allowChanging(pCom), vetoChanging(pComFoo));

                // when, then
                assertThat(apv.grants(mComFooBar_bip, changing()), is(false)); // vetoed
                assertThat(apv.grants(mComFooBar_bip, viewing()), is(true));
            }

            @Test
            public void superPackageAllowChanging_and_VetoViewing() throws Exception {
                // given
                final ApplicationPermissionValueSet apv = newSet(allowChanging(pCom), vetoViewing(pComFoo));

                // when, then
                assertThat(apv.grants(mComFooBar_bip, changing()), is(false)); // vetoed
                assertThat(apv.grants(mComFooBar_bip, viewing()), is(false)); // vetoed
            }

            @Test
            public void superPackageAllowViewing_and_packageVetoChanging() throws Exception {
                // given
                final ApplicationPermissionValueSet apv = newSet(allowViewing(pCom), vetoChanging(pComFoo));

                // when, then
                assertThat(apv.grants(mComFooBar_bip, changing()), is(false));
                assertThat(apv.grants(mComFooBar_bip, viewing()), is(true));
            }

            @Test
            public void superPackageAllowViewing_and_packageVetoViewing() throws Exception {
                // given
                final ApplicationPermissionValueSet apv = newSet(allowViewing(pCom), vetoViewing(pComFoo));

                // when, then
                assertThat(apv.grants(mComFooBar_bip, changing()), is(false));
                assertThat(apv.grants(mComFooBar_bip, viewing()), is(false)); // vetoed
            }
        }

        public static class GivenSetWithPackageAndClass extends Grants {

            @Test
            public void packageAllowChanging_and_classVetoChanging() throws Exception {
                // given
                final ApplicationPermissionValueSet apv = newSet(allowChanging(pCom), vetoChanging(cComFooBar));

                // when, then
                assertThat(apv.grants(mComFooBar_bip, changing()), is(false)); // class-level veto change > package-level allow change
                assertThat(apv.grants(mComFooBar_bip, viewing()), is(true)); // .. but veto doesn't prevent viewing
            }

            @Test
            public void packageAllowChanging_and_classVetoViewing() throws Exception {
                // given
                final ApplicationPermissionValueSet apv = newSet(allowChanging(pCom), vetoViewing(cComFooBar));

                // when, then
                assertThat(apv.grants(mComFooBar_bip, changing()), is(false)); // class-level veto view > package-level allow change
                assertThat(apv.grants(mComFooBar_bip, viewing()), is(false)); // class-level veto view > package-level allow view
            }

            @Test
            public void packageAllowViewing_and_classVetoChanging() throws Exception {
                // given
                final ApplicationPermissionValueSet apv = newSet(allowViewing(pCom), vetoChanging(cComFooBar));

                // when, then
                assertThat(apv.grants(mComFooBar_bip, changing()), is(false));
                assertThat(apv.grants(mComFooBar_bip, viewing()), is(true)); // veto doesn't prevent viewing
            }

            @Test
            public void packageAllowViewing_and_classVetoViewing() throws Exception {
                // given
                final ApplicationPermissionValueSet apv = newSet(allowViewing(pCom), vetoViewing(cComFooBar));

                // when, then
                assertThat(apv.grants(mComFooBar_bip, changing()), is(false)); // class-level veto view > package-level allow change
                assertThat(apv.grants(mComFooBar_bip, viewing()), is(false)); // class-level veto view > package-level allow change
            }

        }

        public static class Complex extends Grants {

            private static ApplicationFeatureId pCom = ApplicationFeatureId.newPackage("com");

            private static ApplicationFeatureId pComFoo = ApplicationFeatureId.newPackage("com.foo");

            private static ApplicationFeatureId cComFooBar = ApplicationFeatureId.newClass("com.foo.Bar");
            private static ApplicationFeatureId mComFooBar_bop = ApplicationFeatureId.newMember("com.foo.Bar", "bop");
            private static ApplicationFeatureId mComFooBar_bip = ApplicationFeatureId.newMember("com.foo.Bar", "bip");
            private static ApplicationFeatureId mComFooBar_bup = ApplicationFeatureId.newMember("com.foo.Bar", "bup");

            private static ApplicationFeatureId cComFooBax = ApplicationFeatureId.newClass("com.foo.Bax");
            private static ApplicationFeatureId mComFooBax_bop = ApplicationFeatureId.newMember("com.foo.Bax", "bop");
            private static ApplicationFeatureId mComFooBax_bip = ApplicationFeatureId.newMember("com.foo.Bax", "bip");

            private static ApplicationFeatureId cComFooBaz = ApplicationFeatureId.newClass("com.foo.Baz");
            private static ApplicationFeatureId mComFooBaz_bop = ApplicationFeatureId.newMember("com.foo.Baz", "bop");
            private static ApplicationFeatureId mComFooBaz_bip = ApplicationFeatureId.newMember("com.foo.Baz", "bip");

            private static ApplicationFeatureId pComFoz = ApplicationFeatureId.newPackage("com.foz");
            private static ApplicationFeatureId cComFozBiz = ApplicationFeatureId.newClass("com.foz.Biz");
            private static ApplicationFeatureId mComFozBiz_bop = ApplicationFeatureId.newMember("com.foz.Biz", "bop");
            private static ApplicationFeatureId mComFozBiz_bip = ApplicationFeatureId.newMember("com.foz.Biz", "bip");

            @Test
            public void packageAllowChanging_and_classVetoChanging() throws Exception {
                // given
                final ApplicationPermissionValueSet apv = newSet(
                        allowChanging(pCom),          // [1]
                        vetoChanging(pComFoz),        // [2]
                        vetoChanging(mComFooBar_bip), // [3]
                        vetoViewing(mComFooBar_bup),  // [4]
                        vetoViewing(cComFooBax),      // [5]
                        vetoChanging(cComFooBaz)      // [6]
                        );

                // when, then
                assertThat(apv.grants(mComFooBar_bip, changing()), is(false)); // vetoed by [3]
                assertThat(apv.grants(mComFooBar_bip, viewing()), is(true));   // allowed by [1]
                assertThat(apv.grants(mComFooBar_bop, changing()), is(true)); // allowed by [1]
                assertThat(apv.grants(mComFooBar_bop, viewing()), is(true));   // allowed by [1]
                assertThat(apv.grants(mComFooBar_bup, changing()), is(false)); // vetoed by [4]
                assertThat(apv.grants(mComFooBar_bup, viewing()), is(false));  // vetoed by [4]

                assertThat(apv.grants(mComFooBax_bip, changing()), is(false)); // vetoed by [5]
                assertThat(apv.grants(mComFooBax_bop, viewing()), is(false));  // vetoed by [5]

                assertThat(apv.grants(mComFooBaz_bip, changing()), is(false)); // vetoed by [6]
                assertThat(apv.grants(mComFooBaz_bop, viewing()), is(true));   // allowed by [1]

                assertThat(apv.grants(mComFozBiz_bip, changing()), is(false)); // vetoed by [2]
                assertThat(apv.grants(mComFozBiz_bop, viewing()), is(true));   // allowed by [1]
            }
        }

    }

    //region > helpers just to make tests easier to read

    static ApplicationPermissionValueSet newSet(ApplicationPermissionValue... applicationPermissionValues) {
        return new ApplicationPermissionValueSet(applicationPermissionValues);
    }

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

    public static class ValueTypeContractTest extends ValueTypeContractTestAbstract<ApplicationPermissionValueSet> {

        @Override
        protected List<ApplicationPermissionValueSet> getObjectsWithSameValue() {
            return Arrays.asList(
                    new ApplicationPermissionValueSet(
                        new ApplicationPermissionValue(ApplicationFeatureId.newPackage("com.mycompany"), ApplicationPermissionRule.ALLOW, ApplicationPermissionMode.CHANGING),
                        new ApplicationPermissionValue(ApplicationFeatureId.newClass("com.mycompany.Bar"), ApplicationPermissionRule.VETO, ApplicationPermissionMode.VIEWING),
                        new ApplicationPermissionValue(ApplicationFeatureId.newMember("com.mycompany.Bar#foo"), ApplicationPermissionRule.ALLOW, ApplicationPermissionMode.VIEWING)),
                    new ApplicationPermissionValueSet(
                        new ApplicationPermissionValue(ApplicationFeatureId.newPackage("com.mycompany"), ApplicationPermissionRule.ALLOW, ApplicationPermissionMode.CHANGING),
                        new ApplicationPermissionValue(ApplicationFeatureId.newClass("com.mycompany.Bar"), ApplicationPermissionRule.VETO, ApplicationPermissionMode.VIEWING),
                        new ApplicationPermissionValue(ApplicationFeatureId.newMember("com.mycompany.Bar#foo"), ApplicationPermissionRule.ALLOW, ApplicationPermissionMode.VIEWING))
                    );
        }

        @Override
        protected List<ApplicationPermissionValueSet> getObjectsWithDifferentValue() {
            return Arrays.asList(
                    // first APV has different FQN
                    new ApplicationPermissionValueSet(
                            new ApplicationPermissionValue(ApplicationFeatureId.newPackage("com.mycompanyX"), ApplicationPermissionRule.ALLOW, ApplicationPermissionMode.CHANGING),
                            new ApplicationPermissionValue(ApplicationFeatureId.newClass("com.mycompany.Bar"), ApplicationPermissionRule.VETO, ApplicationPermissionMode.VIEWING),
                            new ApplicationPermissionValue(ApplicationFeatureId.newMember("com.mycompany.Bar#foo"), ApplicationPermissionRule.ALLOW, ApplicationPermissionMode.VIEWING)),
                    // second APV has different Rule
                    new ApplicationPermissionValueSet(
                            new ApplicationPermissionValue(ApplicationFeatureId.newPackage("com.mycompany"), ApplicationPermissionRule.ALLOW, ApplicationPermissionMode.CHANGING),
                            new ApplicationPermissionValue(ApplicationFeatureId.newClass("com.mycompany.Bar"), ApplicationPermissionRule.ALLOW, ApplicationPermissionMode.VIEWING),
                            new ApplicationPermissionValue(ApplicationFeatureId.newMember("com.mycompany.Bar#foo"), ApplicationPermissionRule.ALLOW, ApplicationPermissionMode.VIEWING)),
                    // third APV has different Mode
                    new ApplicationPermissionValueSet(
                            new ApplicationPermissionValue(ApplicationFeatureId.newPackage("com.mycompany"), ApplicationPermissionRule.ALLOW, ApplicationPermissionMode.CHANGING),
                            new ApplicationPermissionValue(ApplicationFeatureId.newClass("com.mycompany.Bar"), ApplicationPermissionRule.VETO, ApplicationPermissionMode.VIEWING),
                            new ApplicationPermissionValue(ApplicationFeatureId.newMember("com.mycompany.Bar#foo"), ApplicationPermissionRule.ALLOW, ApplicationPermissionMode.CHANGING)),
                    // identical set of APVs, but 1 and 2 in different order
                    new ApplicationPermissionValueSet(
                            new ApplicationPermissionValue(ApplicationFeatureId.newClass("com.mycompany.Bar"), ApplicationPermissionRule.VETO, ApplicationPermissionMode.VIEWING),
                            new ApplicationPermissionValue(ApplicationFeatureId.newPackage("com.mycompany"), ApplicationPermissionRule.ALLOW, ApplicationPermissionMode.CHANGING),
                            new ApplicationPermissionValue(ApplicationFeatureId.newMember("com.mycompany.Bar#foo"), ApplicationPermissionRule.ALLOW, ApplicationPermissionMode.VIEWING)),
                    // identical set of APVs, but 2 and 3 in different order
                    new ApplicationPermissionValueSet(
                            new ApplicationPermissionValue(ApplicationFeatureId.newPackage("com.mycompany"), ApplicationPermissionRule.ALLOW, ApplicationPermissionMode.CHANGING),
                            new ApplicationPermissionValue(ApplicationFeatureId.newMember("com.mycompany.Bar#foo"), ApplicationPermissionRule.ALLOW, ApplicationPermissionMode.VIEWING),
                            new ApplicationPermissionValue(ApplicationFeatureId.newClass("com.mycompany.Bar"), ApplicationPermissionRule.VETO, ApplicationPermissionMode.VIEWING))
                    );
        }

    }



}
