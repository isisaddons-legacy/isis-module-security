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

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;
import org.apache.isis.core.metamodel.facetapi.FacetHolder;
import org.apache.isis.core.metamodel.facetapi.FacetUtil;
import org.apache.isis.core.metamodel.facetapi.FeatureType;
import org.apache.isis.core.metamodel.facets.FacetFactoryAbstract;

import org.apache.isis.core.metamodel.services.ServicesInjector;
import org.apache.isis.core.metamodel.services.ServicesInjectorAware;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyPathEvaluator;
import org.isisaddons.module.security.dom.tenancy.WithApplicationTenancy;
import org.isisaddons.module.security.dom.user.ApplicationUserRepository;

public class TenantedAuthorizationFacetFactory extends FacetFactoryAbstract implements ServicesInjectorAware {

    private ServicesInjector servicesInjector;

    private final ApplicationTenancyPathEvaluatorDefault defaultEvaluator;
    public TenantedAuthorizationFacetFactory() {
        super(FeatureType.EVERYTHING);

        defaultEvaluator = new ApplicationTenancyPathEvaluatorDefault();
    }

    @Override
    public void process(final ProcessClassContext processClassContext) {
        final Class<?> cls = processClassContext.getCls();

        FacetHolder facetHolder = processClassContext.getFacetHolder();
        FacetUtil.addFacet(createFacet(cls, facetHolder));
    }

    @Override
    public void process(final ProcessMethodContext processMethodContext) {
        final Class<?> cls = processMethodContext.getCls();
        FacetHolder facetHolder = processMethodContext.getFacetHolder();
        FacetUtil.addFacet(createFacet(cls, facetHolder));
    }

    @Override
    public void processParams(final ProcessParameterContext processParameterContext) {
        final Class<?> cls = processParameterContext.getCls();
        FacetHolder facetHolder = processParameterContext.getFacetHolder();
        FacetUtil.addFacet(createFacet(cls, facetHolder));
    }

    private TenantedAuthorizationFacetDefault createFacet(
            final Class<?> cls, final FacetHolder holder) {

        ApplicationTenancyPathEvaluator evaluator = servicesInjector.lookupService(ApplicationTenancyPathEvaluator.class);
        if(evaluator == null) {
            evaluator = defaultEvaluator;
        }
        if(!evaluator.handles(cls)) {
            return null;
        }

        final ApplicationUserRepository applicationUserRepository =
                servicesInjector.lookupService(ApplicationUserRepository.class);
        final QueryResultsCache queryResultsCache = servicesInjector.lookupService(QueryResultsCache.class);
        final DomainObjectContainer container = servicesInjector.lookupService(DomainObjectContainer.class);

        return new TenantedAuthorizationFacetDefault(applicationUserRepository, queryResultsCache, evaluator, container, holder);
    }

    static class ApplicationTenancyPathEvaluatorDefault implements ApplicationTenancyPathEvaluator {

        @Override
        public boolean handles(final Class<?> cls) {
            return WithApplicationTenancy.class.isAssignableFrom(cls);
        }

        public String applicationTenancyPathFor(final Object domainObject) {
            // always safe, facet factory only installs facet for classes implementing WithApplicationTenancy
            final WithApplicationTenancy tenantedObject = (WithApplicationTenancy) domainObject;

            final ApplicationTenancy objectTenancy = tenantedObject.getApplicationTenancy();
            final String objectTenancyPath = objectTenancy == null ? null : objectTenancy.getPath();
            return objectTenancyPath;
        }
    }



    @Override
    public void setServicesInjector(ServicesInjector servicesInjector) {
        this.servicesInjector = servicesInjector;
    }
}
