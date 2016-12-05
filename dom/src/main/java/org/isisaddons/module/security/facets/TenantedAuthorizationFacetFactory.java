/*
 *  Copyright 2014~date Dan Haywood
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

package org.isisaddons.module.security.facets;

import java.util.List;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;
import org.apache.isis.applib.services.user.UserService;
import org.apache.isis.core.metamodel.facetapi.FacetHolder;
import org.apache.isis.core.metamodel.facetapi.FacetUtil;
import org.apache.isis.core.metamodel.facetapi.FeatureType;
import org.apache.isis.core.metamodel.facets.FacetFactoryAbstract;
import org.apache.isis.core.metamodel.services.ServicesInjector;
import org.apache.isis.core.metamodel.services.ServicesInjectorAware;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyEvaluator;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyPathEvaluator;
import org.isisaddons.module.security.dom.tenancy.HasAtPath;
import org.isisaddons.module.security.dom.user.ApplicationUserRepository;

public class TenantedAuthorizationFacetFactory extends FacetFactoryAbstract implements ServicesInjectorAware {

    private ServicesInjector servicesInjector;


    public TenantedAuthorizationFacetFactory() {
        super(FeatureType.EVERYTHING);
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

        List<ApplicationTenancyEvaluator> evaluators = servicesInjector.lookupServices(ApplicationTenancyEvaluator.class);
        if(isNullOrEmpty(evaluators)) {
            evaluators = Lists.newArrayList();

            // fallback to previous SPI
            List<ApplicationTenancyPathEvaluator> pathEvaluators =
                    servicesInjector.lookupServices(ApplicationTenancyPathEvaluator.class);
            if(isNullOrEmpty(pathEvaluators)) {
                pathEvaluators = Lists.newArrayList();
                final ApplicationTenancyPathEvaluator pathEvaluator = new ApplicationTenancyPathEvaluatorDefault();
                servicesInjector.injectServicesInto(pathEvaluator);
                pathEvaluators.add(pathEvaluator);
            }

            for (ApplicationTenancyPathEvaluator pathEvaluator : pathEvaluators) {
                final ApplicationTenancyEvaluatorUsingPaths evaluator = new ApplicationTenancyEvaluatorUsingPaths(pathEvaluator);
                servicesInjector.injectServicesInto(evaluator);
                evaluators.add(evaluator);
            }
        }

        final ImmutableList<ApplicationTenancyEvaluator> evaluatorsForCls =
                FluentIterable.from(evaluators).filter(new Predicate<ApplicationTenancyEvaluator>() {
            @Override
            public boolean apply(ApplicationTenancyEvaluator applicationTenancyEvaluator) {
                return applicationTenancyEvaluator.handles(cls);
            }
        }).toList();

        if(evaluatorsForCls.isEmpty()) {
            return null;
        }

        final ApplicationUserRepository applicationUserRepository =
                servicesInjector.lookupService(ApplicationUserRepository.class);
        final QueryResultsCache queryResultsCache = servicesInjector.lookupService(QueryResultsCache.class);
        final UserService userService = servicesInjector.lookupService(UserService.class);

        return new TenantedAuthorizationFacetDefault(evaluatorsForCls, applicationUserRepository, queryResultsCache, userService, holder);
    }

    private static boolean isNullOrEmpty(final List<?> list) {
        return list == null || list.isEmpty();
    }

    @Deprecated
    static class ApplicationTenancyPathEvaluatorDefault implements ApplicationTenancyPathEvaluator {

        @Override
        public boolean handles(final Class<?> cls) {
            return HasAtPath.class.isAssignableFrom(cls);
        }

        public String applicationTenancyPathFor(final Object domainObject) {
            // always safe, facet factory only installs facet for classes implementing HasAtPath
            final HasAtPath tenantedObject = (HasAtPath) domainObject;

            return tenantedObject.getAtPath();
        }
    }



    @Override
    public void setServicesInjector(ServicesInjector servicesInjector) {
        this.servicesInjector = servicesInjector;
    }
}
