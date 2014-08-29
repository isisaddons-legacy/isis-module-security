package org.isisaddons.module.security.dom.permission;

import org.isisaddons.module.security.dom.feature.ApplicationFeatureId;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ApplicationPermissionSetValueTest {

    private static ApplicationFeatureId pCom = ApplicationFeatureId.newPackage("com");
    private static ApplicationFeatureId pComFoo = ApplicationFeatureId.newPackage("com.foo");
    private static ApplicationFeatureId cComFooBar = ApplicationFeatureId.newClass("com.foo.Bar");
    private static ApplicationFeatureId mComFooBar_bop = ApplicationFeatureId.newMember("com.foo.Bar", "bop");
    private static ApplicationFeatureId mComFooBar_bip = ApplicationFeatureId.newMember("com.foo.Bar", "bip");
    private static ApplicationFeatureId mComFooBar_bup = ApplicationFeatureId.newMember("com.foo.Bar", "bup");

    public static class Implies_and_Refutes extends ApplicationPermissionSetValueTest {

        public static class SetWithMemberOnMember extends Implies_and_Refutes {

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

        public static class SetWithClassOnMember extends Implies_and_Refutes {

            @Test
            public void allowChanging() throws Exception {
                // given
                final ApplicationPermissionValueSet apv = newSet(allowChanging(cComFooBar));

                // when, then
                assertThat(apv.grants(mComFooBar_bip, changing()), is(true));
                assertThat(apv.grants(mComFooBar_bip, viewing()), is(true));

                // when, then (for some other member)
                assertThat(apv.grants(mComFooBar_bop, changing()), is(true));
                assertThat(apv.grants(mComFooBar_bop, viewing()), is(true));
            }

            @Test
            public void allowViewing() throws Exception {
                // given
                final ApplicationPermissionValueSet apv = newSet(allowViewing(cComFooBar));

                // when, then
                assertThat(apv.grants(mComFooBar_bip, changing()), is(false));
                assertThat(apv.grants(mComFooBar_bip, viewing()), is(true));

                // when, then (for some other member)
                assertThat(apv.grants(mComFooBar_bop, changing()), is(false));
                assertThat(apv.grants(mComFooBar_bop, viewing()), is(true));
            }

            @Test
            public void vetoChanging() throws Exception {
                // given
                final ApplicationPermissionValueSet apv = newSet(vetoChanging(cComFooBar));

                // when, then
                assertThat(apv.grants(mComFooBar_bip, changing()), is(false));
                assertThat(apv.grants(mComFooBar_bip, viewing()), is(false));

                // when, then (for some other member)
                assertThat(apv.grants(mComFooBar_bop, changing()), is(false));
                assertThat(apv.grants(mComFooBar_bop, viewing()), is(false));
            }

            @Test
            public void vetoViewing() throws Exception {
                // given
                final ApplicationPermissionValueSet apv = newSet(vetoViewing(cComFooBar));

                // when, then
                assertThat(apv.grants(mComFooBar_bip, changing()), is(false));
                assertThat(apv.grants(mComFooBar_bip, viewing()), is(false));

                // when, then (for some other member)
                assertThat(apv.grants(mComFooBar_bop, changing()), is(false));
                assertThat(apv.grants(mComFooBar_bop, viewing()), is(false));
            }
        }

        public static class SetWithPackageOnMember extends Implies_and_Refutes {

            @Test
            public void allowChanging() throws Exception {
                // given
                final ApplicationPermissionValueSet apv = newSet(allowChanging(pCom));

                // when, then
                assertThat(apv.grants(mComFooBar_bip, changing()), is(true));
                assertThat(apv.grants(mComFooBar_bip, viewing()), is(true));

                // when, then (for some other member)
                assertThat(apv.grants(mComFooBar_bop, changing()), is(true));
                assertThat(apv.grants(mComFooBar_bop, viewing()), is(true));
            }

            @Test
            public void allowViewing() throws Exception {
                // given
                final ApplicationPermissionValueSet apv = newSet(allowViewing(pCom));

                // when, then
                assertThat(apv.grants(mComFooBar_bip, changing()), is(false));
                assertThat(apv.grants(mComFooBar_bip, viewing()), is(true));

                // when, then (for some other member)
                assertThat(apv.grants(mComFooBar_bop, changing()), is(false));
                assertThat(apv.grants(mComFooBar_bop, viewing()), is(true));
            }

            @Test
            public void vetoChanging() throws Exception {
                // given
                final ApplicationPermissionValueSet apv = newSet(vetoChanging(pCom));

                // when, then
                assertThat(apv.grants(mComFooBar_bip, changing()), is(false));
                assertThat(apv.grants(mComFooBar_bip, viewing()), is(false));

                // when, then (for some other member)
                assertThat(apv.grants(mComFooBar_bop, changing()), is(false));
                assertThat(apv.grants(mComFooBar_bop, viewing()), is(false));
            }

            @Test
            public void vetoViewing() throws Exception {
                // given
                final ApplicationPermissionValueSet apv = newSet(vetoViewing(pCom));

                // when, then
                assertThat(apv.grants(mComFooBar_bip, changing()), is(false));
                assertThat(apv.grants(mComFooBar_bip, viewing()), is(false));

                // when, then (for some other member)
                assertThat(apv.grants(mComFooBar_bop, changing()), is(false));
                assertThat(apv.grants(mComFooBar_bop, viewing()), is(false));
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


}
