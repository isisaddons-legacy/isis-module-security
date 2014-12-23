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
package org.isisaddons.module.security.app.feature;

import org.isisaddons.module.security.dom.feature.ApplicationFeatureId;
import org.isisaddons.module.security.dom.feature.ApplicationFeatures;
import org.isisaddons.module.security.dom.permission.ApplicationPermission;
import org.isisaddons.module.security.dom.permission.ApplicationPermissions;
import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.Identifier;
import org.apache.isis.applib.annotation.ActionInteraction;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NotContributed;
import org.apache.isis.applib.annotation.NotInServiceMenu;
import org.apache.isis.applib.annotation.PropertyInteraction;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.eventbus.ActionInteractionEvent;
import org.apache.isis.applib.services.eventbus.PropertyInteractionEvent;

@DomainService
public class ApplicationFeatureViewModelContributions {

    //region > feature
    public static class FeatureEvent extends ActionInteractionEvent<ApplicationFeatureViewModelContributions> {
        public FeatureEvent(ApplicationFeatureViewModelContributions source, Identifier identifier, Object... args) {
            super(source, identifier, args);
        }
    }
    public static class FeatureEventContributed extends PropertyInteractionEvent<ApplicationPermission, ApplicationFeatureViewModel> {
        public FeatureEventContributed(ApplicationPermission source, Identifier identifier, ApplicationFeatureViewModel oldValue, ApplicationFeatureViewModel newValue) {
            super(source, identifier, oldValue, newValue);
        }
    }

    @ActionInteraction(FeatureEvent.class)
    @PropertyInteraction(FeatureEventContributed.class)
    @ActionSemantics(ActionSemantics.Of.SAFE)
    @NotInServiceMenu
    @NotContributed(NotContributed.As.ACTION) // ie contributed as property
    @PropertyLayout(hidden=Where.REFERENCES_PARENT) // when contributed
    @MemberOrder(name="Feature", sequence = "4")
    public ApplicationFeatureViewModel feature(ApplicationPermission permission) {
        if(permission.getFeatureType() == null) {
            return null;
        }
        final ApplicationFeatureId featureId = getFeatureId(permission);
        return ApplicationFeatureViewModel.newViewModel(featureId, applicationFeatures, container);
    }

    private static ApplicationFeatureId getFeatureId(ApplicationPermission permission) {
        return ApplicationFeatureId.newFeature(permission.getFeatureType(), permission.getFeatureFqn());
    }

    //endregion


    //region  > services (injected)
    @javax.inject.Inject
    DomainObjectContainer container;

    @javax.inject.Inject
    ApplicationFeatures applicationFeatures;

    @javax.inject.Inject
    ApplicationPermissions applicationPermissions;

    //endregion

}
