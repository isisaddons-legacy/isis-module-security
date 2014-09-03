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
            Assert.assertThat(ApplicationFeatureType.PACKAGE.hideMember(), is(true));
            Assert.assertThat(ApplicationFeatureType.CLASS.hideMember(), is(true));
            Assert.assertThat(ApplicationFeatureType.MEMBER.hideMember(), is(false));
        }
    }

    public static class Init extends ApplicationFeatureTypeTest {

        @Test
        public void givenPackage() throws Exception {

            final ApplicationFeatureId applicationFeatureId = new ApplicationFeatureId(ApplicationFeatureType.PACKAGE);

            ApplicationFeatureType.PACKAGE.init(applicationFeatureId, "com.mycompany");

            Assert.assertThat(applicationFeatureId.getPackageName(), is("com.mycompany"));
            Assert.assertThat(applicationFeatureId.getClassName(), is(nullValue()));
            Assert.assertThat(applicationFeatureId.getMemberName(), is(nullValue()));

        }
        @Test
        public void givenClass() throws Exception {

            final ApplicationFeatureId applicationFeatureId = new ApplicationFeatureId(ApplicationFeatureType.CLASS);

            ApplicationFeatureType.CLASS.init(applicationFeatureId, "com.mycompany.Bar");

            Assert.assertThat(applicationFeatureId.getPackageName(), is("com.mycompany"));
            Assert.assertThat(applicationFeatureId.getClassName(), is("Bar"));
            Assert.assertThat(applicationFeatureId.getMemberName(), is(nullValue()));

        }
        @Test
        public void givenMember() throws Exception {

            final ApplicationFeatureId applicationFeatureId = new ApplicationFeatureId(ApplicationFeatureType.MEMBER);

            ApplicationFeatureType.MEMBER.init(applicationFeatureId, "com.mycompany.Bar#foo");

            Assert.assertThat(applicationFeatureId.getPackageName(), is("com.mycompany"));
            Assert.assertThat(applicationFeatureId.getClassName(), is("Bar"));
            Assert.assertThat(applicationFeatureId.getMemberName(), is("foo"));
        }
    }

    public static class EnsurePackage extends ApplicationFeatureTypeTest {

        @Rule
        public ExpectedException expectedException = ExpectedException.none();

        @Test
        public void whenPackage() throws Exception {
            ApplicationFeatureType.ensurePackage(new ApplicationFeatureId(ApplicationFeatureType.PACKAGE));
        }
        @Test
        public void whenClass() throws Exception {
            expectedException.expect(IllegalStateException.class);
            ApplicationFeatureType.ensurePackage(new ApplicationFeatureId(ApplicationFeatureType.CLASS));
        }
        @Test
        public void whenMember() throws Exception {
            expectedException.expect(IllegalStateException.class);
            ApplicationFeatureType.ensurePackage(new ApplicationFeatureId(ApplicationFeatureType.MEMBER));
        }
    }

    public static class EnsurePackageOrClass extends ApplicationFeatureTypeTest {

        @Rule
        public ExpectedException expectedException = ExpectedException.none();

        @Test
        public void whenPackage() throws Exception {
            ApplicationFeatureType.ensurePackageOrClass(new ApplicationFeatureId(ApplicationFeatureType.PACKAGE));
        }
        @Test
        public void whenClass() throws Exception {
            ApplicationFeatureType.ensurePackageOrClass(new ApplicationFeatureId(ApplicationFeatureType.CLASS));
        }
        @Test
        public void whenMember() throws Exception {
            expectedException.expect(IllegalStateException.class);
            ApplicationFeatureType.ensurePackageOrClass(new ApplicationFeatureId(ApplicationFeatureType.MEMBER));
        }

    }


    public static class EnsureClass extends ApplicationFeatureTypeTest {
        @Rule
        public ExpectedException expectedException = ExpectedException.none();

        @Test
        public void whenPackage() throws Exception {
            expectedException.expect(IllegalStateException.class);
            ApplicationFeatureType.ensureClass(new ApplicationFeatureId(ApplicationFeatureType.PACKAGE));
        }
        @Test
        public void whenClass() throws Exception {
            ApplicationFeatureType.ensureClass(new ApplicationFeatureId(ApplicationFeatureType.CLASS));
        }
        @Test
        public void whenMember() throws Exception {
            expectedException.expect(IllegalStateException.class);
            ApplicationFeatureType.ensureClass(new ApplicationFeatureId(ApplicationFeatureType.MEMBER));
        }

    }



    public static class EnsureMember extends ApplicationFeatureTypeTest {
        @Rule
        public ExpectedException expectedException = ExpectedException.none();

        @Test
        public void whenPackage() throws Exception {
            expectedException.expect(IllegalStateException.class);
            ApplicationFeatureType.ensureMember(new ApplicationFeatureId(ApplicationFeatureType.PACKAGE));
        }
        @Test
        public void whenClass() throws Exception {
            expectedException.expect(IllegalStateException.class);
            ApplicationFeatureType.ensureMember(new ApplicationFeatureId(ApplicationFeatureType.CLASS));
        }
        @Test
        public void whenMember() throws Exception {
            ApplicationFeatureType.ensureMember(new ApplicationFeatureId(ApplicationFeatureType.MEMBER));
        }
    }

}