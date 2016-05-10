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

import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.ViewModelLayout;
import org.apache.isis.core.metamodel.services.appfeat.ApplicationFeatureId;

@SuppressWarnings("UnusedDeclaration")
@ViewModelLayout(paged=100)
public class ApplicationClassProperty extends ApplicationClassMember {

    public static abstract class PropertyDomainEvent<T> extends ApplicationClassMember.PropertyDomainEvent<ApplicationClassProperty, T> {}

    public static abstract class CollectionDomainEvent<T> extends ApplicationClassMember.CollectionDomainEvent<ApplicationClassProperty, T> {}

    public static abstract class ActionDomainEvent extends ApplicationClassMember.ActionDomainEvent<ApplicationClassProperty> {}

    // //////////////////////////////////////

    //region > constructors
    public ApplicationClassProperty() {
        // Default constructor
    }

    public ApplicationClassProperty(final ApplicationFeatureId featureId) {
        super(featureId);
    }
    //endregion

    //region > returnType

    public static class ReturnTypeDomainEvent extends PropertyDomainEvent<String> {}

    @Property(
            domainEvent = ReturnTypeDomainEvent.class
    )
    @MemberOrder(name="Data Type", sequence = "2.6")
    public String getReturnType() {
        return getFeature().getReturnTypeName();
    }
    //endregion

    //region > derived

    public static class DerivedDomainEvent extends PropertyDomainEvent<Boolean> {}

    @Property(
            domainEvent = DerivedDomainEvent.class
    )
    @MemberOrder(name="Detail", sequence = "2.7")
    public boolean isDerived() {
        return getFeature().isDerived();
    }
    //endregion


    //region > maxLength
    public static class MaxLengthDomainEvent extends PropertyDomainEvent<Integer> {}

    @Property(
            domainEvent = MaxLengthDomainEvent.class,
            optionality = Optionality.OPTIONAL
    )
    @MemberOrder(name="Detail", sequence = "2.8")
    public Integer getMaxLength() {
        return getFeature().getPropertyMaxLength();
    }

    public boolean hideMaxLength() {
        return !String.class.getSimpleName().equals(getReturnType());
    }

    //endregion


    //region > typicalLength
    public static class TypicalLengthDomainEvent extends PropertyDomainEvent<Integer> {}

    @Property(
            domainEvent = TypicalLengthDomainEvent.class,
            optionality = Optionality.OPTIONAL
    )
    @MemberOrder(name="Detail", sequence = "2.9")
    public Integer getTypicalLength() {
        return getFeature().getPropertyTypicalLength();
    }

    public boolean hideTypicalLength() {
        return !String.class.getSimpleName().equals(getReturnType());
    }

    //endregion

}

