/*
 *  Copyright 2014 Jeroen van der Wal
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
package org.isisaddons.module.security.dom.feature;

import java.util.List;
import java.util.SortedSet;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.isisaddons.module.security.dom.permission.ApplicationPermission;
import org.isisaddons.module.security.dom.permission.ApplicationPermissions;
import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.ViewModel;
import org.apache.isis.applib.annotation.*;
import org.apache.isis.applib.util.ObjectContracts;

/**
 * View model identified by {@link ApplicationFeatureId} and backed by an {@link org.isisaddons.module.security.dom.feature.ApplicationFeature}.
 */
@MemberGroupLayout(
        columnSpans = {6,0,0,6},
        left = {"Id", "Parent"}
)
@Bookmarkable
public class ApplicationFeatureViewModel implements ViewModel {


    //region > constructors
    public static ApplicationFeatureViewModel newViewModel(final ApplicationFeatureId featureId, final DomainObjectContainer container) {
        return container.newViewModelInstance(ApplicationFeatureViewModel.class, featureId.asEncodedString());
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

    //region > type, packageName, className, memberName (properties)
    @MemberOrder(name="Type", sequence = "2.1")
    @Hidden(where=Where.OBJECT_FORMS)
    public ApplicationFeatureType getType() {
        return getFeatureId().getType();
    }

    @TypicalLength(60)
    @Hidden(where=Where.PARENTED_TABLES)
    @MemberOrder(name="Id", sequence = "2.2")
    public String getPackageName() {
        return getFeatureId().getPackageName();
    }

    @MemberOrder(name="Id", sequence = "2.3")
    public String getClassName() {
        return getFeatureId().getClassName();
    }
    public boolean hideClassName() {
        return getFeatureId().getType().hideClassName();
    }

    @MemberOrder(name="Id", sequence = "2.4")
    public String getMemberName() {
        return getFeatureId().getMemberName();
    }
    public boolean hideMemberName() {
        return getFeatureId().getType().hideMember();
    }
    @MemberOrder(name="Type", sequence = "2.5")
    @Hidden(where=Where.OBJECT_FORMS)
    public ApplicationMemberType getMemberType() {
        return getFeature().getMemberType();
    }
    public boolean hideMemberType() {
        return getFeatureId().getType().hideMember();
    }
    //endregion

    //region > parent (property)

    @Hidden(where=Where.REFERENCES_PARENT)
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
        return feature != null
                ? container.newViewModelInstance(ApplicationFeatureViewModel.class, parentId.asEncodedString())
                : null;
    }
    //endregion

    //region > permissions (collection)
    @MemberOrder(sequence = "10")
    @Render(Render.Type.EAGERLY)
    public List<ApplicationPermission> getPermissions() {
        final ApplicationFeatureId featureId = this.getFeatureId();
        return applicationPermissions.findByFeature(featureId);
    }
    //endregion

    //region > contents (collection, for packages only)
    @MemberOrder(sequence = "4")
    @Render(Render.Type.EAGERLY)
    public List<ApplicationFeatureViewModel> getContents() {
        final SortedSet<ApplicationFeatureId> contents = getFeature().getContents();
        return asViewModels(contents);
    }
    public boolean hideContents() {
        return getType() != ApplicationFeatureType.PACKAGE;
    }
    //endregion

    //region > parentPackage (property, programmatic, for packages & classes only)

    /**
     * The parent package feature of this class or package.
     */
    @Programmatic
    public ApplicationFeatureViewModel getParentPackage() {
        return Functions.asViewModelForId(container).apply(getFeatureId().getParentPackageId());
    }
    //endregion

    //region > properties (collection, for classes only)
    @MemberOrder(sequence = "20.1")
    @Render(Render.Type.EAGERLY)
    public List<ApplicationFeatureViewModel> getProperties() {
        final SortedSet<ApplicationFeatureId> members = getFeature().getProperties();
        return asViewModels(members);
    }
    public boolean hideProperties() {
        return getType() != ApplicationFeatureType.CLASS;
    }
    //endregion

    //region > collections (collection, for classes only)
    @MemberOrder(sequence = "20.2")
    @Render(Render.Type.EAGERLY)
    public List<ApplicationFeatureViewModel> getCollections() {
        final SortedSet<ApplicationFeatureId> members = getFeature().getCollections();
        return asViewModels(members);
    }
    public boolean hideCollections() {
        return getType() != ApplicationFeatureType.CLASS;
    }
    //endregion

    //region > actions (collection, for classes only)
    @MemberOrder(sequence = "20.3")
    @Render(Render.Type.EAGERLY)
    public List<ApplicationFeatureViewModel> getActions() {
        final SortedSet<ApplicationFeatureId> members = getFeature().getActions();
        return asViewModels(members);
    }
    public boolean hideActions() {
        return getType() != ApplicationFeatureType.CLASS;
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
    private List<ApplicationFeatureViewModel> asViewModels(SortedSet<ApplicationFeatureId> members) {
        return Lists.newArrayList(
                Iterables.transform(members, Functions.asViewModelForId(container)));
    }
    //endregion

    //region > Functions

    public static class Functions {
        private Functions(){}
        public static Function<ApplicationFeatureId, ApplicationFeatureViewModel> asViewModelForId(final DomainObjectContainer container) {
            return new Function<ApplicationFeatureId, ApplicationFeatureViewModel>(){
                @Override
                public ApplicationFeatureViewModel apply(ApplicationFeatureId input) {
                    return ApplicationFeatureViewModel.newViewModel(input, container);
                }
            };
        }
        public static Function<ApplicationFeature, ApplicationFeatureViewModel> asViewModel(final DomainObjectContainer container) {
            return new Function<ApplicationFeature, ApplicationFeatureViewModel>(){
                @Override
                public ApplicationFeatureViewModel apply(ApplicationFeature input) {
                    return ApplicationFeatureViewModel.newViewModel(input.getFeatureId(), container);
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
