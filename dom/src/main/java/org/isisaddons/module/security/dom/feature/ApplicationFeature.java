/*
 *  Copyright 2014 Dan Haywood
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

import java.util.SortedSet;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.util.ObjectContracts;

/**
 * Canonical application feature, identified by {@link org.isisaddons.module.security.dom.feature.ApplicationFeatureId},
 * and wired together with other application features and cached by {@link org.isisaddons.module.security.dom.feature.ApplicationFeatures}.
 */
@Hidden
public class ApplicationFeature implements Comparable<ApplicationFeature> {

    //region > constants

    // using same value for all to neaten up rendering
    public static final int TYPICAL_LENGTH_PKG_FQN = 50;
    public static final int TYPICAL_LENGTH_CLS_NAME = 50;
    public static final int TYPICAL_LENGTH_MEMBER_NAME = 50;
    //endregion

    //region > constructors
    public ApplicationFeature() {
        this(null);
    }
    public ApplicationFeature(final ApplicationFeatureId featureId) {
        setFeatureId(featureId);
    }
    //endregion

    //region > featureId
    private ApplicationFeatureId featureId;

    @Programmatic
    public ApplicationFeatureId getFeatureId() {
        return featureId;
    }

    public void setFeatureId(ApplicationFeatureId applicationFeatureId) {
        this.featureId = applicationFeatureId;
    }
    //endregion

    //region > memberType
    private ApplicationMemberType memberType;

    /**
     * Only for {@link org.isisaddons.module.security.dom.feature.ApplicationFeatureType#MEMBER member}s.
     */
    @Programmatic
    public ApplicationMemberType getMemberType() {
        return memberType;
    }

    public void setMemberType(ApplicationMemberType memberType) {
        this.memberType = memberType;
    }
    //endregion

    //region > returnTypeName (for: properties, collections, actions)
    private String returnTypeName;

    /**
     * Only for {@link org.isisaddons.module.security.dom.feature.ApplicationMemberType#ACTION action}s.
     */
    @Programmatic
    public String getReturnTypeName() {
        return returnTypeName;
    }

    public void setReturnTypeName(String returnTypeName) {
        this.returnTypeName = returnTypeName;
    }
    //endregion

    //region > contributed (for: properties, collections, actions)
    private boolean contributed;

    @Programmatic
    public boolean isContributed() {
        return contributed;
    }

    public void setContributed(boolean contributed) {
        this.contributed = contributed;
    }
    //endregion

    //region > derived (properties and collections)
    private Boolean derived;

    /**
     * Only for {@link org.isisaddons.module.security.dom.feature.ApplicationMemberType#PROPERTY} and {@link org.isisaddons.module.security.dom.feature.ApplicationMemberType#COLLECTION}
     */
    @Programmatic
    public Boolean isDerived() {
        return derived;
    }

    public void setDerived(Boolean derived) {
        this.derived = derived;
    }
    //endregion

    //region > propertyMaxLength (properties only)
    private Integer propertyMaxLength;

    /**
     * Only for {@link org.isisaddons.module.security.dom.feature.ApplicationMemberType#ACTION action}s.
     */
    @Programmatic
    public Integer getPropertyMaxLength() {
        return propertyMaxLength;
    }

    public void setPropertyMaxLength(Integer propertyMaxLength) {
        this.propertyMaxLength = propertyMaxLength;
    }
    //endregion

    //region > propertyTypicalLength (properties only)
    private Integer propertyTypicalLength;

    /**
     * Only for {@link org.isisaddons.module.security.dom.feature.ApplicationMemberType#ACTION action}s.
     */
    @Programmatic
    public Integer getPropertyTypicalLength() {
        return propertyTypicalLength;
    }

    public void setPropertyTypicalLength(Integer propertyTypicalLength) {
        this.propertyTypicalLength = propertyTypicalLength;
    }
    //endregion

    //region > actionSemantics (actions only)
    private ActionSemantics.Of actionSemantics;

    /**
     * Only for {@link org.isisaddons.module.security.dom.feature.ApplicationMemberType#ACTION action}s.
     */
    @Programmatic
    public ActionSemantics.Of getActionSemantics() {
        return actionSemantics;
    }

    public void setActionSemantics(ActionSemantics.Of actionSemantics) {
        this.actionSemantics = actionSemantics;
    }
    //endregion

    //region > packages: Contents
    private SortedSet<ApplicationFeatureId> contents = Sets.newTreeSet();

    @Programmatic
    public SortedSet<ApplicationFeatureId> getContents() {
        ApplicationFeatureType.ensurePackage(this.getFeatureId());
        return contents;
    }

    @Programmatic
    public void addToContents(ApplicationFeatureId contentId) {
        ApplicationFeatureType.ensurePackage(this.getFeatureId());
        ApplicationFeatureType.ensurePackageOrClass(contentId);
        this.contents.add(contentId);
    }
    //endregion

    //region > classes: Properties, Collections, Actions
    private SortedSet<ApplicationFeatureId> properties = Sets.newTreeSet();

    @Programmatic
    public SortedSet<ApplicationFeatureId> getProperties() {
        ApplicationFeatureType.ensureClass(this.getFeatureId());
        return properties;
    }


    private SortedSet<ApplicationFeatureId> collections = Sets.newTreeSet();
    @Programmatic
    public SortedSet<ApplicationFeatureId> getCollections() {
        ApplicationFeatureType.ensureClass(this.getFeatureId());
        return collections;
    }


    private SortedSet<ApplicationFeatureId> actions = Sets.newTreeSet();

    @Programmatic
    public SortedSet<ApplicationFeatureId> getActions() {
        ApplicationFeatureType.ensureClass(this.getFeatureId());
        return actions;
    }

    @Programmatic
    public void addToMembers(ApplicationFeatureId memberId, ApplicationMemberType memberType) {
        ApplicationFeatureType.ensureClass(this.getFeatureId());
        ApplicationFeatureType.ensureMember(memberId);

        membersOf(memberType).add(memberId);
    }

    @Programmatic
    public SortedSet<ApplicationFeatureId> membersOf(ApplicationMemberType memberType) {
        ApplicationFeatureType.ensureClass(this.getFeatureId());
        switch (memberType) {
            case PROPERTY:
                return properties;
            case COLLECTION:
                return collections;
            default: // case ACTION:
                return actions;
        }
    }
    //endregion

    //region > Functions

    public static class Functions {
        private Functions(){}

        public static final Function<? super ApplicationFeature, ? extends String> GET_FQN = new Function<ApplicationFeature, String>() {
            @Override
            public String apply(ApplicationFeature input) {
                return input.getFeatureId().getFullyQualifiedName();
            }
        };

        public static final Function<ApplicationFeature, ApplicationFeatureId> GET_ID =
                new Function<ApplicationFeature, ApplicationFeatureId>() {
            @Override
            public ApplicationFeatureId apply(ApplicationFeature input) {
                return input.getFeatureId();
            }
        };
    }

    public static class Predicates {
        private Predicates(){}

        public static Predicate<ApplicationFeature> packageContainingClasses(
                final ApplicationMemberType memberType, final ApplicationFeatures applicationFeatures) {
            return new Predicate<ApplicationFeature>() {
                @Override
                public boolean apply(ApplicationFeature input) {
                    // all the classes in this package
                    final Iterable<ApplicationFeatureId> classIds =
                            Iterables.filter(input.getContents(),
                                    ApplicationFeatureId.Predicates.isClassContaining(memberType, applicationFeatures));
                    return classIds.iterator().hasNext();
                }
            };
        }
    }

    //endregion

    //region > equals, hashCode, compareTo, toString

    private final static String propertyNames = "featureId";

    @Override
    public int compareTo(ApplicationFeature o) {
        return ObjectContracts.compare(this, o, propertyNames);
    }

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

}
