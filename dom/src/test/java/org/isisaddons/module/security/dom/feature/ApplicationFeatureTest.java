package org.isisaddons.module.security.dom.feature;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.containsInAnyOrder;

public class ApplicationFeatureTest {

    public static class NewPackage extends ApplicationFeatureTest {

        @Test
        public void testNewPackage() throws Exception {
            final ApplicationFeature applicationFeature = ApplicationFeature.newPackage("com.mycompany");
            Assert.assertThat(applicationFeature.getType(), is(ApplicationFeatureType.PACKAGE));
            Assert.assertThat(applicationFeature.getPackageName(), is("com.mycompany"));
            Assert.assertThat(applicationFeature.getClassName(), is(nullValue()));
            Assert.assertThat(applicationFeature.getMemberName(), is(nullValue()));
        }
    }

    public static class NewClass extends ApplicationFeatureTest {

        @Test
        public void testNewClass() throws Exception {
            final ApplicationFeature applicationFeature = ApplicationFeature.newClass("com.mycompany.Bar");
            Assert.assertThat(applicationFeature.getType(), is(ApplicationFeatureType.CLASS));
            Assert.assertThat(applicationFeature.getPackageName(), is("com.mycompany"));
            Assert.assertThat(applicationFeature.getClassName(), is("Bar"));
            Assert.assertThat(applicationFeature.getMemberName(), is(nullValue()));
        }
    }

    public static class NewMember extends ApplicationFeatureTest {

        @Test
        public void testNewMember() throws Exception {
            final ApplicationFeature applicationFeature = ApplicationFeature.newMember("com.mycompany.Bar", "foo");
            Assert.assertThat(applicationFeature.getType(), is(ApplicationFeatureType.MEMBER));
            Assert.assertThat(applicationFeature.getPackageName(), is("com.mycompany"));
            Assert.assertThat(applicationFeature.getClassName(), is("Bar"));
            Assert.assertThat(applicationFeature.getMemberName(), is("foo"));
        }
    }

    public static class ViewModelRoundtrip extends ApplicationFeatureTest {

        @Test
        public void whenPackage() throws Exception {
            final ApplicationFeature applicationFeature = ApplicationFeature.newPackage("com.mycompany");

            final String str = applicationFeature.viewModelMemento();
            final ApplicationFeature applicationFeature1 = new ApplicationFeature();
            applicationFeature1.viewModelInit(str);

            Assert.assertThat(applicationFeature1.getType(), is(ApplicationFeatureType.PACKAGE));
            Assert.assertThat(applicationFeature1.getPackageName(), is("com.mycompany"));
            Assert.assertThat(applicationFeature1.getClassName(), is(nullValue()));
            Assert.assertThat(applicationFeature1.getMemberName(), is(nullValue()));
        }

        @Test
        public void whenClass() throws Exception {
            final ApplicationFeature applicationFeature = ApplicationFeature.newClass("com.mycompany.Bar");

            final String str = applicationFeature.viewModelMemento();
            final ApplicationFeature applicationFeature1 = new ApplicationFeature();
            applicationFeature1.viewModelInit(str);

            Assert.assertThat(applicationFeature1.getType(), is(ApplicationFeatureType.CLASS));
            Assert.assertThat(applicationFeature1.getPackageName(), is("com.mycompany"));
            Assert.assertThat(applicationFeature1.getClassName(), is("Bar"));
            Assert.assertThat(applicationFeature1.getMemberName(), is(nullValue()));
        }

        @Test
        public void whenMember() throws Exception {
            final ApplicationFeature applicationFeature = ApplicationFeature.newMember("com.mycompany.Bar", "foo");

            final String str = applicationFeature.viewModelMemento();
            final ApplicationFeature applicationFeature1 = new ApplicationFeature();
            applicationFeature1.viewModelInit(str);

            Assert.assertThat(applicationFeature1.getType(), is(ApplicationFeatureType.MEMBER));
            Assert.assertThat(applicationFeature1.getPackageName(), is("com.mycompany"));
            Assert.assertThat(applicationFeature1.getClassName(), is("Bar"));
            Assert.assertThat(applicationFeature1.getMemberName(), is("foo"));
        }
    }

    public static class GetParentPackage extends ApplicationFeatureTest {

        @Test
        public void givenPackageWhenParentIsNotRoot() throws Exception {
            final ApplicationFeature applicationFeature = ApplicationFeature.newPackage("com.mycompany");
            final ApplicationFeature parentPackage = applicationFeature.getParentPackage();

            Assert.assertThat(parentPackage.getType(), is(ApplicationFeatureType.PACKAGE));
            Assert.assertThat(parentPackage.getPackageName(), is("com"));
            Assert.assertThat(parentPackage.getClassName(), is(nullValue()));
            Assert.assertThat(parentPackage.getMemberName(), is(nullValue()));
        }

        @Test
        public void givenPackageWhenParentIsRoot() throws Exception {
            final ApplicationFeature applicationFeature = ApplicationFeature.newPackage("com");
            final ApplicationFeature parentPackage = applicationFeature.getParentPackage();

            Assert.assertThat(parentPackage.getType(), is(ApplicationFeatureType.PACKAGE));
            Assert.assertThat(parentPackage.getPackageName(), is(""));
            Assert.assertThat(parentPackage.getClassName(), is(nullValue()));
            Assert.assertThat(parentPackage.getMemberName(), is(nullValue()));
        }

        @Test
        public void givenRootPackage() throws Exception {
            final ApplicationFeature applicationFeature = ApplicationFeature.newPackage("");
            final ApplicationFeature parentPackage = applicationFeature.getParentPackage();
            Assert.assertThat(parentPackage, is(nullValue()));
        }

        @Test
        public void givenClass() throws Exception {
            final ApplicationFeature applicationFeature = ApplicationFeature.newClass("com.mycompany.Bar");
            final ApplicationFeature parentPackage = applicationFeature.getParentPackage();

            Assert.assertThat(parentPackage.getType(), is(ApplicationFeatureType.PACKAGE));
            Assert.assertThat(parentPackage.getPackageName(), is("com.mycompany"));
            Assert.assertThat(parentPackage.getClassName(), is(nullValue()));
            Assert.assertThat(parentPackage.getMemberName(), is(nullValue()));
        }

        @Test
        public void givenClassInRootPackage() throws Exception {
            final ApplicationFeature applicationFeature = ApplicationFeature.newClass("Bar");
            final ApplicationFeature parentPackage = applicationFeature.getParentPackage();

            Assert.assertThat(parentPackage.getType(), is(ApplicationFeatureType.PACKAGE));
            Assert.assertThat(parentPackage.getPackageName(), is(""));
            Assert.assertThat(parentPackage.getClassName(), is(nullValue()));
            Assert.assertThat(parentPackage.getMemberName(), is(nullValue()));
        }


        @Rule
        public ExpectedException expectedException = ExpectedException.none();

        @Test
        public void givenMember() throws Exception {

            expectedException.expect(IllegalStateException.class);

            final ApplicationFeature applicationFeature = ApplicationFeature.newMember("com.mycompany.Bar", "foo");
            applicationFeature.getParentPackage();
        }

    }

    public static class GetContents_and_AddToContents extends ApplicationFeatureTest {

        @Rule
        public ExpectedException expectedException = ExpectedException.none();

        @Test
        public void givenPackage_whenAddPackageAndClass() throws Exception {
            final ApplicationFeature applicationFeature = ApplicationFeature.newPackage("com.mycompany");
            final ApplicationFeature packageFeature = ApplicationFeature.newPackage("com.mycompany.flob");
            final ApplicationFeature classFeature = ApplicationFeature.newClass("com.mycompany.Bar");

            applicationFeature.addToContents(packageFeature);
            applicationFeature.addToContents(classFeature);

            Assert.assertThat(applicationFeature.getContents().size(), is(2));
            Assert.assertThat(applicationFeature.getContents(), containsInAnyOrder(packageFeature, classFeature));
        }

        @Test
        public void givenPackage_whenAddMember() throws Exception {

            expectedException.expect(IllegalStateException.class);

            final ApplicationFeature applicationFeature = ApplicationFeature.newPackage("com.mycompany");
            final ApplicationFeature memberFeature = ApplicationFeature.newMember("com.mycompany.Bar", "foo");

            applicationFeature.addToContents(memberFeature);
        }

        @Test
        public void givenClass() throws Exception {

            expectedException.expect(IllegalStateException.class);

            final ApplicationFeature applicationFeature = ApplicationFeature.newClass("com.mycompany.Bar");
            final ApplicationFeature classFeature = ApplicationFeature.newClass("com.mycompany.flob.Bar");

            applicationFeature.addToContents(classFeature);
        }

        @Test
        public void givenMember() throws Exception {

            expectedException.expect(IllegalStateException.class);

            final ApplicationFeature applicationFeature = ApplicationFeature.newMember("com.mycompany.Bar", "foo");
            final ApplicationFeature classFeature = ApplicationFeature.newClass("com.mycompany.flob.Bar");

            applicationFeature.addToContents(classFeature);
        }

    }

    public static class GetMembers_and_AddToMembers extends ApplicationFeatureTest {

        @Rule
        public ExpectedException expectedException = ExpectedException.none();

        @Test
        public void givenPackage() throws Exception {

            expectedException.expect(IllegalStateException.class);

            final ApplicationFeature applicationFeature = ApplicationFeature.newPackage("com.mycompany");
            final ApplicationFeature memberFeature = ApplicationFeature.newMember("com.mycompany.Bar", "foo");

            applicationFeature.addToMembers(memberFeature);
        }

        @Test
        public void givenClass_whenAddMember() throws Exception {

            final ApplicationFeature applicationFeature = ApplicationFeature.newClass("com.mycompany.Bar");
            final ApplicationFeature memberFeature = ApplicationFeature.newMember("com.mycompany.Bar", "foo");
            final ApplicationFeature memberFeature2 = ApplicationFeature.newMember("com.mycompany.Bar", "boz");

            applicationFeature.addToMembers(memberFeature);
            applicationFeature.addToMembers(memberFeature2);

            Assert.assertThat(applicationFeature.getMembers().size(), is(2));
            Assert.assertThat(applicationFeature.getMembers(), containsInAnyOrder(memberFeature, memberFeature2));
        }

        @Test
        public void givenClass_whenAddPackage() throws Exception {

            expectedException.expect(IllegalStateException.class);

            final ApplicationFeature applicationFeature = ApplicationFeature.newClass("com.mycompany.Bar");
            final ApplicationFeature packageFeature = ApplicationFeature.newPackage("com.mycompany");

            applicationFeature.addToMembers(packageFeature);
        }

        @Test
        public void givenClass_whenAddClass() throws Exception {

            expectedException.expect(IllegalStateException.class);

            final ApplicationFeature applicationFeature = ApplicationFeature.newClass("com.mycompany.Bar");
            final ApplicationFeature classFeature = ApplicationFeature.newClass("com.mycompany.Bop");

            applicationFeature.addToMembers(classFeature);
        }

        @Test
        public void givenMember() throws Exception {

            expectedException.expect(IllegalStateException.class);

            final ApplicationFeature applicationFeature = ApplicationFeature.newMember("com.mycompany.Bar", "foo");
            final ApplicationFeature classFeature = ApplicationFeature.newClass("com.mycompany.flob.Bar");

            applicationFeature.addToMembers(classFeature);
        }
    }

}