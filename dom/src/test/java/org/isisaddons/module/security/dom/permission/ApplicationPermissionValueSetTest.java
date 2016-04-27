package org.isisaddons.module.security.dom.permission;

import org.junit.Test;

import org.apache.isis.core.metamodel.services.appfeat.ApplicationFeatureId;

import org.isisaddons.module.security.dom.SerializationContractTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ApplicationPermissionValueSetTest {

    public static class Serialization extends SerializationContractTest {

        final ApplicationFeatureId barClass = ApplicationFeatureId.newClass("com.foo.Bar");
        final ApplicationFeatureId bipMember = ApplicationFeatureId.newMember("com.foo.Bar#bip");
        final ApplicationPermissionValue apv1 = new ApplicationPermissionValue(barClass, ApplicationPermissionRule.ALLOW, ApplicationPermissionMode.CHANGING);
        final ApplicationPermissionValue apv2 = new ApplicationPermissionValue(bipMember, ApplicationPermissionRule.VETO, ApplicationPermissionMode.VIEWING);

        @Test
        public void roundtrip() throws Exception {

            final ApplicationPermissionValueSet original = new ApplicationPermissionValueSet(apv1, apv2);

            final ApplicationPermissionValueSet roundtripped = roundtripSerialization(original);

            assertThat(roundtripped.evaluate(barClass, ApplicationPermissionMode.CHANGING).isGranted(), is(true));
        }

    }
}