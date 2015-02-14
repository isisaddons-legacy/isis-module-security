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
import org.isisaddons.module.security.dom.feature.ApplicationFeatureId;
import org.apache.isis.applib.Identifier;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.ViewModelLayout;

@ViewModelLayout(
        bookmarking = BookmarkPolicy.AS_CHILD
)
public abstract class ApplicationClassMember extends ApplicationFeatureViewModel {

    public static abstract class PropertyDomainEvent<S extends ApplicationClassMember, T> extends ApplicationFeatureViewModel.PropertyDomainEvent<ApplicationClassMember, T> {
        public PropertyDomainEvent(final S source, final Identifier identifier) {
            super(source, identifier);
        }

        public PropertyDomainEvent(final S source, final Identifier identifier, final T oldValue, final T newValue) {
            super(source, identifier, oldValue, newValue);
        }
    }

    public static abstract class CollectionDomainEvent<S extends ApplicationClassMember, T> extends ApplicationFeatureViewModel.CollectionDomainEvent<S, T> {
        public CollectionDomainEvent(final S source, final Identifier identifier, final Of of) {
            super(source, identifier, of);
        }

        public CollectionDomainEvent(final S source, final Identifier identifier, final Of of, final T value) {
            super(source, identifier, of, value);
        }
    }

    public static abstract class ActionDomainEvent<S extends ApplicationClassMember> extends ApplicationFeatureViewModel.ActionDomainEvent<S> {
        public ActionDomainEvent(final S source, final Identifier identifier) {
            super(source, identifier);
        }

        public ActionDomainEvent(final S source, final Identifier identifier, final Object... arguments) {
            super(source, identifier, arguments);
        }

        public ActionDomainEvent(final S source, final Identifier identifier, final List<Object> arguments) {
            super(source, identifier, arguments);
        }
    }

    // //////////////////////////////////////

    //region > constructors
    public ApplicationClassMember() {
    }

    public ApplicationClassMember(final ApplicationFeatureId featureId) {
        super(featureId);
    }
    //endregion

    //region > memberName (properties)

    public static class MemberNameDomainEvent extends PropertyDomainEvent<ApplicationClassMember, String> {
        public MemberNameDomainEvent(final ApplicationClassMember source, final Identifier identifier) {
            super(source, identifier);
        }

        public MemberNameDomainEvent(final ApplicationClassMember source, final Identifier identifier, final String oldValue, final String newValue) {
            super(source, identifier, oldValue, newValue);
        }
    }

    @Property(
            domainEvent = MemberNameDomainEvent.class
    )
    @MemberOrder(name="Id", sequence = "2.4")
    public String getMemberName() {
        return super.getMemberName();
    }
    //endregion



}


