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

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyPathEvaluator;
import org.isisaddons.module.security.dom.tenancy.WithApplicationTenancy;
import org.isisaddons.module.security.dom.user.ApplicationUsers;
import org.apache.isis.applib.DomainObjectContainer;
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

        final ApplicationTenancyPathEvaluator evaluator = servicesInjector.lookupService(ApplicationTenancyPathEvaluator.class);

        if(evaluator != null) {
            if(!evaluator.handles(cls)) {
                return;
            }
        } else {
            final boolean assignableFrom = WithApplicationTenancy.class.isAssignableFrom(cls);
            if (!assignableFrom) {
                return;
            }
        }
        final DomainObjectContainer container = servicesInjector.lookupService(DomainObjectContainer.class);

        FacetUtil.addFacet(createFacet(processClassContext.getFacetHolder(), evaluator, container));
    }

    @Override
    public void process(final ProcessMethodContext processMethodContext) {
        final Class<?> cls = processMethodContext.getCls();

        final ApplicationTenancyPathEvaluator evaluator = servicesInjector.lookupService(ApplicationTenancyPathEvaluator.class);
        if(evaluator != null) {
            if (!evaluator.handles(cls)) {
                return;
            }
        } else {
            final boolean assignableFrom = WithApplicationTenancy.class.isAssignableFrom(cls);
            if (!assignableFrom) {
                return;
            }
        }
        final DomainObjectContainer container = servicesInjector.lookupService(DomainObjectContainer.class);
        FacetUtil.addFacet(createFacet(processMethodContext.getFacetHolder(), evaluator, container));
    }

    private TenantedAuthorizationFacetDefault createFacet(
            final FacetHolder holder,
            final ApplicationTenancyPathEvaluator evaluator,
            final DomainObjectContainer container) {
        final ApplicationUsers applicationUsers = servicesInjector.lookupService(ApplicationUsers.class);
        final QueryResultsCache queryResultsCache = servicesInjector.lookupService(QueryResultsCache.class);
        return new TenantedAuthorizationFacetDefault(applicationUsers, queryResultsCache, evaluator, container, holder);
    }


    @Override
    public void setServicesInjector(ServicesInjector servicesInjector) {
        this.servicesInjector = servicesInjector;
    }
}
