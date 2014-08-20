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

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.ViewModel;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.util.ObjectContracts;
import org.apache.isis.applib.util.TitleBuffer;

/**
 * View model identified by {@link ApplicationFeatureId} and backed by an {@link org.isisaddons.module.security.dom.feature.ApplicationFeature}.
 */
public class ApplicationFeatureViewModel implements ViewModel {

    //region > constructors
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
        final TitleBuffer buf = new TitleBuffer();
        buf.append(getFullyQualifiedName());
        return buf.toString();
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

    //region > FeatureId
    private ApplicationFeatureId featureId;

    @Programmatic
    public ApplicationFeatureId getFeatureId() {
        return featureId;
    }

    public void setFeatureId(ApplicationFeatureId applicationFeatureId) {
        this.featureId = applicationFeatureId;
    }
    //endregion

    //region > Feature
    @Programmatic
    ApplicationFeature getFeature() {
        return applicationFeatures.findFeature(getFeatureId());
    }
    //endregion
    
    //region > FullyQualifiedName
    @MemberOrder(sequence = "1")
    public String getFullyQualifiedName() {
        return getFeatureId().getFullyQualifiedName();
    }
    //endregion

    //region > Type, PackageName, ClassName, MemberName
    @MemberOrder(sequence = "2.1")
    public ApplicationFeatureType getType() {
        return getFeatureId().getType();
    }

    @MemberOrder(sequence = "2.2")
    public String getPackageName() {
        return getFeatureId().getPackageName();
    }

    @MemberOrder(sequence = "2.3")
    public String getClassName() {
        return getFeatureId().getClassName();
    }
    public boolean hideClassName() {
        return getFeatureId().getType().hideClassName();
    }

    @MemberOrder(sequence = "2.3")
    public String getMemberName() {
        return getFeatureId().getMemberName();
    }
    public boolean hideMemberName() {
        return getFeatureId().getType().hideMemberName();
    }
    //endregion

    //region > Packages: Contents
    public List<ApplicationFeatureViewModel> getContents() {
        final SortedSet<ApplicationFeatureId> contents = getFeature().getContents();
        return asViewModels(contents);
    }
    //endregion

    //region > Package or Class: getParentPackage

    /**
     * The parent package feature of this class or package.
     */
    @Programmatic
    public ApplicationFeatureViewModel getParentPackage() {
        return Functions.asViewModel(container).apply(getFeatureId().getParentPackageId());
    }
    //endregion

    //region > Classes: Members
    public List<ApplicationFeatureViewModel> getMembers() {
        final SortedSet<ApplicationFeatureId> members = getFeature().getMembers();
        return asViewModels(members);
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
    private ArrayList<ApplicationFeatureViewModel> asViewModels(SortedSet<ApplicationFeatureId> members) {
        return Lists.newArrayList(
                Iterables.transform(members, Functions.asViewModel(container)));
    }
    //endregion

    //region > Functions

    static class Functions {
        private Functions(){}
        public static Function<ApplicationFeatureId, ApplicationFeatureViewModel> asViewModel(final DomainObjectContainer container) {
            return new Function<ApplicationFeatureId, ApplicationFeatureViewModel>(){
                @Override
                public ApplicationFeatureViewModel apply(ApplicationFeatureId input) {
                    return container.newViewModelInstance(ApplicationFeatureViewModel.class, input.asEncodedString());
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
    //endregion

}
