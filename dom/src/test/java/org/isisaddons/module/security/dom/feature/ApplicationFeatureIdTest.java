package org.isisaddons.module.security.dom.feature;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;

public class ApplicationFeatureIdTest {

    public static class NewPackage extends ApplicationFeatureIdTest {

        @Test
        public void testNewPackage() throws Exception {
            final ApplicationFeatureId applicationFeatureId = ApplicationFeatureId.newPackage("com.mycompany");
            Assert.assertThat(applicationFeatureId.getType(), is(ApplicationFeatureType.PACKAGE));
            Assert.assertThat(applicationFeatureId.getPackageName(), is("com.mycompany"));
            Assert.assertThat(applicationFeatureId.getClassName(), is(nullValue()));
            Assert.assertThat(applicationFeatureId.getMemberName(), is(nullValue()));
        }
    }

    public static class NewClass extends ApplicationFeatureIdTest {

        @Test
        public void testNewClass() throws Exception {
            final ApplicationFeatureId applicationFeatureId = ApplicationFeatureId.newClass("com.mycompany.Bar");
            Assert.assertThat(applicationFeatureId.getType(), is(ApplicationFeatureType.CLASS));
            Assert.assertThat(applicationFeatureId.getPackageName(), is("com.mycompany"));
            Assert.assertThat(applicationFeatureId.getClassName(), is("Bar"));
            Assert.assertThat(applicationFeatureId.getMemberName(), is(nullValue()));
        }
    }

    public static class NewMember extends ApplicationFeatureIdTest {

        @Test
        public void testNewMember() throws Exception {
            final ApplicationFeatureId applicationFeatureId = ApplicationFeatureId.newMember("com.mycompany.Bar", "foo");
            Assert.assertThat(applicationFeatureId.getType(), is(ApplicationFeatureType.MEMBER));
            Assert.assertThat(applicationFeatureId.getPackageName(), is("com.mycompany"));
            Assert.assertThat(applicationFeatureId.getClassName(), is("Bar"));
            Assert.assertThat(applicationFeatureId.getMemberName(), is("foo"));
        }
    }

    public static class GetParentPackage extends ApplicationFeatureIdTest {

        @Test
        public void givenPackageWhenParentIsNotRoot() throws Exception {
            final ApplicationFeatureId applicationFeatureId = ApplicationFeatureId.newPackage("com.mycompany");
            final ApplicationFeatureId parentPackageId = applicationFeatureId.getParentPackageId();

            Assert.assertThat(parentPackageId.getType(), is(ApplicationFeatureType.PACKAGE));
            Assert.assertThat(parentPackageId.getPackageName(), is("com"));
            Assert.assertThat(parentPackageId.getClassName(), is(nullValue()));
            Assert.assertThat(parentPackageId.getMemberName(), is(nullValue()));
        }

        @Test
        public void givenPackageWhenParentIsRoot() throws Exception {
            final ApplicationFeatureId applicationFeatureId = ApplicationFeatureId.newPackage("com");
            final ApplicationFeatureId parentPackageId = applicationFeatureId.getParentPackageId();

            Assert.assertThat(parentPackageId.getType(), is(ApplicationFeatureType.PACKAGE));
            Assert.assertThat(parentPackageId.getPackageName(), is(""));
            Assert.assertThat(parentPackageId.getClassName(), is(nullValue()));
            Assert.assertThat(parentPackageId.getMemberName(), is(nullValue()));
        }

        @Test
        public void givenRootPackage() throws Exception {
            final ApplicationFeatureId applicationFeatureId = ApplicationFeatureId.newPackage("");
            final ApplicationFeatureId parentPackageId = applicationFeatureId.getParentPackageId();
            Assert.assertThat(parentPackageId, is(nullValue()));
        }

        @Test
        public void givenClass() throws Exception {
            final ApplicationFeatureId applicationFeatureId = ApplicationFeatureId.newClass("com.mycompany.Bar");
            final ApplicationFeatureId parentPackageId = applicationFeatureId.getParentPackageId();

            Assert.assertThat(parentPackageId.getType(), is(ApplicationFeatureType.PACKAGE));
            Assert.assertThat(parentPackageId.getPackageName(), is("com.mycompany"));
            Assert.assertThat(parentPackageId.getClassName(), is(nullValue()));
            Assert.assertThat(parentPackageId.getMemberName(), is(nullValue()));
        }

        @Test
        public void givenClassInRootPackage() throws Exception {
            final ApplicationFeatureId applicationFeatureId = ApplicationFeatureId.newClass("Bar");
            final ApplicationFeatureId parentPackageId = applicationFeatureId.getParentPackageId();

            Assert.assertThat(parentPackageId.getType(), is(ApplicationFeatureType.PACKAGE));
            Assert.assertThat(parentPackageId.getPackageName(), is(""));
            Assert.assertThat(parentPackageId.getClassName(), is(nullValue()));
            Assert.assertThat(parentPackageId.getMemberName(), is(nullValue()));
        }


        @Rule
        public ExpectedException expectedException = ExpectedException.none();

        @Test
        public void givenMember() throws Exception {

            expectedException.expect(IllegalStateException.class);

            final ApplicationFeatureId applicationFeatureId = ApplicationFeatureId.newMember("com.mycompany.Bar", "foo");
            applicationFeatureId.getParentPackageId();
        }

    }


}