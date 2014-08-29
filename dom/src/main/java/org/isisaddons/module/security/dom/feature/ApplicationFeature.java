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

import java.util.SortedSet;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.util.ObjectContracts;

/**
 * Canonical application feature, identified by {@link org.isisaddons.module.security.dom.feature.ApplicationFeatureId},
 * and wired together with other application features and cached by {@link org.isisaddons.module.security.dom.feature.ApplicationFeatures}.
 */
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
    ApplicationFeature(final ApplicationFeatureId featureId) {
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

    //region > featureId
    private ApplicationMemberType memberType;
    @Programmatic
    public ApplicationMemberType getMemberType() {
        return memberType;
    }

    public void setMemberType(ApplicationMemberType memberType) {
        this.memberType = memberType;
    }
    //endregion


    //region > packages: Contents
    private SortedSet<ApplicationFeatureId> contents = Sets.newTreeSet();

    public SortedSet<ApplicationFeatureId> getContents() {
        ApplicationFeatureType.ensurePackage(this.getFeatureId());
        return contents;
    }

    void addToContents(ApplicationFeatureId contentId) {
        ApplicationFeatureType.ensurePackage(this.getFeatureId());
        ApplicationFeatureType.ensurePackageOrClass(contentId);
        this.contents.add(contentId);
    }
    //endregion


    //region > classes: Properties, Collections, Actions
    private SortedSet<ApplicationFeatureId> properties = Sets.newTreeSet();

    public SortedSet<ApplicationFeatureId> getProperties() {
        ApplicationFeatureType.ensureClass(this.getFeatureId());
        return properties;
    }


    private SortedSet<ApplicationFeatureId> collections = Sets.newTreeSet();
    public SortedSet<ApplicationFeatureId> getCollections() {
        ApplicationFeatureType.ensureClass(this.getFeatureId());
        return collections;
    }


    private SortedSet<ApplicationFeatureId> actions = Sets.newTreeSet();

    public SortedSet<ApplicationFeatureId> getActions() {
        ApplicationFeatureType.ensureClass(this.getFeatureId());
        return actions;
    }

    void addToMembers(ApplicationFeatureId memberId, ApplicationMemberType memberType) {
        ApplicationFeatureType.ensureClass(this.getFeatureId());
        ApplicationFeatureType.ensureMember(memberId);

        membersOf(memberType).add(memberId);
    }

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
