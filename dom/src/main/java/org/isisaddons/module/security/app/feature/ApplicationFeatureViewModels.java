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

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.RestrictTo;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.core.metamodel.services.appfeat.ApplicationFeature;
import org.apache.isis.core.metamodel.services.appfeat.ApplicationFeatureRepositoryDefault;

import org.isisaddons.module.security.SecurityModule;

@DomainService(
        nature = NatureOfService.VIEW_MENU_ONLY,
        objectType = "isissecurity.ApplicationFeatureViewModels",
        repositoryFor = ApplicationFeatureViewModel.class
)
@DomainServiceLayout(
        named="Security",
        menuBar = DomainServiceLayout.MenuBar.SECONDARY,
        menuOrder = "100.40"
)
public class ApplicationFeatureViewModels  {

    public static abstract class PropertyDomainEvent<T> extends SecurityModule.PropertyDomainEvent<ApplicationFeatureViewModels, T> {}

    public static abstract class CollectionDomainEvent<T> extends SecurityModule.CollectionDomainEvent<ApplicationFeatureViewModels, T> {}

    public static abstract class ActionDomainEvent extends SecurityModule.ActionDomainEvent<ApplicationFeatureViewModels> {}

    // //////////////////////////////////////

    //region > iconName

    public String iconName() {
        return "applicationFeature";
    }

    //endregion

    // //////////////////////////////////////

    //region > allPackages

    public static class AllPackagesDomainEvent extends ActionDomainEvent {}

    @Action(
            domainEvent = AllPackagesDomainEvent.class,
            semantics = SemanticsOf.SAFE,
            restrictTo = RestrictTo.PROTOTYPING
    )
    @ActionLayout(
            cssClassFa = "fa-list"
    )
    @MemberOrder(sequence = "100.40.1")
    public List<ApplicationPackage> allPackages() {
        return asViewModels(applicationFeatureRepository.allPackages(), ApplicationPackage.class);
    }
    //endregion

    //region > allClasses

    public static class AllClassesDomainEvent extends ActionDomainEvent {}

    @Action(
            domainEvent = AllClassesDomainEvent.class,
            semantics = SemanticsOf.SAFE,
            restrictTo = RestrictTo.PROTOTYPING
    )
    @ActionLayout(
            cssClassFa = "fa-list"
    )
    @MemberOrder(sequence = "100.40.2")
    public List<ApplicationClass> allClasses() {
        return asViewModels(applicationFeatureRepository.allClasses(), ApplicationClass.class);
    }
    //endregion

    // //////////////////////////////////////

    //region > allActions

    public static class AllActionsDomainEvent extends ActionDomainEvent {}

    @Action(
            domainEvent = AllActionsDomainEvent.class,
            semantics = SemanticsOf.SAFE,
            restrictTo = RestrictTo.PROTOTYPING
    )
    @ActionLayout(
            cssClassFa = "fa-list"
    )
    @MemberOrder(sequence = "100.40.3")
    public List<ApplicationClassAction> allActions() {
        return asViewModels(applicationFeatureRepository.allActions(), ApplicationClassAction.class);
    }
    //endregion

    //region > allProperties

    public static class AllPropertiesDomainEvent extends ActionDomainEvent {}

    @Action(
            domainEvent = AllPropertiesDomainEvent.class,
            semantics = SemanticsOf.SAFE,
            restrictTo = RestrictTo.PROTOTYPING
    )
    @ActionLayout(
            cssClassFa = "fa-list"
    )
    @MemberOrder(sequence = "100.40.4")
    public List<ApplicationClassProperty> allProperties() {
        return asViewModels(applicationFeatureRepository.allProperties(), ApplicationClassProperty.class);
    }
    //endregion

    // //////////////////////////////////////

    //region > allCollections

    public static class AllCollectionsDomainEvent extends ActionDomainEvent {}

    @Action(
            domainEvent = AllCollectionsDomainEvent.class,
            semantics = SemanticsOf.SAFE,
            restrictTo = RestrictTo.PROTOTYPING
    )
    @ActionLayout(
            cssClassFa = "fa-list"
    )
    @MemberOrder(sequence = "100.40.5")
    public List<ApplicationClassCollection> allCollections() {
        return asViewModels(applicationFeatureRepository.allCollections(), ApplicationClassCollection.class);
    }
    //endregion

    //region > helpers
    private <T extends ApplicationFeatureViewModel> List<T> asViewModels(final Iterable<ApplicationFeature> features, final Class<T> cls) {
        return Lists.newArrayList(
                Iterables.transform(
                        features,
                        ApplicationFeatureViewModel.Functions.<T>asViewModel(applicationFeatureRepository, container)
                ));
    }
    //endregion

    //region > injected services
    @javax.inject.Inject
    ApplicationFeatureRepositoryDefault applicationFeatureRepository;
    @javax.inject.Inject
    DomainObjectContainer container;
    //endregion

}
