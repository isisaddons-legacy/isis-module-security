/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
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

package org.isisaddons.module.security.facets;

import org.isisaddons.module.security.dom.tenancy.WithApplicationTenancy;
import org.isisaddons.module.security.dom.user.ApplicationUsers;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;
import org.apache.isis.core.metamodel.facetapi.FacetHolder;
import org.apache.isis.core.metamodel.facetapi.FacetUtil;
import org.apache.isis.core.metamodel.facetapi.FeatureType;
import org.apache.isis.core.metamodel.facets.FacetFactoryAbstract;
import org.apache.isis.core.metamodel.runtimecontext.ServicesInjector;
import org.apache.isis.core.metamodel.runtimecontext.ServicesInjectorAware;

public class TenantedAuthorizationFacetFactory extends FacetFactoryAbstract implements ServicesInjectorAware {

    private ServicesInjector servicesInjector;

    public TenantedAuthorizationFacetFactory() {
        super(FeatureType.EVERYTHING_BUT_PARAMETERS);
    }

    @Override
    public void process(final ProcessClassContext processClassContext) {
        final Class<?> cls = processClassContext.getCls();
        final boolean assignableFrom = WithApplicationTenancy.class.isAssignableFrom(cls);
        if (!assignableFrom) {
            return;
        }

        FacetUtil.addFacet(createFacet(processClassContext.getFacetHolder()));
    }

    @Override
    public void process(final ProcessMethodContext processMethodContext) {
        final Class<?> cls = processMethodContext.getCls();
        final boolean assignableFrom = WithApplicationTenancy.class.isAssignableFrom(cls);
        if (!assignableFrom) {
            return;
        }
        FacetUtil.addFacet(createFacet(processMethodContext.getFacetHolder()));
    }

    private TenantedAuthorizationFacetDefault createFacet(final FacetHolder holder) {
        final ApplicationUsers applicationUsers = getApplicationUsers();
        final QueryResultsCache queryResultsCache = getQueryResultsCache();
        return new TenantedAuthorizationFacetDefault(applicationUsers, queryResultsCache, holder);
    }

    private ApplicationUsers getApplicationUsers() {
        return servicesInjector.lookupService(ApplicationUsers.class);
    }
    private QueryResultsCache getQueryResultsCache() {
        return servicesInjector.lookupService(QueryResultsCache.class);
    }


    @Override
    public void setServicesInjector(ServicesInjector servicesInjector) {
        this.servicesInjector = servicesInjector;
    }
}
