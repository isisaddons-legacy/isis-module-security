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
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.isisaddons.module.security.SecurityModule;
import org.isisaddons.module.security.dom.feature.ApplicationFeature;
import org.isisaddons.module.security.dom.feature.ApplicationFeatures;
import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.Identifier;
import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.RestrictTo;
import org.apache.isis.applib.annotation.SemanticsOf;

@DomainService(
        repositoryFor = ApplicationFeatureViewModel.class
)
@DomainServiceLayout(
        named="Security",
        menuBar = DomainServiceLayout.MenuBar.SECONDARY,
        menuOrder = "100.40"
)
public class ApplicationFeatureViewModels  {

    public static abstract class PropertyDomainEvent<T> extends SecurityModule.PropertyDomainEvent<ApplicationFeatureViewModels, T> {
        public PropertyDomainEvent(final ApplicationFeatureViewModels source, final Identifier identifier) {
            super(source, identifier);
        }

        public PropertyDomainEvent(final ApplicationFeatureViewModels source, final Identifier identifier, final T oldValue, final T newValue) {
            super(source, identifier, oldValue, newValue);
        }
    }

    public static abstract class CollectionDomainEvent<T> extends SecurityModule.CollectionDomainEvent<ApplicationFeatureViewModels, T> {
        public CollectionDomainEvent(final ApplicationFeatureViewModels source, final Identifier identifier, final Of of) {
            super(source, identifier, of);
        }

        public CollectionDomainEvent(final ApplicationFeatureViewModels source, final Identifier identifier, final Of of, final T value) {
            super(source, identifier, of, value);
        }
    }

    public static abstract class ActionDomainEvent extends SecurityModule.ActionDomainEvent<ApplicationFeatureViewModels> {
        public ActionDomainEvent(final ApplicationFeatureViewModels source, final Identifier identifier) {
            super(source, identifier);
        }

        public ActionDomainEvent(final ApplicationFeatureViewModels source, final Identifier identifier, final Object... arguments) {
            super(source, identifier, arguments);
        }

        public ActionDomainEvent(final ApplicationFeatureViewModels source, final Identifier identifier, final List<Object> arguments) {
            super(source, identifier, arguments);
        }
    }

    // //////////////////////////////////////

    //region > iconName

    public String iconName() {
        return "applicationFeature";
    }

    //endregion

    // //////////////////////////////////////

    //region > allPackages

    public static class AllPackagesDomainEvent extends ActionDomainEvent {
        public AllPackagesDomainEvent(final ApplicationFeatureViewModels source, final Identifier identifier, final Object... args) {
            super(source, identifier, args);
        }
    }

    @Action(
            domainEvent = AllPackagesDomainEvent.class,
            semantics = SemanticsOf.SAFE,
            restrictTo = RestrictTo.PROTOTYPING
    )
    @ActionLayout(
            cssClassFa = "fa-list"
    )
    @MemberOrder(sequence = "10")
    public List<ApplicationPackage> allPackages() {
        return asViewModels(applicationFeatures.allPackages(), ApplicationPackage.class);
    }
    //endregion

    //region > allClasses

    public static class AllClassesDomainEvent extends ActionDomainEvent {
        public AllClassesDomainEvent(final ApplicationFeatureViewModels source, final Identifier identifier, final Object... args) {
            super(source, identifier, args);
        }
    }

    @Action(
            domainEvent = AllClassesDomainEvent.class,
            semantics = SemanticsOf.SAFE,
            restrictTo = RestrictTo.PROTOTYPING
    )
    @ActionLayout(
            cssClassFa = "fa-list"
    )
    @MemberOrder(sequence = "20")
    public List<ApplicationClass> allClasses() {
        return asViewModels(applicationFeatures.allClasses(), ApplicationClass.class);
    }
    //endregion

    // //////////////////////////////////////

    //region > allActions

    public static class AllActionsDomainEvent extends ActionDomainEvent {
        public AllActionsDomainEvent(final ApplicationFeatureViewModels source, final Identifier identifier, final Object... args) {
            super(source, identifier, args);
        }
    }

    @Action(
            domainEvent = AllActionsDomainEvent.class,
            semantics = SemanticsOf.SAFE,
            restrictTo = RestrictTo.PROTOTYPING
    )
    @ActionLayout(
            cssClassFa = "fa-list"
    )
    @MemberOrder(sequence = "40")
    public List<ApplicationClassAction> allActions() {
        return asViewModels(applicationFeatures.allActions(), ApplicationClassAction.class);
    }
    //endregion

    //region > allProperties

    public static class AllPropertiesDomainEvent extends ActionDomainEvent {
        public AllPropertiesDomainEvent(final ApplicationFeatureViewModels source, final Identifier identifier, final Object... args) {
            super(source, identifier, args);
        }
    }

    @Action(
            domainEvent = AllPropertiesDomainEvent.class,
            semantics = SemanticsOf.SAFE,
            restrictTo = RestrictTo.PROTOTYPING
    )
    @ActionLayout(
            cssClassFa = "fa-list"
    )
    @MemberOrder(sequence = "50")
    public List<ApplicationClassProperty> allProperties() {
        return asViewModels(applicationFeatures.allProperties(), ApplicationClassProperty.class);
    }
    //endregion

    // //////////////////////////////////////

    //region > allCollections

    public static class AllCollectionsDomainEvent extends ActionDomainEvent {
        public AllCollectionsDomainEvent(final ApplicationFeatureViewModels source, final Identifier identifier, final Object... args) {
            super(source, identifier, args);
        }
    }

    @Action(
            domainEvent = AllCollectionsDomainEvent.class,
            semantics = SemanticsOf.SAFE,
            restrictTo = RestrictTo.PROTOTYPING
    )
    @ActionLayout(
            cssClassFa = "fa-list"
    )
    @MemberOrder(sequence = "60")
    public List<ApplicationClassCollection> allCollections() {
        return asViewModels(applicationFeatures.allCollections(), ApplicationClassCollection.class);
    }
    //endregion

    //region > helpers
    private <T extends ApplicationFeatureViewModel> List<T> asViewModels(final Iterable<ApplicationFeature> features, final Class<T> cls) {
        return Lists.newArrayList(
                Iterables.transform(
                        features,
                        ApplicationFeatureViewModel.Functions.<T>asViewModel(applicationFeatures, container)
                ));
    }
    //endregion

    //region > injected services
    @javax.inject.Inject
    ApplicationFeatures applicationFeatures;
    @javax.inject.Inject
    DomainObjectContainer container;
    //endregion

}
