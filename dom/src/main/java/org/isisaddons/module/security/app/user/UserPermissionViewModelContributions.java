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

import java.util.Collection;
import java.util.List;
import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.isisaddons.module.security.dom.feature.ApplicationFeature;
import org.isisaddons.module.security.dom.feature.ApplicationFeatureId;
import org.isisaddons.module.security.dom.feature.ApplicationFeatures;
import org.isisaddons.module.security.dom.user.ApplicationUser;
import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.*;

@DomainService
public class UserPermissionViewModelContributions  {

    //region > Permissions (derived collection)

    @MemberOrder(sequence = "30")
    @Render(Render.Type.EAGERLY)
    @Paged(50)
    @NotInServiceMenu
    @NotContributed(NotContributed.As.ACTION) // ie contributed as property
    @ActionSemantics(ActionSemantics.Of.SAFE)
    public List<UserPermissionViewModel> permissions(ApplicationUser user) {
        final Collection<ApplicationFeature> allMembers = applicationFeatures.allMembers();
        return asViewModels(user, allMembers);
    }

    //endregion

    //region > filterPermissions (action)

    @MemberOrder(sequence = "1", name="permissions")
    @NotInServiceMenu
    @ActionSemantics(ActionSemantics.Of.SAFE)
    public List<UserPermissionViewModel> filterPermissions(
            final ApplicationUser user,
            final @Named("Package") @TypicalLength(ApplicationFeature.TYPICAL_LENGTH_PKG_FQN) String packageFqn,
            final @Optional @Named("Class") @TypicalLength(ApplicationFeature.TYPICAL_LENGTH_CLS_NAME) String className) {
        final Collection<ApplicationFeature> allMembers = applicationFeatures.allMembers();
        final Iterable<ApplicationFeature> filtered = Iterables.filter(allMembers, within(packageFqn, className));
        return asViewModels(user, filtered);
    }

    Predicate<ApplicationFeature> within(final String packageFqn, final String className) {
        return new Predicate<ApplicationFeature>() {
            @Override
            public boolean apply(final ApplicationFeature input) {
                final ApplicationFeatureId inputFeatureId = input.getFeatureId();

                // recursive match on package
                final ApplicationFeatureId packageId = ApplicationFeatureId.newPackage(packageFqn);
                final List<ApplicationFeatureId> pathIds = inputFeatureId.getPathIds();
                if(!inputFeatureId.getPathIds().contains(packageId)) {
                    return false;
                }

                // match on class (if specified)
                return className == null || Objects.equal(inputFeatureId.getClassName(), className);
            }
        };
    }

    /**
     * Package names that have classes in them.
     */
    public List<String> choices1FilterPermissions() {
        return applicationFeatures.packageNames();
    }


    /**
     * Class names for selected package.
     */
    public List<String> choices2FilterPermissions(
            final ApplicationUser user,
            final String packageFqn) {
        return applicationFeatures.classNamesRecursivelyContainedIn(packageFqn);
    }


    //endregion

    //region > helpers
    List<UserPermissionViewModel> asViewModels(
            final ApplicationUser user,
            final Iterable<ApplicationFeature> features) {
        return Lists.newArrayList(
                Iterables.transform(
                        features,
                        UserPermissionViewModel.Functions.asViewModel(user, container))
        );
    }
    //endregion

    //region > injected
    @javax.inject.Inject
    DomainObjectContainer container;
    @javax.inject.Inject
    ApplicationFeatures applicationFeatures;
    //endregion

}
