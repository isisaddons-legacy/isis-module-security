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
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.ViewModelLayout;

@SuppressWarnings("UnusedDeclaration")
@ViewModelLayout(paged=100)
public class ApplicationClassAction extends ApplicationClassMember {

    public static abstract class PropertyDomainEvent<T> extends ApplicationClassMember.PropertyDomainEvent<ApplicationClassAction, T> {
        public PropertyDomainEvent(final ApplicationClassAction source, final Identifier identifier) {
            super(source, identifier);
        }

        public PropertyDomainEvent(final ApplicationClassAction source, final Identifier identifier, final T oldValue, final T newValue) {
            super(source, identifier, oldValue, newValue);
        }
    }

    public static abstract class CollectionDomainEvent<T> extends ApplicationClassMember.CollectionDomainEvent<ApplicationClassAction, T> {
        public CollectionDomainEvent(final ApplicationClassAction source, final Identifier identifier, final Of of) {
            super(source, identifier, of);
        }

        public CollectionDomainEvent(final ApplicationClassAction source, final Identifier identifier, final Of of, final T value) {
            super(source, identifier, of, value);
        }
    }

    public static abstract class ActionDomainEvent extends ApplicationClassMember.ActionDomainEvent<ApplicationClassAction> {
        public ActionDomainEvent(final ApplicationClassAction source, final Identifier identifier) {
            super(source, identifier);
        }

        public ActionDomainEvent(final ApplicationClassAction source, final Identifier identifier, final Object... arguments) {
            super(source, identifier, arguments);
        }

        public ActionDomainEvent(final ApplicationClassAction source, final Identifier identifier, final List<Object> arguments) {
            super(source, identifier, arguments);
        }
    }

    // //////////////////////////////////////

    //region > constructors

    public ApplicationClassAction() {
    }

    public ApplicationClassAction(final ApplicationFeatureId featureId) {
        super(featureId);
    }
    //endregion

    //region > returnTypeName (property)

    @Property()
    @MemberOrder(name="Data Type", sequence = "2.6")
    public String getReturnType() {
        return getFeature().getReturnTypeName();
    }
    //endregion

    //region > actionSemantics (property)
    @Property()
    @MemberOrder(name="Detail", sequence = "2.8")
    public ActionSemantics.Of getActionSemantics() {
        return getFeature().getActionSemantics();
    }
    //endregion


}
