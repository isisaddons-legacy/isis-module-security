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
import org.isisaddons.module.security.SecurityModule;
import org.isisaddons.module.security.dom.feature.ApplicationFeature;
import org.isisaddons.module.security.dom.feature.ApplicationFeatureId;
import org.isisaddons.module.security.dom.feature.ApplicationFeatures;
import org.isisaddons.module.security.dom.user.ApplicationUser;
import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.Identifier;
import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Collection;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.RenderType;
import org.apache.isis.applib.annotation.SemanticsOf;

@DomainService(
        nature = NatureOfService.VIEW_CONTRIBUTIONS_ONLY
)
public class UserPermissionViewModelContributions  {

    public static abstract class PropertyDomainEvent<T> extends SecurityModule.PropertyDomainEvent<UserPermissionViewModelContributions, T> {
        public PropertyDomainEvent(final UserPermissionViewModelContributions source, final Identifier identifier) {
            super(source, identifier);
        }

        public PropertyDomainEvent(final UserPermissionViewModelContributions source, final Identifier identifier, final T oldValue, final T newValue) {
            super(source, identifier, oldValue, newValue);
        }
    }

    public static abstract class CollectionDomainEvent<T> extends SecurityModule.CollectionDomainEvent<UserPermissionViewModelContributions, T> {
        public CollectionDomainEvent(final UserPermissionViewModelContributions source, final Identifier identifier, final Of of) {
            super(source, identifier, of);
        }

        public CollectionDomainEvent(final UserPermissionViewModelContributions source, final Identifier identifier, final Of of, final T value) {
            super(source, identifier, of, value);
        }
    }

    public static abstract class ActionDomainEvent extends SecurityModule.ActionDomainEvent<UserPermissionViewModelContributions> {
        public ActionDomainEvent(final UserPermissionViewModelContributions source, final Identifier identifier) {
            super(source, identifier);
        }

        public ActionDomainEvent(final UserPermissionViewModelContributions source, final Identifier identifier, final Object... arguments) {
            super(source, identifier, arguments);
        }

        public ActionDomainEvent(final UserPermissionViewModelContributions source, final Identifier identifier, final List<Object> arguments) {
            super(source, identifier, arguments);
        }
    }

    // //////////////////////////////////////

    //region > Permissions (derived collection)

    public static class PermissionsDomainEvent extends CollectionDomainEvent<UserPermissionViewModel> {
        public PermissionsDomainEvent(final UserPermissionViewModelContributions source, final Identifier identifier, final Of of) {
            super(source, identifier, of);
        }

        public PermissionsDomainEvent(final UserPermissionViewModelContributions source, final Identifier identifier, final Of of, final UserPermissionViewModel value) {
            super(source, identifier, of, value);
        }
    }

    @Action(
        semantics = SemanticsOf.SAFE
    )
    @ActionLayout(
            contributed = Contributed.AS_ASSOCIATION
    )
    @Collection(
            domainEvent = PermissionsDomainEvent.class
    )
    @CollectionLayout(
            paged=50,
            render = RenderType.EAGERLY
    ) // when contributed
    @MemberOrder(sequence = "30")
    public List<UserPermissionViewModel> permissions(final ApplicationUser user) {
        final java.util.Collection<ApplicationFeature> allMembers = applicationFeatures.allMembers();
        return asViewModels(user, allMembers);
    }

    //endregion

    // //////////////////////////////////////

    //region > filterPermissions (action)

    public static class FilterPermissionsEvent extends ActionDomainEvent {
        public FilterPermissionsEvent(final UserPermissionViewModelContributions source, final Identifier identifier) {
            super(source, identifier);
        }
        public FilterPermissionsEvent(final UserPermissionViewModelContributions source, final Identifier identifier, final Object... arguments) {
            super(source, identifier, arguments);
        }
        public FilterPermissionsEvent(final UserPermissionViewModelContributions source, final Identifier identifier, final List<Object> arguments) {
            super(source, identifier, arguments);
        }
    }

    @Action(
            domainEvent = FilterPermissionsEvent.class,
            semantics = SemanticsOf.SAFE
    )
    @MemberOrder(sequence = "1", name="permissions")
    public List<UserPermissionViewModel> filterPermissions(
            final ApplicationUser user,
            @ParameterLayout(named="Package", typicalLength=ApplicationFeature.TYPICAL_LENGTH_PKG_FQN)
            final String packageFqn,
            @Parameter(optionality = Optionality.OPTIONAL)
            @ParameterLayout(named="Class",  typicalLength=ApplicationFeature.TYPICAL_LENGTH_CLS_NAME)
            final String className) {
        final java.util.Collection<ApplicationFeature> allMembers = applicationFeatures.allMembers();
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
                if(!pathIds.contains(packageId)) {
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

    // //////////////////////////////////////

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
