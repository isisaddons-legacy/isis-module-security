package org.isisaddons.module.security.dom.feature;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;

public class ApplicationFeatureTypeTest {

    public static class HideClassName extends ApplicationFeatureTypeTest {
        @Test
        public void all() throws Exception {
            Assert.assertThat(ApplicationFeatureType.PACKAGE.hideClassName(), is(true));
            Assert.assertThat(ApplicationFeatureType.CLASS.hideClassName(), is(false));
            Assert.assertThat(ApplicationFeatureType.MEMBER.hideClassName(), is(false));
        }
    }

    public static class HideMemberName extends ApplicationFeatureTypeTest {

        @Test
        public void all() throws Exception {
            Assert.assertThat(ApplicationFeatureType.PACKAGE.hideMemberName(), is(true));
            Assert.assertThat(ApplicationFeatureType.CLASS.hideMemberName(), is(true));
            Assert.assertThat(ApplicationFeatureType.MEMBER.hideMemberName(), is(false));
        }
    }

    public static class Init extends ApplicationFeatureTypeTest {

        @Test
        public void givenPackage() throws Exception {

            final ApplicationFeature applicationFeature = new ApplicationFeature(ApplicationFeatureType.PACKAGE);

            ApplicationFeatureType.PACKAGE.init(applicationFeature, "com.mycompany");

            Assert.assertThat(applicationFeature.getPackageName(), is("com.mycompany"));
            Assert.assertThat(applicationFeature.getClassName(), is(nullValue()));
            Assert.assertThat(applicationFeature.getMemberName(), is(nullValue()));

        }
        @Test
        public void givenClass() throws Exception {

            final ApplicationFeature applicationFeature = new ApplicationFeature(ApplicationFeatureType.CLASS);

            ApplicationFeatureType.CLASS.init(applicationFeature, "com.mycompany.Bar");

            Assert.assertThat(applicationFeature.getPackageName(), is("com.mycompany"));
            Assert.assertThat(applicationFeature.getClassName(), is("Bar"));
            Assert.assertThat(applicationFeature.getMemberName(), is(nullValue()));

        }
        @Test
        public void givenMember() throws Exception {

            final ApplicationFeature applicationFeature = new ApplicationFeature(ApplicationFeatureType.MEMBER);

            ApplicationFeatureType.MEMBER.init(applicationFeature, "com.mycompany.Bar#foo");

            Assert.assertThat(applicationFeature.getPackageName(), is("com.mycompany"));
            Assert.assertThat(applicationFeature.getClassName(), is("Bar"));
            Assert.assertThat(applicationFeature.getMemberName(), is("foo"));
        }
    }

    public static class EnsurePackage extends ApplicationFeatureTypeTest {

        @Rule
        public ExpectedException expectedException = ExpectedException.none();

        @Test
        public void whenPackage() throws Exception {
            ApplicationFeatureType.ensurePackage(new ApplicationFeature(ApplicationFeatureType.PACKAGE));
        }
        @Test
        public void whenClass() throws Exception {
            expectedException.expect(IllegalStateException.class);
            ApplicationFeatureType.ensurePackage(new ApplicationFeature(ApplicationFeatureType.CLASS));
        }
        @Test
        public void whenMember() throws Exception {
            expectedException.expect(IllegalStateException.class);
            ApplicationFeatureType.ensurePackage(new ApplicationFeature(ApplicationFeatureType.MEMBER));
        }
    }

    public static class EnsurePackageOrClass extends ApplicationFeatureTypeTest {

        @Rule
        public ExpectedException expectedException = ExpectedException.none();

        @Test
        public void whenPackage() throws Exception {
            ApplicationFeatureType.ensurePackageOrClass(new ApplicationFeature(ApplicationFeatureType.PACKAGE));
        }
        @Test
        public void whenClass() throws Exception {
            ApplicationFeatureType.ensurePackageOrClass(new ApplicationFeature(ApplicationFeatureType.CLASS));
        }
        @Test
        public void whenMember() throws Exception {
            expectedException.expect(IllegalStateException.class);
            ApplicationFeatureType.ensurePackageOrClass(new ApplicationFeature(ApplicationFeatureType.MEMBER));
        }

    }


    public static class EnsureClass extends ApplicationFeatureTypeTest {
        @Rule
        public ExpectedException expectedException = ExpectedException.none();

        @Test
        public void whenPackage() throws Exception {
            expectedException.expect(IllegalStateException.class);
            ApplicationFeatureType.ensureClass(new ApplicationFeature(ApplicationFeatureType.PACKAGE));
        }
        @Test
        public void whenClass() throws Exception {
            ApplicationFeatureType.ensureClass(new ApplicationFeature(ApplicationFeatureType.CLASS));
        }
        @Test
        public void whenMember() throws Exception {
            expectedException.expect(IllegalStateException.class);
            ApplicationFeatureType.ensureClass(new ApplicationFeature(ApplicationFeatureType.MEMBER));
        }

    }



    public static class EnsureMember extends ApplicationFeatureTypeTest {
        @Rule
        public ExpectedException expectedException = ExpectedException.none();

        @Test
        public void whenPackage() throws Exception {
            expectedException.expect(IllegalStateException.class);
            ApplicationFeatureType.ensureMember(new ApplicationFeature(ApplicationFeatureType.PACKAGE));
        }
        @Test
        public void whenClass() throws Exception {
            expectedException.expect(IllegalStateException.class);
            ApplicationFeatureType.ensureMember(new ApplicationFeature(ApplicationFeatureType.CLASS));
        }
        @Test
        public void whenMember() throws Exception {
            ApplicationFeatureType.ensureMember(new ApplicationFeature(ApplicationFeatureType.MEMBER));
        }
    }

}