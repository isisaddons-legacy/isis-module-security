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
import org.apache.isis.core.metamodel.facetapi.Facet;
import org.apache.isis.core.metamodel.facetapi.FacetAbstract;
import org.apache.isis.core.metamodel.facetapi.FacetHolder;
import org.apache.isis.core.metamodel.interactions.UsabilityContext;
import org.apache.isis.core.metamodel.interactions.VisibilityContext;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyEvaluator;

public class TenantedAuthorizationFacetDefault extends FacetAbstract implements TenantedAuthorizationFacet {

    public static Class<? extends Facet> type() {
        return TenantedAuthorizationFacet.class;
    }

    private final ApplicationTenancyEvaluator evaluator;

    public TenantedAuthorizationFacetDefault(
            final ApplicationTenancyEvaluator evaluator,
            final FacetHolder holder) {
        super(type(), holder, Derivation.NOT_DERIVED);
        this.evaluator = evaluator;
    }

    @Override
    public String hides(final VisibilityContext<? extends VisibilityEvent> ic) {
        return evaluator.hides(ic);
    }

    @Override
    public String disables(final UsabilityContext<? extends UsabilityEvent> ic) {
        return evaluator.disables(ic);
    }



}
