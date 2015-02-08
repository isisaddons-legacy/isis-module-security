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

import java.util.List;
import org.isisaddons.module.security.SecurityModule;
import org.isisaddons.module.security.dom.feature.ApplicationFeatureId;
import org.isisaddons.module.security.dom.feature.ApplicationFeatures;
import org.isisaddons.module.security.dom.permission.ApplicationPermission;
import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.Identifier;
import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;

@DomainService(
        nature = NatureOfService.VIEW_CONTRIBUTIONS_ONLY
)
public class ApplicationFeatureViewModelContributions {

    public static abstract class PropertyDomainEvent<T> extends SecurityModule.PropertyDomainEvent<ApplicationFeatureViewModelContributions, T> {
        public PropertyDomainEvent(final ApplicationFeatureViewModelContributions source, final Identifier identifier) {
            super(source, identifier);
        }

        public PropertyDomainEvent(final ApplicationFeatureViewModelContributions source, final Identifier identifier, final T oldValue, final T newValue) {
            super(source, identifier, oldValue, newValue);
        }
    }

    public static abstract class CollectionDomainEvent<T> extends SecurityModule.CollectionDomainEvent<ApplicationFeatureViewModelContributions, T> {
        public CollectionDomainEvent(final ApplicationFeatureViewModelContributions source, final Identifier identifier, final Of of) {
            super(source, identifier, of);
        }

        public CollectionDomainEvent(final ApplicationFeatureViewModelContributions source, final Identifier identifier, final Of of, final T value) {
            super(source, identifier, of, value);
        }
    }

    public static abstract class ActionDomainEvent extends SecurityModule.ActionDomainEvent<ApplicationFeatureViewModelContributions> {
        public ActionDomainEvent(final ApplicationFeatureViewModelContributions source, final Identifier identifier) {
            super(source, identifier);
        }

        public ActionDomainEvent(final ApplicationFeatureViewModelContributions source, final Identifier identifier, final Object... arguments) {
            super(source, identifier, arguments);
        }

        public ActionDomainEvent(final ApplicationFeatureViewModelContributions source, final Identifier identifier, final List<Object> arguments) {
            super(source, identifier, arguments);
        }
    }

    // //////////////////////////////////////

    //region > feature
    public static class FeatureDomainEvent extends PropertyDomainEvent<ApplicationFeatureViewModel> {
        public FeatureDomainEvent(final ApplicationFeatureViewModelContributions source, final Identifier identifier, final ApplicationFeatureViewModel oldValue, final ApplicationFeatureViewModel newValue) {
            super(source, identifier, oldValue, newValue);
        }
    }

    @Action(
            semantics = SemanticsOf.SAFE
    )
    @ActionLayout(
            contributed = Contributed.AS_ASSOCIATION
    )
    @Property(
            domainEvent = FeatureDomainEvent.class
    )
    @PropertyLayout(
            hidden=Where.REFERENCES_PARENT
    )
    @MemberOrder(name="Feature", sequence = "4")
    public ApplicationFeatureViewModel feature(final ApplicationPermission permission) {
        if(permission.getFeatureType() == null) {
            return null;
        }
        final ApplicationFeatureId featureId = getFeatureId(permission);
        return ApplicationFeatureViewModel.newViewModel(featureId, applicationFeatures, container);
    }

    private static ApplicationFeatureId getFeatureId(final ApplicationPermission permission) {
        return ApplicationFeatureId.newFeature(permission.getFeatureType(), permission.getFeatureFqn());
    }

    //endregion

    //region  > services (injected)
    @javax.inject.Inject
    DomainObjectContainer container;

    @javax.inject.Inject
    ApplicationFeatures applicationFeatures;

    //endregion

}
