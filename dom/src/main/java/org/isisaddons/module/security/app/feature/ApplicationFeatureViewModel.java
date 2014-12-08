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
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.isisaddons.module.security.dom.feature.ApplicationFeature;
import org.isisaddons.module.security.dom.feature.ApplicationFeatureId;
import org.isisaddons.module.security.dom.feature.ApplicationFeatureType;
import org.isisaddons.module.security.dom.feature.ApplicationFeatures;
import org.isisaddons.module.security.dom.permission.ApplicationPermission;
import org.isisaddons.module.security.dom.permission.ApplicationPermissions;
import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.ViewModel;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.MemberGroupLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.util.ObjectContracts;

/**
 * View model identified by {@link org.isisaddons.module.security.dom.feature.ApplicationFeatureId} and backed by an {@link org.isisaddons.module.security.dom.feature.ApplicationFeature}.
 */
@MemberGroupLayout(
        columnSpans = {6,0,6,12},
        left = {"Id", "Data Type"},
        right= {"Parent", "Contributed", "Detail"}
)
public abstract class ApplicationFeatureViewModel implements ViewModel {

    //region > constructors
    public static ApplicationFeatureViewModel newViewModel(
            final ApplicationFeatureId featureId,
            final ApplicationFeatures applicationFeatures,
            final DomainObjectContainer container) {
        Class<? extends ApplicationFeatureViewModel> cls = viewModelClassFor(featureId, applicationFeatures);
        if(cls == null) {
            // TODO: not sure why, yet...
            return null;
        }
        return container.newViewModelInstance(cls, featureId.asEncodedString());
    }

    private static Class<? extends ApplicationFeatureViewModel> viewModelClassFor(
            final ApplicationFeatureId featureId,
            final ApplicationFeatures applicationFeatures) {
        switch (featureId.getType()) {
            case PACKAGE:
                return ApplicationPackage.class;
            case CLASS:
                return ApplicationClass.class;
            case MEMBER:
            final ApplicationFeature feature = applicationFeatures.findFeature(featureId);
                if(feature == null) {
                    // TODO: not sure why, yet...
                    return null;
                }
                switch(feature.getMemberType()) {
                    case PROPERTY:
                        return ApplicationClassProperty.class;
                    case COLLECTION:
                        return ApplicationClassCollection.class;
                    case ACTION:
                        return ApplicationClassAction.class;
                }
        }
        throw new IllegalArgumentException("could not determine feature type; featureId = " + featureId);
    }

    public ApplicationFeatureViewModel() {
    }

    ApplicationFeatureViewModel(ApplicationFeatureId featureId) {
        setFeatureId(featureId);
    }
    //endregion

    //region > identification
    /**
     * having a title() method (rather than using @Title annotation) is necessary as a workaround to be able to use
     * wrapperFactory#unwrap(...) method, which is otherwise broken in Isis 1.6.0
     */
    public String title() {
        return getFullyQualifiedName();
    }
    public String iconName() {
        return "applicationFeature";
    }
    //endregion

    //region > ViewModel impl
    @Override
    public String viewModelMemento() {
        return getFeatureId().asEncodedString();
    }

    @Override
    public void viewModelInit(String encodedMemento) {
        final ApplicationFeatureId applicationFeatureId = ApplicationFeatureId.parseEncoded(encodedMemento);
        setFeatureId(applicationFeatureId);
    }

    //endregion

    //region > featureId (property, programmatic)
    private ApplicationFeatureId featureId;

    @Programmatic
    public ApplicationFeatureId getFeatureId() {
        return featureId;
    }

    public void setFeatureId(ApplicationFeatureId applicationFeatureId) {
        this.featureId = applicationFeatureId;
    }
    //endregion

    //region > feature (property, programmatic)
    @Programmatic
    ApplicationFeature getFeature() {
        return applicationFeatures.findFeature(getFeatureId());
    }
    //endregion
    
    //region > fullyQualifiedName (property, programmatic)
    @Programmatic // in the title
    public String getFullyQualifiedName() {
        return getFeatureId().getFullyQualifiedName();
    }
    //endregion

    //region > type (programmatic)
    @Programmatic
    public ApplicationFeatureType getType() {
        return getFeatureId().getType();
    }
    //endregion

    //region > type, packageName, className, memberName (properties)
    @PropertyLayout(typicalLength=ApplicationFeature.TYPICAL_LENGTH_PKG_FQN)
    @MemberOrder(name="Id", sequence = "2.2")
    public String getPackageName() {
        return getFeatureId().getPackageName();
    }

    /**
     * For packages, will be null. Is in this class (rather than subclasses) so is shown in
     * {@link ApplicationPackage#getContents() package contents}.
     */
    @PropertyLayout(typicalLength=ApplicationFeature.TYPICAL_LENGTH_CLS_NAME)
    @MemberOrder(name="Id", sequence = "2.3")
    public String getClassName() {
        return getFeatureId().getClassName();
    }
    public boolean hideClassName() {
        return getType().hideClassName();
    }

    /**
     * For packages and class names, will be null.
     */
    @PropertyLayout(typicalLength=ApplicationFeature.TYPICAL_LENGTH_MEMBER_NAME)
    @MemberOrder(name="Id", sequence = "2.4")
    public String getMemberName() {
        return getFeatureId().getMemberName();
    }

    public boolean hideMemberName() {
        return getType().hideMember();
    }


    //endregion

    //region > parent (property)

    @PropertyLayout(hidden=Where.ALL_TABLES)
    @MemberOrder(name = "Parent", sequence = "2.6")
    public ApplicationFeatureViewModel getParent() {
        final ApplicationFeatureId parentId;
        parentId = getType() == ApplicationFeatureType.MEMBER
                ? getFeatureId().getParentClassId()
                : getFeatureId().getParentPackageId();
        if(parentId == null) {
            return null;
        }
        final ApplicationFeature feature = applicationFeatures.findFeature(parentId);
        if (feature == null) {
            return null;
        }
        final Class<?> cls = viewModelClassFor(parentId, applicationFeatures);
        return (ApplicationFeatureViewModel) container.newViewModelInstance(cls, parentId.asEncodedString());

    }
    //endregion

    //region > contributed (property)

    /**
     * For packages and class names, will be null.
     */
    @MemberOrder(name="Contributed", sequence = "2.5.5")
    public boolean isContributed() {
        return getFeature().isContributed();
    }

    public boolean hideContributed() {
        return getType().hideMember();
    }
    //endregion

    //region > permissions (collection)
    @MemberOrder(sequence = "10")
    @CollectionLayout(render= CollectionLayout.RenderType.EAGERLY)
    public List<ApplicationPermission> getPermissions() {
        return applicationPermissions.findByFeature(getFeatureId());
    }
    //endregion

    //region > parentPackage (property, programmatic, for packages & classes only)

    /**
     * The parent package feature of this class or package.
     */
    @Programmatic
    public ApplicationFeatureViewModel getParentPackage() {
        return Functions.asViewModelForId(applicationFeatures, container).apply(getFeatureId().getParentPackageId());
    }
    //endregion

    //region > equals, hashCode, toString

    private final static String propertyNames = "featureId";

    @Override
    public boolean equals(Object obj) {
        return ObjectContracts.equals(this, obj, propertyNames);
    }

    @Override
    public int hashCode() {
        return ObjectContracts.hashCode(this, propertyNames);
    }

    @Override
    public String toString() {
        return ObjectContracts.toString(this, propertyNames);
    }
    //endregion

    //region > helpers
    <T extends ApplicationFeatureViewModel> List<T> asViewModels(SortedSet<ApplicationFeatureId> members) {
        final Function<ApplicationFeatureId, T> function = Functions.<T>asViewModelForId(applicationFeatures, container);
        return Lists.newArrayList(
                Iterables.transform(members, function));
    }
    //endregion

    //region > Functions

    public static class Functions {
        private Functions(){}
        public static <T extends ApplicationFeatureViewModel> Function<ApplicationFeatureId, T> asViewModelForId(
                final ApplicationFeatures applicationFeatures, final DomainObjectContainer container) {
            return new Function<ApplicationFeatureId, T>(){
                @Override
                public T apply(ApplicationFeatureId input) {
                    return (T)ApplicationFeatureViewModel.newViewModel(input, applicationFeatures, container);
                }
            };
        }
        public static <T extends ApplicationFeatureViewModel> Function<ApplicationFeature, T> asViewModel(
                final ApplicationFeatures applicationFeatures, final DomainObjectContainer container) {
            return new Function<ApplicationFeature, T>(){
                @Override
                public T apply(ApplicationFeature input) {
                    return (T) ApplicationFeatureViewModel.newViewModel(input.getFeatureId(), applicationFeatures, container);
                }
            };
        }
    }
    //endregion

    //region > injected services
    @javax.inject.Inject
    DomainObjectContainer container;

    @javax.inject.Inject
    ApplicationFeatures applicationFeatures;

    @javax.inject.Inject
    ApplicationPermissions applicationPermissions;
    //endregion

}
