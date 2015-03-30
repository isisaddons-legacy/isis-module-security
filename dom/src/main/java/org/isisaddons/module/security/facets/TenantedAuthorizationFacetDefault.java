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

import java.util.concurrent.Callable;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyPathEvaluator;
import org.isisaddons.module.security.dom.tenancy.WithApplicationTenancy;
import org.isisaddons.module.security.dom.user.ApplicationUser;
import org.isisaddons.module.security.dom.user.ApplicationUsers;
import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.events.UsabilityEvent;
import org.apache.isis.applib.events.VisibilityEvent;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;
import org.apache.isis.core.metamodel.facetapi.Facet;
import org.apache.isis.core.metamodel.facetapi.FacetAbstract;
import org.apache.isis.core.metamodel.facetapi.FacetHolder;
import org.apache.isis.core.metamodel.interactions.InteractionContext;
import org.apache.isis.core.metamodel.interactions.UsabilityContext;
import org.apache.isis.core.metamodel.interactions.VisibilityContext;

public class TenantedAuthorizationFacetDefault extends FacetAbstract implements TenantedAuthorizationFacet {

    public static Class<? extends Facet> type() {
        return TenantedAuthorizationFacet.class;
    }

    private final ApplicationUsers applicationUsers;
    private final QueryResultsCache queryResultsCache;
    private final ApplicationTenancyPathEvaluator evaluator;
    private final DomainObjectContainer container;

    public TenantedAuthorizationFacetDefault(
            final ApplicationUsers applicationUsers,
            final QueryResultsCache queryResultsCache,
            final ApplicationTenancyPathEvaluator evaluator,
            final DomainObjectContainer container,
            final FacetHolder holder) {
        super(type(), holder, Derivation.NOT_DERIVED);
        this.applicationUsers = applicationUsers;
        this.queryResultsCache = queryResultsCache;
        this.evaluator = evaluator;
        this.container = container;
    }

    static class Paths {
        String objectTenancyPath; // eg /x/y
        String userTenancyPath;   // eg /x  or /x/y/z
        String reason;
    }

    @Override
    public String hides(final VisibilityContext<? extends VisibilityEvent> ic) {
        final Paths paths = pathsFor(ic);

        if (paths == null) {
            return null;
        }
        if(paths.reason != null) {
            return paths.reason;
        }

        // if in same hierarchy
        if( paths.objectTenancyPath.startsWith(paths.userTenancyPath) ||
            paths.userTenancyPath.startsWith(paths.objectTenancyPath)) {
            return null;
        }

        // it's ok to return this info, because it isn't actually rendered (helpful if debugging)
        return String.format("User with tenancy '%s' is not permitted to view object with tenancy '%s'", paths.userTenancyPath, paths.objectTenancyPath);
    }

    @Override
    public String disables(final UsabilityContext<? extends UsabilityEvent> ic) {
        Paths paths = pathsFor(ic);

        if (paths == null) {
            return null;
        }
        if(paths.reason != null) {
            return paths.reason;
        }

        // if user's tenancy "above" object's tenancy in the hierarchy
        if(paths.objectTenancyPath.startsWith(paths.userTenancyPath)) {
            return null;
        }

        return String.format("User with tenancy '%s' is not permitted to edit object with tenancy '%s'", paths.userTenancyPath, paths.objectTenancyPath);
    }

    private Paths pathsFor(final InteractionContext<?> ic) {

        final Paths paths = new Paths();
        final String userName = container.getUser().getName();

        final ApplicationUser applicationUser = findApplicationUser(userName);
        if(applicationUser == null) {
            // not expected, but best to be safe...
            paths.reason = "Could not locate application user for " + userName;
            return paths;
        }

        final Object domainObject = ic.getTarget().getObject();

        if(evaluator != null) {

            paths.objectTenancyPath = evaluator.applicationTenancyPathFor(domainObject);
            paths.userTenancyPath = evaluator.applicationTenancyPathFor(applicationUser);

        } else {

            if (domainObject instanceof WithApplicationTenancy) {
                // should always be true, facet factory should only have installed for
                // classes implementing WithApplicationTenancy

                final WithApplicationTenancy tenantedObject = (WithApplicationTenancy) domainObject;
                final ApplicationTenancy objectTenancy = tenantedObject.getApplicationTenancy();
                if (objectTenancy == null) {
                    return null;
                }
                paths.objectTenancyPath = objectTenancy.getPath();

                final ApplicationTenancy userTenancy = applicationUser.getTenancy();
                if (userTenancy == null) {
                    paths.reason = "User has no tenancy";
                    return paths;
                }
                paths.userTenancyPath = userTenancy.getPath();
            }
        }

        if(paths.objectTenancyPath == null || paths.userTenancyPath == null) {
            return null;
        }

        return paths;
    }

    protected ApplicationUser findApplicationUser(final String userName) {
        return queryResultsCache.execute(new Callable<ApplicationUser>() {
            @Override
            public ApplicationUser call() throws Exception {
                return applicationUsers.findUserByUsername(userName);
            }
        }, TenantedAuthorizationFacetDefault.class, "findApplicationUser", userName);
    }



}
