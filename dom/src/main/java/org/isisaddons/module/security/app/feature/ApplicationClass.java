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
import java.util.SortedSet;
import org.isisaddons.module.security.dom.feature.ApplicationFeatureId;
import org.apache.isis.applib.Identifier;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.Collection;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.RenderType;
import org.apache.isis.applib.annotation.ViewModelLayout;

@SuppressWarnings("UnusedDeclaration")
@ViewModelLayout(
        paged=100,
        bookmarking = BookmarkPolicy.AS_ROOT
)
public class ApplicationClass extends ApplicationFeatureViewModel {

    public static abstract class PropertyDomainEvent<T> extends ApplicationFeatureViewModel.PropertyDomainEvent<ApplicationClass, T> {
        public PropertyDomainEvent(final ApplicationClass source, final Identifier identifier) {
            super(source, identifier);
        }

        public PropertyDomainEvent(final ApplicationClass source, final Identifier identifier, final T oldValue, final T newValue) {
            super(source, identifier, oldValue, newValue);
        }
    }

    public static abstract class CollectionDomainEvent<T> extends ApplicationFeatureViewModel.CollectionDomainEvent<ApplicationClass, T> {
        public CollectionDomainEvent(final ApplicationClass source, final Identifier identifier, final Of of) {
            super(source, identifier, of);
        }

        public CollectionDomainEvent(final ApplicationClass source, final Identifier identifier, final Of of, final T value) {
            super(source, identifier, of, value);
        }
    }

    public static abstract class ActionDomainEvent extends ApplicationFeatureViewModel.ActionDomainEvent<ApplicationClass> {
        public ActionDomainEvent(final ApplicationClass source, final Identifier identifier) {
            super(source, identifier);
        }

        public ActionDomainEvent(final ApplicationClass source, final Identifier identifier, final Object... arguments) {
            super(source, identifier, arguments);
        }

        public ActionDomainEvent(final ApplicationClass source, final Identifier identifier, final List<Object> arguments) {
            super(source, identifier, arguments);
        }
    }

    // //////////////////////////////////////

    //region > constructors

    public ApplicationClass() {
    }

    public ApplicationClass(final ApplicationFeatureId featureId) {
        super(featureId);
    }
    //endregion

    //region > actions (collection)
    @Collection()
    @CollectionLayout(
            render = RenderType.EAGERLY
    )
    @MemberOrder(sequence = "20.1")
    public List<ApplicationClassAction> getActions() {
        final SortedSet<ApplicationFeatureId> members = getFeature().getActions();
        return asViewModels(members);
    }
    //endregion

    //region > properties (collection)
    @Collection()
    @CollectionLayout(
            render = RenderType.EAGERLY
    )
    @MemberOrder(sequence = "20.2")
    public List<ApplicationClassProperty> getProperties() {
        final SortedSet<ApplicationFeatureId> members = getFeature().getProperties();
        return asViewModels(members);
    }
    //endregion

    //region > collections (collection)
    @Collection()
    @CollectionLayout(
            render = RenderType.EAGERLY
    )
    @MemberOrder(sequence = "20.3")
    public List<ApplicationClassCollection> getCollections() {
        final SortedSet<ApplicationFeatureId> members = getFeature().getCollections();
        return asViewModels(members);
    }
    //endregion

}
