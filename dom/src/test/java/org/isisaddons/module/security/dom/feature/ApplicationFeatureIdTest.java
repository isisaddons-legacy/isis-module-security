package org.isisaddons.module.security.dom.feature;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class ApplicationFeatureIdTest {

    public static class NewPackage extends ApplicationFeatureIdTest {

        @Test
        public void testNewPackage() throws Exception {
            final ApplicationFeatureId applicationFeatureId = ApplicationFeatureId.newPackage("com.mycompany");
            assertThat(applicationFeatureId.getType(), is(ApplicationFeatureType.PACKAGE));
            assertThat(applicationFeatureId.getPackageName(), is("com.mycompany"));
            assertThat(applicationFeatureId.getClassName(), is(nullValue()));
            assertThat(applicationFeatureId.getMemberName(), is(nullValue()));
        }
    }

    public static class NewClass extends ApplicationFeatureIdTest {

        @Test
        public void testNewClass() throws Exception {
            final ApplicationFeatureId applicationFeatureId = ApplicationFeatureId.newClass("com.mycompany.Bar");
            assertThat(applicationFeatureId.getType(), is(ApplicationFeatureType.CLASS));
            assertThat(applicationFeatureId.getPackageName(), is("com.mycompany"));
            assertThat(applicationFeatureId.getClassName(), is("Bar"));
            assertThat(applicationFeatureId.getMemberName(), is(nullValue()));
        }
    }

    public static class NewMember extends ApplicationFeatureIdTest {

        @Test
        public void using_fullyQualifiedClassName_and_MemberName() throws Exception {
            final ApplicationFeatureId applicationFeatureId = ApplicationFeatureId.newMember("com.mycompany.Bar", "foo");
            assertThat(applicationFeatureId.getType(), is(ApplicationFeatureType.MEMBER));
            assertThat(applicationFeatureId.getPackageName(), is("com.mycompany"));
            assertThat(applicationFeatureId.getClassName(), is("Bar"));
            assertThat(applicationFeatureId.getMemberName(), is("foo"));
        }

        @Test
        public void using_fullyQualifiedName() throws Exception {
            final ApplicationFeatureId applicationFeatureId = ApplicationFeatureId.newMember("com.mycompany.Bar#foo");
            assertThat(applicationFeatureId.getType(), is(ApplicationFeatureType.MEMBER));
            assertThat(applicationFeatureId.getPackageName(), is("com.mycompany"));
            assertThat(applicationFeatureId.getClassName(), is("Bar"));
            assertThat(applicationFeatureId.getMemberName(), is("foo"));
        }

    }

    public static class NewFeature extends ApplicationFeatureIdTest {

        @Test
        public void whenPackage() throws Exception {
            final ApplicationFeatureId applicationFeatureId = ApplicationFeatureId.newFeature(ApplicationFeatureType.PACKAGE, "com.mycompany");
            assertThat(applicationFeatureId, is(ApplicationFeatureId.newPackage("com.mycompany")));
        }

        @Test
        public void whenClass() throws Exception {
            final ApplicationFeatureId applicationFeatureId = ApplicationFeatureId.newFeature(ApplicationFeatureType.CLASS, "com.mycompany.Bar");
            assertThat(applicationFeatureId, is(ApplicationFeatureId.newClass("com.mycompany.Bar")));
        }

        @Test
        public void whenMember() throws Exception {
            final ApplicationFeatureId applicationFeatureId = ApplicationFeatureId.newFeature(ApplicationFeatureType.MEMBER, "com.mycompany.Bar#foo");
            assertThat(applicationFeatureId, is(ApplicationFeatureId.newMember("com.mycompany.Bar","foo")));
        }
    }


    public static class GetParentPackage extends ApplicationFeatureIdTest {

        @Test
        public void givenPackageWhenParentIsNotRoot() throws Exception {
            final ApplicationFeatureId applicationFeatureId = ApplicationFeatureId.newPackage("com.mycompany");
            final ApplicationFeatureId parentPackageId = applicationFeatureId.getParentPackageId();

            assertThat(parentPackageId.getType(), is(ApplicationFeatureType.PACKAGE));
            assertThat(parentPackageId.getPackageName(), is("com"));
            assertThat(parentPackageId.getClassName(), is(nullValue()));
            assertThat(parentPackageId.getMemberName(), is(nullValue()));
        }

        @Test
        public void givenPackageWhenParentIsRoot() throws Exception {
            final ApplicationFeatureId applicationFeatureId = ApplicationFeatureId.newPackage("com");
            final ApplicationFeatureId parentPackageId = applicationFeatureId.getParentPackageId();

            assertThat(parentPackageId.getType(), is(ApplicationFeatureType.PACKAGE));
            assertThat(parentPackageId.getPackageName(), is(""));
            assertThat(parentPackageId.getClassName(), is(nullValue()));
            assertThat(parentPackageId.getMemberName(), is(nullValue()));
        }

        @Test
        public void givenRootPackage() throws Exception {
            final ApplicationFeatureId applicationFeatureId = ApplicationFeatureId.newPackage("");
            final ApplicationFeatureId parentPackageId = applicationFeatureId.getParentPackageId();
            assertThat(parentPackageId, is(nullValue()));
        }

        @Test
        public void givenClass() throws Exception {
            final ApplicationFeatureId applicationFeatureId = ApplicationFeatureId.newClass("com.mycompany.Bar");
            final ApplicationFeatureId parentPackageId = applicationFeatureId.getParentPackageId();

            assertThat(parentPackageId.getType(), is(ApplicationFeatureType.PACKAGE));
            assertThat(parentPackageId.getPackageName(), is("com.mycompany"));
            assertThat(parentPackageId.getClassName(), is(nullValue()));
            assertThat(parentPackageId.getMemberName(), is(nullValue()));
        }

        @Test
        public void givenClassInRootPackage() throws Exception {
            final ApplicationFeatureId applicationFeatureId = ApplicationFeatureId.newClass("Bar");
            final ApplicationFeatureId parentPackageId = applicationFeatureId.getParentPackageId();

            assertThat(parentPackageId.getType(), is(ApplicationFeatureType.PACKAGE));
            assertThat(parentPackageId.getPackageName(), is(""));
            assertThat(parentPackageId.getClassName(), is(nullValue()));
            assertThat(parentPackageId.getMemberName(), is(nullValue()));
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