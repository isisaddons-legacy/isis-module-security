package org.isisaddons.module.security.dom.permission;

import org.isisaddons.module.security.dom.feature.ApplicationFeatureId;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ApplicationPermissionValueTest {

    private static ApplicationFeatureId pCom = ApplicationFeatureId.newPackage("com");
    private static ApplicationFeatureId pComFoo = ApplicationFeatureId.newPackage("com.foo");
    private static ApplicationFeatureId cComFooBar = ApplicationFeatureId.newClass("com.foo.Bar");
    private static ApplicationFeatureId mComFooBar_bop = ApplicationFeatureId.newMember("com.foo.Bar", "bop");
    private static ApplicationFeatureId mComFooBar_bip = ApplicationFeatureId.newMember("com.foo.Bar", "bip");
    private static ApplicationFeatureId mComFooBar_bup = ApplicationFeatureId.newMember("com.foo.Bar", "bup");

    public static class Implies_and_Refutes extends ApplicationPermissionValueTest {

        public static class MemberOnMember extends Implies_and_Refutes {

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

        public static class ClassOnMember extends Implies_and_Refutes {

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

        public static class PackageOnMember extends Implies_and_Refutes {

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
