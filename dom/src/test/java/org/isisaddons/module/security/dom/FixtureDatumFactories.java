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
package org.isisaddons.module.security.dom;

import com.danhaywood.java.testsupport.coverage.PojoTester;

import org.joda.time.LocalDate;

import org.apache.isis.core.metamodel.services.appfeat.ApplicationFeature;
import org.apache.isis.core.metamodel.services.appfeat.ApplicationFeatureId;

import org.isisaddons.module.security.dom.permission.ApplicationPermission;
import org.isisaddons.module.security.dom.role.ApplicationRole;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.isisaddons.module.security.dom.user.ApplicationUser;

public class FixtureDatumFactories {

    public FixtureDatumFactories(){}

    public static PojoTester.FixtureDatumFactory<LocalDate> dates() {
        return new PojoTester.FixtureDatumFactory<>(LocalDate.class, null, new LocalDate(2012, 7, 19), new LocalDate(2012, 7, 20), new LocalDate(2012, 8, 19), new LocalDate(2013, 7, 19));
    }

    public static PojoTester.FixtureDatumFactory<Boolean> booleans() {
        return new PojoTester.FixtureDatumFactory<>(Boolean.class, null, Boolean.FALSE, Boolean.TRUE);
    }

    public static PojoTester.FixtureDatumFactory<ApplicationFeatureId> featureIds() {
        return new PojoTester.FixtureDatumFactory<>(ApplicationFeatureId.class, ApplicationFeatureId.newPackage("com.mycompany"), ApplicationFeatureId.newClass("com.mycompany.Foo"), ApplicationFeatureId.newMember("com.mycompany.Foo", "bar"));
    }

    public static PojoTester.FixtureDatumFactory<ApplicationRole> roles() {
        return new PojoTester.FixtureDatumFactory<>(ApplicationRole.class, new ApplicationRole(), new ApplicationRole());
    }

    public static PojoTester.FixtureDatumFactory<ApplicationUser> users() {
        return new PojoTester.FixtureDatumFactory<>(ApplicationUser.class, new ApplicationUser(), new ApplicationUser());
    }

    public static PojoTester.FixtureDatumFactory<ApplicationPermission> permissions() {
        return new PojoTester.FixtureDatumFactory<>(ApplicationPermission.class, new ApplicationPermission(), new ApplicationPermission());
    }

    public static PojoTester.FixtureDatumFactory<ApplicationTenancy> tenancies() {
        return new PojoTester.FixtureDatumFactory<>(ApplicationTenancy.class, new ApplicationTenancy(), new ApplicationTenancy());
    }

    public static PojoTester.FixtureDatumFactory<ApplicationFeature> features() {
        return new PojoTester.FixtureDatumFactory<>(ApplicationFeature.class, new ApplicationFeature(), new ApplicationFeature());
    }

}
