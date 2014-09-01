/*
 *  Copyright 2014 Dan Haywood
 *
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
import org.apache.isis.applib.annotation.*;

@Named("Features")
@DomainService(menuOrder = "90.4")
public class ApplicationFeatureViewModels  {

    public String iconName() {
        return "applicationFeature";
    }

    @Paged(100)
    @MemberOrder(sequence = "10")
    @ActionSemantics(ActionSemantics.Of.SAFE)
    @Prototype
    public List<ApplicationPackage> allPackages() {
        return asViewModels(applicationFeatures.allPackages(), ApplicationPackage.class);
    }

    @Paged(100)
    @MemberOrder(sequence = "20")
    @ActionSemantics(ActionSemantics.Of.SAFE)
    @Prototype
    public List<ApplicationClass> allClasses() {
        return asViewModels(applicationFeatures.allClasses(), ApplicationClass.class);
    }

    @Paged(100)
    @MemberOrder(sequence = "40")
    @ActionSemantics(ActionSemantics.Of.SAFE)
    @Prototype
    public List<ApplicationClassAction> allActions() {
        return asViewModels(applicationFeatures.allActions(), ApplicationClassAction.class);
    }

    @MemberOrder(sequence = "50")
    @ActionSemantics(ActionSemantics.Of.SAFE)
    @Prototype
    public List<ApplicationClassProperty> allProperties() {
        return asViewModels(applicationFeatures.allProperties(), ApplicationClassProperty.class);
    }

    @Paged(100)
    @MemberOrder(sequence = "60")
    @ActionSemantics(ActionSemantics.Of.SAFE)
    @Prototype
    public List<ApplicationClassCollection> allCollections() {
        return asViewModels(applicationFeatures.allCollections(), ApplicationClassCollection.class);
    }


    private <T extends ApplicationFeatureViewModel> List<T> asViewModels(Iterable<ApplicationFeature> features, Class<T> cls) {
        return Lists.newArrayList(
                Iterables.transform(
                        features,
                        ApplicationFeatureViewModel.Functions.<T>asViewModel(applicationFeatures, container)
                ));
    }

    //region > injected services
    @javax.inject.Inject
    ApplicationFeatures applicationFeatures;
    @javax.inject.Inject
    DomainObjectContainer container;
    //endregion

}
