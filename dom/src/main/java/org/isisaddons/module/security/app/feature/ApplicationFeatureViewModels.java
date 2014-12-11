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
import org.isisaddons.module.security.dom.feature.ApplicationFeature;
import org.isisaddons.module.security.dom.feature.ApplicationFeatures;
import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.Identifier;
import org.apache.isis.applib.annotation.ActionInteraction;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.services.eventbus.ActionInteractionEvent;

@DomainService()
@DomainServiceLayout(
        named="Security",
        menuBar = DomainServiceLayout.MenuBar.SECONDARY,
        menuOrder = "100.40"
)
public class ApplicationFeatureViewModels  {

    //region > iconName

    public String iconName() {
        return "applicationFeature";
    }

    //endregion

    //region > allPackages

    public static class AllPackagesEvent extends ActionInteractionEvent<ApplicationFeatureViewModels> {
        public AllPackagesEvent(ApplicationFeatureViewModels source, Identifier identifier, Object... args) {
            super(source, identifier, args);
        }
    }

    @ActionInteraction(AllPackagesEvent.class)
    @ActionLayout(prototype = true)
    @MemberOrder(sequence = "10")
    @ActionSemantics(ActionSemantics.Of.SAFE)
    public List<ApplicationPackage> allPackages() {
        return asViewModels(applicationFeatures.allPackages(), ApplicationPackage.class);
    }
    //endregion

    //region > allClasses

    public static class AllClassesEvent extends ActionInteractionEvent<ApplicationFeatureViewModels> {
        public AllClassesEvent(ApplicationFeatureViewModels source, Identifier identifier, Object... args) {
            super(source, identifier, args);
        }
    }

    @ActionInteraction(AllClassesEvent.class)
    @MemberOrder(sequence = "20")
    @ActionSemantics(ActionSemantics.Of.SAFE)
    @ActionLayout(prototype = true)
    public List<ApplicationClass> allClasses() {
        return asViewModels(applicationFeatures.allClasses(), ApplicationClass.class);
    }
    //endregion

    //region > allActions

    public static class AllActionsEvent extends ActionInteractionEvent<ApplicationFeatureViewModels> {
        public AllActionsEvent(ApplicationFeatureViewModels source, Identifier identifier, Object... args) {
            super(source, identifier, args);
        }
    }

    @ActionInteraction(AllActionsEvent.class)
    @MemberOrder(sequence = "40")
    @ActionSemantics(ActionSemantics.Of.SAFE)
    @ActionLayout(prototype = true)
    public List<ApplicationClassAction> allActions() {
        return asViewModels(applicationFeatures.allActions(), ApplicationClassAction.class);
    }
    //endregion

    //region > allProperties

    public static class AllPropertiesEvent extends ActionInteractionEvent<ApplicationFeatureViewModels> {
        public AllPropertiesEvent(ApplicationFeatureViewModels source, Identifier identifier, Object... args) {
            super(source, identifier, args);
        }
    }

    @ActionInteraction(AllPropertiesEvent.class)
    @ActionSemantics(ActionSemantics.Of.SAFE)
    @ActionLayout(prototype = true)
    @MemberOrder(sequence = "50")
    public List<ApplicationClassProperty> allProperties() {
        return asViewModels(applicationFeatures.allProperties(), ApplicationClassProperty.class);
    }
    //endregion

    //region > allCollections

    public static class AllCollectionsEvent extends ActionInteractionEvent<ApplicationFeatureViewModels> {
        public AllCollectionsEvent(ApplicationFeatureViewModels source, Identifier identifier, Object... args) {
            super(source, identifier, args);
        }
    }

    @ActionInteraction(AllCollectionsEvent.class)
    @ActionSemantics(ActionSemantics.Of.SAFE)
    @ActionLayout(prototype = true)
    @MemberOrder(sequence = "60")
    public List<ApplicationClassCollection> allCollections() {
        return asViewModels(applicationFeatures.allCollections(), ApplicationClassCollection.class);
    }
    //endregion

    //region > helpers


    private <T extends ApplicationFeatureViewModel> List<T> asViewModels(Iterable<ApplicationFeature> features, Class<T> cls) {
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
