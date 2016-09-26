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

import org.apache.isis.applib.events.UsabilityEvent;
import org.apache.isis.applib.events.VisibilityEvent;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;
import org.apache.isis.applib.services.user.UserService;
import org.apache.isis.core.metamodel.facetapi.Facet;
import org.apache.isis.core.metamodel.facetapi.FacetAbstract;
import org.apache.isis.core.metamodel.facetapi.FacetHolder;
import org.apache.isis.core.metamodel.interactions.UsabilityContext;
import org.apache.isis.core.metamodel.interactions.VisibilityContext;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyEvaluator;
import org.isisaddons.module.security.dom.user.ApplicationUser;
import org.isisaddons.module.security.dom.user.ApplicationUserRepository;

import java.util.concurrent.Callable;

public class TenantedAuthorizationFacetDefault extends FacetAbstract implements TenantedAuthorizationFacet {

    public static Class<? extends Facet> type() {
        return TenantedAuthorizationFacet.class;
    }

    private final ApplicationTenancyEvaluator evaluator;
    private final ApplicationUserRepository applicationUserRepository;
    private final QueryResultsCache queryResultsCache;
    private final UserService userService;

    public TenantedAuthorizationFacetDefault(
            final ApplicationTenancyEvaluator evaluator,
            final ApplicationUserRepository applicationUserRepository,
            final QueryResultsCache queryResultsCache,
            final UserService userService,
            final FacetHolder holder) {
        super(type(), holder, Derivation.NOT_DERIVED);
        this.evaluator = evaluator;
        this.applicationUserRepository = applicationUserRepository;
        this.queryResultsCache = queryResultsCache;
        this.userService = userService;
    }

    @Override
    public String hides(final VisibilityContext<? extends VisibilityEvent> ic) {

        final Object domainObject = ic.getTarget().getObject();
        final String userName = userService.getUser().getName();

        final ApplicationUser applicationUser = findApplicationUser(userName);
        if (applicationUser == null) {
            // not expected, but best to be safe...
            return "Could not locate application user for " + userName;
        }

        return evaluator.hides(domainObject, applicationUser);
    }


    @Override
    public String disables(final UsabilityContext<? extends UsabilityEvent> ic) {

        final Object domainObject = ic.getTarget().getObject();

        final String userName = userService.getUser().getName();

        final ApplicationUser applicationUser = findApplicationUser(userName);
        if (applicationUser == null) {
            // not expected, but best to be safe...
            return "Could not locate application user for " + userName;
        }

        return evaluator.disables(domainObject, applicationUser);
    }


    /**
     * Per {@link #findApplicationUserNoCache(String)}, cached for the request using the {@link QueryResultsCache}.
     */
    protected ApplicationUser findApplicationUser(final String userName) {
        return queryResultsCache.execute(new Callable<ApplicationUser>() {
            @Override
            public ApplicationUser call() throws Exception {
                return findApplicationUserNoCache(userName);
            }
        }, TenantedAuthorizationFacetDefault.class, "findApplicationUser", userName);
    }

    protected ApplicationUser findApplicationUserNoCache(final String userName) {
        return applicationUserRepository.findByUsername(userName);
    }

}
