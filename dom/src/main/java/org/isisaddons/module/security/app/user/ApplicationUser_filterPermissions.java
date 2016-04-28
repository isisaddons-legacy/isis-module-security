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
package org.isisaddons.module.security.app.user;

import java.util.List;

import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.core.metamodel.services.appfeat.ApplicationFeature;
import org.apache.isis.core.metamodel.services.appfeat.ApplicationFeatureId;
import org.apache.isis.core.metamodel.services.appfeat.ApplicationFeatureRepositoryDefault;

import org.isisaddons.module.security.SecurityModule;
import org.isisaddons.module.security.dom.user.ApplicationUser;

@Mixin
public class ApplicationUser_filterPermissions {


    public static class ActionDomainEvent extends SecurityModule.ActionDomainEvent<ApplicationUser_filterPermissions> {}

    //region > constructor
    private final ApplicationUser user;
    public ApplicationUser_filterPermissions(final ApplicationUser user) {
        this.user = user;
    }
    //endregion



    //region > filterPermissions (action)


    @Action(
            domainEvent = ActionDomainEvent.class,
            semantics = SemanticsOf.SAFE
    )
    @MemberOrder(sequence = "1", name="permissions")
    public List<UserPermissionViewModel> $$(
            @ParameterLayout(named="Package", typicalLength=ApplicationFeature.TYPICAL_LENGTH_PKG_FQN)
            final String packageFqn,
            @Parameter(optionality = Optionality.OPTIONAL)
            @ParameterLayout(named="Class",  typicalLength=ApplicationFeature.TYPICAL_LENGTH_CLS_NAME)
            final String className) {
        final java.util.Collection<ApplicationFeature> allMembers = applicationFeatureRepository.allMembers();
        final Iterable<ApplicationFeature> filtered = Iterables.filter(allMembers, within(packageFqn, className));
        return asViewModels(filtered);
    }

    /**
     * Package names that have classes in them.
     */
    public List<String> choices0$$() {
        return applicationFeatureRepository.packageNames();
    }


    /**
     * Class names for selected package.
     */
    public List<String> choices1$$(final String packageFqn) {
        return applicationFeatureRepository.classNamesRecursivelyContainedIn(packageFqn);
    }


    static Predicate<ApplicationFeature> within(final String packageFqn, final String className) {
        return new Predicate<ApplicationFeature>() {
            @Override
            public boolean apply(final ApplicationFeature input) {
                final ApplicationFeatureId inputFeatureId = input.getFeatureId();

                // recursive match on package
                final ApplicationFeatureId packageId = ApplicationFeatureId.newPackage(packageFqn);
                final List<ApplicationFeatureId> pathIds = inputFeatureId.getPathIds();
                if(!pathIds.contains(packageId)) {
                    return false;
                }

                // match on class (if specified)
                return className == null || Objects.equal(inputFeatureId.getClassName(), className);
            }
        };
    }

    List<UserPermissionViewModel> asViewModels(final Iterable<ApplicationFeature> features) {
        return Lists.newArrayList(
                Iterables.transform(
                        features,
                        UserPermissionViewModel.Functions.asViewModel(user, container))
        );
    }

    @javax.inject.Inject
    DomainObjectContainer container;
    @javax.inject.Inject
    ApplicationFeatureRepositoryDefault applicationFeatureRepository;

}
