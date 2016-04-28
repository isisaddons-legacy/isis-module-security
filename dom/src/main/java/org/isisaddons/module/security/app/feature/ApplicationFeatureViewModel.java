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

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.ViewModel;
import org.apache.isis.applib.annotation.Collection;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.MemberGroupLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.RenderType;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.util.ObjectContracts;
import org.apache.isis.core.metamodel.services.appfeat.ApplicationFeature;
import org.apache.isis.core.metamodel.services.appfeat.ApplicationFeatureId;
import org.apache.isis.core.metamodel.services.appfeat.ApplicationFeatureRepositoryDefault;
import org.apache.isis.core.metamodel.services.appfeat.ApplicationFeatureType;

import org.isisaddons.module.security.SecurityModule;
import org.isisaddons.module.security.dom.permission.ApplicationPermission;
import org.isisaddons.module.security.dom.permission.ApplicationPermissionRepository;

/**
 * View model identified by {@link ApplicationFeatureId} and backed by an {@link ApplicationFeature}.
 */
@MemberGroupLayout(
        columnSpans = {6,0,6,12},
        left = {"Id", "Data Type", "Metadata"},
        right= {"Parent", "Contributed", "Detail"}
)
public abstract class ApplicationFeatureViewModel implements ViewModel {

    public static abstract class PropertyDomainEvent<S extends ApplicationFeatureViewModel,T> extends SecurityModule.PropertyDomainEvent<S, T> {}

    public static abstract class CollectionDomainEvent<S extends ApplicationFeatureViewModel,T> extends SecurityModule.CollectionDomainEvent<S, T> {}

    public static abstract class ActionDomainEvent<S extends ApplicationFeatureViewModel> extends SecurityModule.ActionDomainEvent<S> {}

    // //////////////////////////////////////

    //region > constructors
    public static ApplicationFeatureViewModel newViewModel(
            final ApplicationFeatureId featureId,
            final ApplicationFeatureRepositoryDefault applicationFeatureRepository,
            final DomainObjectContainer container) {
        final Class<? extends ApplicationFeatureViewModel> cls = viewModelClassFor(featureId, applicationFeatureRepository);
        if(cls == null) {
            // TODO: not sure why, yet...
            return null;
        }
        return container.newViewModelInstance(cls, featureId.asEncodedString());
    }

    private static Class<? extends ApplicationFeatureViewModel> viewModelClassFor(
            final ApplicationFeatureId featureId,
            final ApplicationFeatureRepositoryDefault applicationFeatureRepository) {
        switch (featureId.getType()) {
            case PACKAGE:
                return ApplicationPackage.class;
            case CLASS:
                return ApplicationClass.class;
            case MEMBER:
            final ApplicationFeature feature = applicationFeatureRepository.findFeature(featureId);
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

    ApplicationFeatureViewModel(final ApplicationFeatureId featureId) {
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

    // //////////////////////////////////////

    //region > ViewModel impl
    @Override
    public String viewModelMemento() {
        return getFeatureId().asEncodedString();
    }

    @Override
    public void viewModelInit(final String encodedMemento) {
        final ApplicationFeatureId applicationFeatureId = ApplicationFeatureId.parseEncoded(encodedMemento);
        setFeatureId(applicationFeatureId);
    }

    //endregion

    // //////////////////////////////////////

    //region > featureId (property, programmatic)
    private ApplicationFeatureId featureId;

    @Programmatic
    public ApplicationFeatureId getFeatureId() {
        return featureId;
    }

    public void setFeatureId(final ApplicationFeatureId applicationFeatureId) {
        this.featureId = applicationFeatureId;
    }
    //endregion

    //region > feature (property, programmatic)
    @Programmatic
    ApplicationFeature getFeature() {
        return applicationFeatureRepository.findFeature(getFeatureId());
    }
    //endregion
    
    //region > fullyQualifiedName (property, programmatic)
    @Programmatic // in the title
    public String getFullyQualifiedName() {
        return getFeatureId().getFullyQualifiedName();
    }
    //endregion

    // //////////////////////////////////////

    //region > type (programmatic)
    @Programmatic
    public ApplicationFeatureType getType() {
        return getFeatureId().getType();
    }
    //endregion

    // //////////////////////////////////////

    //region > packageName
    public static class PackageNameDomainEvent extends PropertyDomainEvent<ApplicationFeatureViewModel, String> {}

    @Property(
            domainEvent = PackageNameDomainEvent.class
    )
    @PropertyLayout(typicalLength=ApplicationFeature.TYPICAL_LENGTH_PKG_FQN)
    @MemberOrder(name="Id", sequence = "2.2")
    public String getPackageName() {
        return getFeatureId().getPackageName();
    }

    //endregion

    // //////////////////////////////////////

    //region > className

    public static class ClassNameDomainEvent extends PropertyDomainEvent<ApplicationFeatureViewModel, String> {}

    /**
     * For packages, will be null. Is in this class (rather than subclasses) so is shown in
     * {@link ApplicationPackage#getContents() package contents}.
     */
    @Property(
            domainEvent = ClassNameDomainEvent.class
    )
    @PropertyLayout(typicalLength=ApplicationFeature.TYPICAL_LENGTH_CLS_NAME)
    @MemberOrder(name="Id", sequence = "2.3")
    public String getClassName() {
        return getFeatureId().getClassName();
    }
    public boolean hideClassName() {
        return getType().hideClassName();
    }

    //endregion

    // //////////////////////////////////////

    //region > memberName

    public static class MemberNameDomainEvent extends PropertyDomainEvent<ApplicationFeatureViewModel, String> {}

    /**
     * For packages and class names, will be null.
     */
    @Property(
            domainEvent = MemberNameDomainEvent.class
    )
    @PropertyLayout(typicalLength=ApplicationFeature.TYPICAL_LENGTH_MEMBER_NAME)
    @MemberOrder(name="Id", sequence = "2.4")
    public String getMemberName() {
        return getFeatureId().getMemberName();
    }

    public boolean hideMemberName() {
        return getType().hideMember();
    }


    //endregion

    // //////////////////////////////////////

    //region > parent (property)

    public static class ParentDomainEvent extends PropertyDomainEvent<ApplicationFeatureViewModel, ApplicationFeatureViewModel> {}

    @Property(
            domainEvent = ParentDomainEvent.class
    )
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
        final ApplicationFeature feature = applicationFeatureRepository.findFeature(parentId);
        if (feature == null) {
            return null;
        }
        final Class<?> cls = viewModelClassFor(parentId, applicationFeatureRepository);
        return (ApplicationFeatureViewModel) container.newViewModelInstance(cls, parentId.asEncodedString());

    }
    //endregion

    // //////////////////////////////////////

    //region > contributed (property)

    public static class ContributedDomainEvent extends PropertyDomainEvent<ApplicationFeatureViewModel, Boolean> {}

    /**
     * For packages and class names, will be null.
     */
    @Property(
            domainEvent = ContributedDomainEvent.class
    )
    @MemberOrder(name="Contributed", sequence = "2.5.5")
    public boolean isContributed() {
        return getFeature().isContributed();
    }

    public boolean hideContributed() {
        return getType().hideMember();
    }
    //endregion

    // //////////////////////////////////////

    //region > permissions (collection)
    public static class PermissionsDomainEvent extends CollectionDomainEvent<ApplicationFeatureViewModel, ApplicationPermission> {}

    @Collection(
            domainEvent = PermissionsDomainEvent.class
    )
    @CollectionLayout(
            render = RenderType.EAGERLY
    )
    @MemberOrder(sequence = "10")
    public List<ApplicationPermission> getPermissions() {
        return applicationPermissionRepository.findByFeatureCached(getFeatureId());
    }
    //endregion

    // //////////////////////////////////////

    //region > parentPackage (property, programmatic, for packages & classes only)

    /**
     * The parent package feature of this class or package.
     */
    @Programmatic
    public ApplicationFeatureViewModel getParentPackage() {
        return Functions.asViewModelForId(applicationFeatureRepository, container).apply(getFeatureId().getParentPackageId());
    }
    //endregion

    // //////////////////////////////////////

    //region > equals, hashCode, toString

    private final static String propertyNames = "featureId";

    @Override
    public boolean equals(final Object obj) {
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
    <T extends ApplicationFeatureViewModel> List<T> asViewModels(final SortedSet<ApplicationFeatureId> members) {
        final Function<ApplicationFeatureId, T> function = Functions.<T>asViewModelForId(applicationFeatureRepository, container);
        return Lists.newArrayList(
                Iterables.transform(members, function));
    }
    //endregion

    //region > Functions

    public static class Functions {
        private Functions(){}
        public static <T extends ApplicationFeatureViewModel> Function<ApplicationFeatureId, T> asViewModelForId(
                final ApplicationFeatureRepositoryDefault applicationFeatureRepository, final DomainObjectContainer container) {
            return new Function<ApplicationFeatureId, T>(){
                @Override
                public T apply(final ApplicationFeatureId input) {
                    return (T)ApplicationFeatureViewModel.newViewModel(input, applicationFeatureRepository, container);
                }
            };
        }
        public static <T extends ApplicationFeatureViewModel> Function<ApplicationFeature, T> asViewModel(
                final ApplicationFeatureRepositoryDefault applicationFeatureRepository, final DomainObjectContainer container) {
            return new Function<ApplicationFeature, T>(){
                @Override
                public T apply(final ApplicationFeature input) {
                    return (T) ApplicationFeatureViewModel.newViewModel(input.getFeatureId(), applicationFeatureRepository, container);
                }
            };
        }
    }
    //endregion

    //region > injected services
    @javax.inject.Inject
    DomainObjectContainer container;

    @javax.inject.Inject
    ApplicationFeatureRepositoryDefault applicationFeatureRepository;

    @javax.inject.Inject
    ApplicationPermissionRepository applicationPermissionRepository;
    //endregion

}
