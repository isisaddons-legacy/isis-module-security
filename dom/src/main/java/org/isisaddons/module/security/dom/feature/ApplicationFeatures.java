/*
 *  Copyright 2014 Jeroen van der Wal
 *
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

import java.util.List;

import org.isisaddons.module.security.service.ApplicationSecurityService;

import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.core.metamodel.services.devutils.MetaModelRow;

@DomainService
public class ApplicationFeatures extends AbstractFactoryAndRepository {

    @Programmatic
    public List<ApplicationFeature> allFeatures() {
        return allMatches(new QueryDefault<ApplicationFeature>(ApplicationFeature.class, "allByName"));
    }

    @Programmatic
    public List<ApplicationFeature> findByPackageName(String packageName) {
        return allMatches(new QueryDefault<ApplicationFeature>(ApplicationFeature.class, "findByPackageName", "packageName", packageName));
    }

    @Programmatic
    public ApplicationFeature findFeatureByName(String name) {
        return firstMatch(new QueryDefault<ApplicationFeature>(ApplicationFeature.class, "findByName", "name", name));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Programmatic
    public List<String> findPackageName(String searchString) {
        String escapedString = searchString == null ? null : java.util.regex.Pattern.quote(searchString.replace("*", ".*"));
        return allMatches(new QueryDefault(ApplicationFeature.class, "findPackageName", "matcher", escapedString));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Programmatic
    public List<String> allPackageNames() {
        return allMatches(new QueryDefault(ApplicationFeature.class, "allPackageNames"));
    }

    @Programmatic
    public ApplicationFeature addFeature(ApplicationSecurityService applicationSecurityService, MetaModelRow row) {
        final String name = row.getPackageName().concat(".").concat(row.getClassName()).concat(".").concat(row.getMemberName());

        final ApplicationFeature feature = findFeatureByName(name);
        if (feature != null) {
            return feature;
        }
        ApplicationFeature newFeature = newTransientInstance(ApplicationFeature.class);
        newFeature.setName(name);
        newFeature.setClassName(row.getClassName());
        newFeature.setPackageName(row.getPackageName());
        newFeature.setClassType(row.getClassType());
        newFeature.setMemberName(row.getMemberName());
        newFeature.setMemberType(row.getType());
        persist(newFeature);
        return newFeature;
    }

}
