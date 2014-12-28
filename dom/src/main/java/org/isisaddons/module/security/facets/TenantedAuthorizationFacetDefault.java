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
import org.isisaddons.module.security.dom.tenancy.WithApplicationTenancy;
import org.isisaddons.module.security.dom.user.ApplicationUser;
import org.isisaddons.module.security.dom.user.ApplicationUsers;
import org.apache.isis.applib.events.UsabilityEvent;
import org.apache.isis.applib.events.VisibilityEvent;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;
import org.apache.isis.core.metamodel.facetapi.Facet;
import org.apache.isis.core.metamodel.facetapi.FacetAbstract;
import org.apache.isis.core.metamodel.facetapi.FacetHolder;
import org.apache.isis.core.metamodel.interactions.UsabilityContext;
import org.apache.isis.core.metamodel.interactions.VisibilityContext;

public class TenantedAuthorizationFacetDefault extends FacetAbstract implements TenantedAuthorizationFacet {

    public static Class<? extends Facet> type() {
        return TenantedAuthorizationFacet.class;
    }

    private final ApplicationUsers applicationUsers;
    private final QueryResultsCache queryResultsCache;

    public TenantedAuthorizationFacetDefault(
            final ApplicationUsers applicationUsers,
            final QueryResultsCache queryResultsCache,
            final FacetHolder holder) {
        super(type(), holder, Derivation.NOT_DERIVED);
        this.applicationUsers = applicationUsers;
        this.queryResultsCache = queryResultsCache;
    }

    @Override
    public String hides(final VisibilityContext<? extends VisibilityEvent> ic) {
        final Object object = ic.getTarget().getObject();
        if (!(object instanceof WithApplicationTenancy)) {
            // not expected, facet factory should only have installed for those classes that implement the WithApplicationTenancy interface
            return null;
        }

        final WithApplicationTenancy tenantedObject = (WithApplicationTenancy) object;
        final ApplicationTenancy objectTenancy = tenantedObject.getApplicationTenancy();
        if(objectTenancy == null) {
            return null;
        }

        final String userName = ic.getSession().getUserName();

        final ApplicationUser applicationUser = getApplicationUser(userName);
        if(applicationUser == null) {
            // not expected, but best to be safe...
            return "Could not location application user for " + userName;
        }

        final ApplicationTenancy userTenancy = applicationUser.getTenancy();
        if(userTenancy == null) {
            return "User has no tenancy";
        }

        final String objectTenancyPath = objectTenancy.getPath(); // eg /x/y
        final String userTenancyPath = userTenancy.getPath();     // eg /x  or /x/y/z

        // if in same hierarchy
        if(objectTenancyPath.startsWith(userTenancyPath) || userTenancyPath.startsWith(objectTenancyPath)) {
            return null;
        }

        // it's ok to return this info, because it isn't actually rendered
        return String.format("User with tenancy '%s' is not permitted to view object with tenancy '%s'", userTenancyPath, objectTenancyPath);
    }

    @Override
    public String disables(final UsabilityContext<? extends UsabilityEvent> ic) {
        final Object object = ic.getTarget().getObject();
        if (!(object instanceof WithApplicationTenancy)) {
            // not expected, facet factory should only have installed for those classes that implement the WithApplicationTenancy interface
            return null;
        }

        final WithApplicationTenancy tenantedObject = (WithApplicationTenancy) object;
        final ApplicationTenancy objectTenancy = tenantedObject.getApplicationTenancy();
        if(objectTenancy == null) {
            return null;
        }

        final String userName = ic.getSession().getUserName();
        final ApplicationUser applicationUser = getApplicationUser(userName);
        if(applicationUser == null) {
            // not expected, but best to be safe...
            return "Could not location application user for " + userName;
        }

        final ApplicationTenancy userTenancy = applicationUser.getTenancy();
        if(userTenancy == null) {
            return "User has no tenancy";
        }

        final String objectTenancyPath = objectTenancy.getPath(); // eg /x/y
        final String userTenancyPath = userTenancy.getPath();     // eg /x  or /x/y/z

        // if user's tenancy "above" object's tenancy in the hierarchy
        if(objectTenancyPath.startsWith(userTenancyPath)) {
            return null;
        }

        return String.format("User with tenancy '%s' is not permitted to edit object with tenancy '%s'", userTenancyPath, objectTenancyPath);
    }

    protected ApplicationUser getApplicationUser(final String userName) {
        return queryResultsCache.execute(new Callable<ApplicationUser>() {
            @Override
            public ApplicationUser call() throws Exception {
                return applicationUsers.findUserByUsername(userName);
            }
        }, TenantedAuthorizationFacetDefault.class, "getApplicationUser", userName);
    }


}
