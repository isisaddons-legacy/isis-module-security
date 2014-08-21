package org.isisaddons.module.security.dom.feature;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class ApplicationFeatureIdTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    public static class NewPackage extends ApplicationFeatureIdTest {

        @Test
        public void testNewPackage() throws Exception {
            // when
            final ApplicationFeatureId applicationFeatureId = ApplicationFeatureId.newPackage("com.mycompany");
            // then
            assertThat(applicationFeatureId.getType(), is(ApplicationFeatureType.PACKAGE));
            assertThat(applicationFeatureId.getPackageName(), is("com.mycompany"));
            assertThat(applicationFeatureId.getClassName(), is(nullValue()));
            assertThat(applicationFeatureId.getMemberName(), is(nullValue()));
        }
    }

    public static class NewClass extends ApplicationFeatureIdTest {

        @Test
        public void testNewClass() throws Exception {
            // when
            final ApplicationFeatureId applicationFeatureId = ApplicationFeatureId.newClass("com.mycompany.Bar");
            // then
            assertThat(applicationFeatureId.getType(), is(ApplicationFeatureType.CLASS));
            assertThat(applicationFeatureId.getPackageName(), is("com.mycompany"));
            assertThat(applicationFeatureId.getClassName(), is("Bar"));
            assertThat(applicationFeatureId.getMemberName(), is(nullValue()));
        }
    }

    public static class NewMember extends ApplicationFeatureIdTest {

        @Test
        public void using_fullyQualifiedClassName_and_MemberName() throws Exception {
            // when
            final ApplicationFeatureId applicationFeatureId = ApplicationFeatureId.newMember("com.mycompany.Bar", "foo");
            // then
            assertThat(applicationFeatureId.getType(), is(ApplicationFeatureType.MEMBER));
            assertThat(applicationFeatureId.getPackageName(), is("com.mycompany"));
            assertThat(applicationFeatureId.getClassName(), is("Bar"));
            assertThat(applicationFeatureId.getMemberName(), is("foo"));
        }

        @Test
        public void using_fullyQualifiedName() throws Exception {
            // when
            final ApplicationFeatureId applicationFeatureId = ApplicationFeatureId.newMember("com.mycompany.Bar#foo");
            // then
            assertThat(applicationFeatureId.getType(), is(ApplicationFeatureType.MEMBER));
            assertThat(applicationFeatureId.getPackageName(), is("com.mycompany"));
            assertThat(applicationFeatureId.getClassName(), is("Bar"));
            assertThat(applicationFeatureId.getMemberName(), is("foo"));
        }

    }

    public static class NewFeature extends ApplicationFeatureIdTest {

        @Test
        public void whenPackage() throws Exception {
            // when
            final ApplicationFeatureId applicationFeatureId = ApplicationFeatureId.newFeature(ApplicationFeatureType.PACKAGE, "com.mycompany");
            // then
            assertThat(applicationFeatureId, is(ApplicationFeatureId.newPackage("com.mycompany")));
        }

        @Test
        public void whenClass() throws Exception {
            // when
            final ApplicationFeatureId applicationFeatureId = ApplicationFeatureId.newFeature(ApplicationFeatureType.CLASS, "com.mycompany.Bar");
            // then
            assertThat(applicationFeatureId, is(ApplicationFeatureId.newClass("com.mycompany.Bar")));
        }

        @Test
        public void whenMember() throws Exception {
            // when
            final ApplicationFeatureId applicationFeatureId = ApplicationFeatureId.newFeature(ApplicationFeatureType.MEMBER, "com.mycompany.Bar#foo");
            // then
            assertThat(applicationFeatureId, is(ApplicationFeatureId.newMember("com.mycompany.Bar","foo")));
        }
    }

    public static class GetParentPackageId extends ApplicationFeatureIdTest {

        @Test
        public void givenPackageWhenParentIsNotRoot() throws Exception {
            // given
            final ApplicationFeatureId applicationFeatureId = ApplicationFeatureId.newPackage("com.mycompany");
            // when
            final ApplicationFeatureId parentPackageId = applicationFeatureId.getParentPackageId();
            // then
            assertThat(parentPackageId.getType(), is(ApplicationFeatureType.PACKAGE));
            assertThat(parentPackageId.getPackageName(), is("com"));
            assertThat(parentPackageId.getClassName(), is(nullValue()));
            assertThat(parentPackageId.getMemberName(), is(nullValue()));
        }

        @Test
        public void givenPackageWhenParentIsRoot() throws Exception {
            // given
            final ApplicationFeatureId applicationFeatureId = ApplicationFeatureId.newPackage("com");
            // when
            final ApplicationFeatureId parentPackageId = applicationFeatureId.getParentPackageId();
            // then
            assertThat(parentPackageId, is(nullValue()));
        }

        @Test
        public void givenRootPackage() throws Exception {
            // given
            final ApplicationFeatureId applicationFeatureId = ApplicationFeatureId.newPackage("");
            // when
            final ApplicationFeatureId parentPackageId = applicationFeatureId.getParentPackageId();
            // then
            assertThat(parentPackageId, is(nullValue()));
        }

        @Test
        public void givenClass() throws Exception {
            // given
            final ApplicationFeatureId applicationFeatureId = ApplicationFeatureId.newClass("com.mycompany.Bar");

            // when
            final ApplicationFeatureId parentPackageId = applicationFeatureId.getParentPackageId();

            // then
            assertThat(parentPackageId.getType(), is(ApplicationFeatureType.PACKAGE));
            assertThat(parentPackageId.getPackageName(), is("com.mycompany"));
            assertThat(parentPackageId.getClassName(), is(nullValue()));
            assertThat(parentPackageId.getMemberName(), is(nullValue()));
        }

        @Test
        public void givenClassInRootPackage() throws Exception {
            // given
            final ApplicationFeatureId applicationFeatureId = ApplicationFeatureId.newClass("Bar");

            // when
            final ApplicationFeatureId parentPackageId = applicationFeatureId.getParentPackageId();

            // then
            assertThat(parentPackageId.getType(), is(ApplicationFeatureType.PACKAGE));
            assertThat(parentPackageId.getPackageName(), is(""));
            assertThat(parentPackageId.getClassName(), is(nullValue()));
            assertThat(parentPackageId.getMemberName(), is(nullValue()));
        }

        @Test
        public void givenMember() throws Exception {

            // given
            final ApplicationFeatureId applicationFeatureId = ApplicationFeatureId.newMember("com.mycompany.Bar", "foo");

            // then
            expectedException.expect(IllegalStateException.class);

            // when
            applicationFeatureId.getParentPackageId();
        }

    }

    public static class GetParentClass extends ApplicationFeatureIdTest {

        @Test
        public void givenMember() throws Exception {
            // given
            final ApplicationFeatureId applicationFeatureId = ApplicationFeatureId.newMember("com.mycompany.Bar", "foo");

            // when
            final ApplicationFeatureId parentClassId = applicationFeatureId.getParentClassId();

            // then
            assertThat(parentClassId.getType(), is(ApplicationFeatureType.CLASS));
            assertThat(parentClassId.getPackageName(), is("com.mycompany"));
            assertThat(parentClassId.getClassName(), is("Bar"));
            assertThat(parentClassId.getMemberName(), is(nullValue()));
        }

        @Test
        public void givenPackage() throws Exception {
            // given
            final ApplicationFeatureId applicationFeatureId = ApplicationFeatureId.newPackage("com");

            // then
            expectedException.expect(IllegalStateException.class);

            // when
            applicationFeatureId.getParentClassId();
        }

        @Test
        public void givenClass() throws Exception {

            // given
            final ApplicationFeatureId applicationFeatureId = ApplicationFeatureId.newClass("com.mycompany.Bar");

            // then
            expectedException.expect(IllegalStateException.class);

            // when
            applicationFeatureId.getParentClassId();
        }
    }


}