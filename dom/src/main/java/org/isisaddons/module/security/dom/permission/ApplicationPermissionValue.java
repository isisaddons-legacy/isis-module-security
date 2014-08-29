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
package org.isisaddons.module.security.dom.permission;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import org.isisaddons.module.security.dom.feature.ApplicationFeatureId;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.util.ObjectContracts;

/**
 * A serializable value object representing an (anonymized)
 * {@link org.isisaddons.module.security.dom.permission.ApplicationPermission}.
 *
 * <p>
 *     Intended for value type arithmetic and also for caching.  No user/role information is held because that information
 *     is not required to perform the arithmetic.
 * </p>
 */
@Hidden
public class ApplicationPermissionValue implements Comparable<ApplicationPermissionValue>, Serializable {

    //region > constructor

    public ApplicationPermissionValue(
            ApplicationFeatureId featureId,
            ApplicationPermissionRule rule,
            ApplicationPermissionMode mode) {
        this.featureId = featureId;
        this.rule = rule;
        this.mode = mode;
    }
    //endregion

    //region > featureId
    private final ApplicationFeatureId featureId;
    public ApplicationFeatureId getFeatureId() {
        return featureId;
    }
    //endregion

    //region > rule
    private final ApplicationPermissionRule rule;
    public ApplicationPermissionRule getRule() {
        return rule;
    }
    //endregion

    //region > mode
    private final ApplicationPermissionMode mode;
    public ApplicationPermissionMode getMode() {
        return mode;
    }
    //endregion

    //region > implies, refutes
    public boolean implies(ApplicationFeatureId featureId, ApplicationPermissionMode mode) {
        if(getRule() != ApplicationPermissionRule.ALLOW) {
            // only allow rules can imply
            return false;
        }
        if(getMode() == ApplicationPermissionMode.VIEWING && mode == ApplicationPermissionMode.CHANGING) {
            // an "allow viewing" permission does not imply ability to change
            return false;
        }

        // determine if this permission is on the path (ie the feature or one of its parents)
        return onPathOf(featureId);
    }

    public boolean refutes(ApplicationFeatureId featureId, ApplicationPermissionMode mode) {
        if(getRule() != ApplicationPermissionRule.VETO) {
            // only veto rules can refute
            return false;
        }
        if(getMode() == ApplicationPermissionMode.CHANGING && mode == ApplicationPermissionMode.VIEWING) {
            // an "veto changing" permission does not refute ability to view
            return false;
        }
        // determine if this permission is on the path (ie the feature or one of its parents)
        return onPathOf(featureId);
    }

    private boolean onPathOf(ApplicationFeatureId featureId) {

        final List<ApplicationFeatureId> pathIds = featureId.getPathIds();
        for (ApplicationFeatureId pathId : pathIds) {
            if(getFeatureId().equals(pathId)) {
                return true;
            }
        }

        return false;
    }

    //endregion

    //region > Comparators
    public static final class Comparators {
        private Comparators(){}
        public static Comparator<ApplicationPermissionValue> evaluationPrecedence() {
            return new Comparator<ApplicationPermissionValue>() {
                @Override
                public int compare(ApplicationPermissionValue o1, ApplicationPermissionValue o2) {
                    return 0;
                }
            };
        }
    }
    //endregion

    //region > equals, hashCode, compareTo, toString

    private final static String propertyNames = "rule, mode, featureId";

    @Override
    public int compareTo(ApplicationPermissionValue o) {
        return ObjectContracts.compare(this, o, propertyNames);
    }

    @Override
    public boolean equals(Object o) {
        // not using because trying to be efficient.  Premature optimization?
        // return ObjectContracts.equals(this, obj, propertyNames);
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ApplicationPermissionValue that = (ApplicationPermissionValue) o;

        if (featureId != null ? !featureId.equals(that.featureId) : that.featureId != null) return false;
        if (mode != that.mode) return false;
        if (rule != that.rule) return false;

        return true;
    }

    @Override
    public int hashCode() {
        // not using because trying to be efficient.  Premature optimization?
        // return ObjectContracts.hashCode(this, propertyNames);
        int result = featureId != null ? featureId.hashCode() : 0;
        result = 31 * result + (rule != null ? rule.hashCode() : 0);
        result = 31 * result + (mode != null ? mode.hashCode() : 0);
        return result;
    }

    //    @Override
//    public boolean equals(final Object obj) {
//    }

//    @Override
//    public int hashCode() {
//    }

    @Override
    public String toString() {
            return ObjectContracts.toString(this, propertyNames);
    }

    //endregion

}
