package org.isisaddons.module.security.dom.feature;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsInAnyOrder;

public class ApplicationFeatureTest {

    public static class GetContents_and_AddToContents extends ApplicationFeatureTest {

        @Rule
        public ExpectedException expectedException = ExpectedException.none();

        @Test
        public void givenPackage_whenAddPackageAndClass() throws Exception {
            final ApplicationFeature applicationFeature = new ApplicationFeature(ApplicationFeatureId.newPackage("com.mycompany"));
            final ApplicationFeatureId packageFeatureId = ApplicationFeatureId.newPackage("com.mycompany.flob");
            final ApplicationFeatureId classFeatureId = ApplicationFeatureId.newClass("com.mycompany.Bar");

            applicationFeature.addToContents(packageFeatureId);
            applicationFeature.addToContents(classFeatureId);

            Assert.assertThat(applicationFeature.getContents().size(), is(2));
            Assert.assertThat(applicationFeature.getContents(), containsInAnyOrder(packageFeatureId, classFeatureId));
        }

        @Test
        public void givenPackage_whenAddMember() throws Exception {

            expectedException.expect(IllegalStateException.class);

            final ApplicationFeature applicationFeature = new ApplicationFeature(ApplicationFeatureId.newPackage("com.mycompany"));
            final ApplicationFeatureId memberFeatureId = ApplicationFeatureId.newMember("com.mycompany.Bar", "foo");

            applicationFeature.addToContents(memberFeatureId);
        }

        @Test
        public void givenClass() throws Exception {

            expectedException.expect(IllegalStateException.class);

            final ApplicationFeature applicationFeature = new ApplicationFeature(ApplicationFeatureId.newClass("com.mycompany.Bar"));
            final ApplicationFeatureId classFeatureId = ApplicationFeatureId.newClass("com.mycompany.flob.Bar");

            applicationFeature.addToContents(classFeatureId);
        }

        @Test
        public void givenMember() throws Exception {

            expectedException.expect(IllegalStateException.class);

            final ApplicationFeature applicationFeature = new ApplicationFeature(ApplicationFeatureId.newMember("com.mycompany.Bar", "foo"));
            final ApplicationFeatureId classFeatureId = ApplicationFeatureId.newClass("com.mycompany.flob.Bar");

            applicationFeature.addToContents(classFeatureId);
        }

    }

    public static class GetMembers_and_AddToMembers extends ApplicationFeatureTest {

        @Rule
        public ExpectedException expectedException = ExpectedException.none();

        @Test
        public void givenPackage() throws Exception {

            expectedException.expect(IllegalStateException.class);

            final ApplicationFeature applicationFeature = new ApplicationFeature(ApplicationFeatureId.newPackage("com.mycompany"));
            final ApplicationFeatureId memberFeatureId = ApplicationFeatureId.newMember("com.mycompany.Bar", "foo");

            applicationFeature.addToMembers(memberFeatureId, ApplicationMemberType.PROPERTY);
        }

        @Test
        public void givenClass_whenAddMember() throws Exception {

            final ApplicationFeature applicationFeature = new ApplicationFeature(ApplicationFeatureId.newClass("com.mycompany.Bar"));
            final ApplicationFeatureId memberFeatureId = ApplicationFeatureId.newMember("com.mycompany.Bar", "foo");
            final ApplicationFeatureId memberFeatureId2 = ApplicationFeatureId.newMember("com.mycompany.Bar", "boz");

            applicationFeature.addToMembers(memberFeatureId, ApplicationMemberType.PROPERTY);
            applicationFeature.addToMembers(memberFeatureId2, ApplicationMemberType.PROPERTY);

            Assert.assertThat(applicationFeature.getProperties().size(), is(2));
            Assert.assertThat(applicationFeature.getProperties(), containsInAnyOrder(memberFeatureId, memberFeatureId2));
        }

        @Test
        public void givenClass_whenAddPackage() throws Exception {

            expectedException.expect(IllegalStateException.class);

            final ApplicationFeature applicationFeature = new ApplicationFeature(ApplicationFeatureId.newClass("com.mycompany.Bar"));
            final ApplicationFeatureId packageFeatureId = ApplicationFeatureId.newPackage("com.mycompany");

            applicationFeature.addToMembers(packageFeatureId, ApplicationMemberType.PROPERTY);
        }

        @Test
        public void givenClass_whenAddClass() throws Exception {

            expectedException.expect(IllegalStateException.class);

            final ApplicationFeature applicationFeature = new ApplicationFeature(ApplicationFeatureId.newClass("com.mycompany.Bar"));
            final ApplicationFeatureId classFeatureId = ApplicationFeatureId.newClass("com.mycompany.Bop");

            applicationFeature.addToMembers(classFeatureId, ApplicationMemberType.PROPERTY);
        }

        @Test
        public void givenMember() throws Exception {

            expectedException.expect(IllegalStateException.class);

            final ApplicationFeature applicationFeature = new ApplicationFeature(ApplicationFeatureId.newMember("com.mycompany.Bar", "foo"));
            final ApplicationFeatureId classFeatureId = ApplicationFeatureId.newClass("com.mycompany.flob.Bar");

            applicationFeature.addToMembers(classFeatureId, ApplicationMemberType.PROPERTY);
        }
    }
}