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
package org.isisaddons.module.security.dom.permission;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;
import org.isisaddons.module.security.dom.feature.ApplicationFeatureId;
import org.apache.isis.applib.annotation.Hidden;

/**
 * A serializable value object representing a set of (anonymized){@link org.isisaddons.module.security.dom.permission.ApplicationPermissionValue permission}s.
 *
 * <p>
 *     Intended for value type arithmetic and also for caching.
 * </p>
 */
@Hidden
public class ApplicationPermissionValueSet implements Serializable {

    //region > values
    private final List<ApplicationPermissionValue> values;
    /**
     * Partitions the {@link ApplicationPermissionValue permissions} by feature and within that orders according to their
     * evaluation precedence.
     *
     * <p>
     *     The following sketches out what is stored:
     * </p>
     * <pre>
     *     com.foo.Bar#bip -> ALLOW, CHANGING
     *                     -> ALLOW, VIEWING
     *                     -> VETO, VIEWING
     *                     -> VETO, CHANGING
     *     com.foo.Bar     -> ALLOW, CHANGING
     *                     -> ALLOW, VIEWING
     *                     -> VETO, VIEWING
     *                     -> VETO, CHANGING
     *     com.foo         -> ALLOW, CHANGING
     *                     -> ALLOW, VIEWING
     *                     -> VETO, VIEWING
     *                     -> VETO, CHANGING
     *     com             -> ALLOW, CHANGING
     *                     -> ALLOW, VIEWING
     *                     -> VETO, VIEWING
     *                     -> VETO, CHANGING
     * </pre>
     * 
     * <p>
     *     Note that {@link org.isisaddons.module.security.dom.permission.ApplicationPermissionRule#ALLOW allow} rule
     *     is ordered before {@link org.isisaddons.module.security.dom.permission.ApplicationPermissionRule#VETO veto} rule
     *     meaning that it is checked first and therefore also takes precedence.
     * </p>
     */
    private final Multimap<ApplicationFeatureId, ApplicationPermissionValue> permissionsByFeature = TreeMultimap.create(
            Collections.reverseOrder(ApplicationFeatureId.Comparators.natural()),
            ApplicationPermissionValue.Comparators.natural());
    private final PermissionsEvaluationService permissionsEvaluationService;


    //endregion

    //region > constructor
    ApplicationPermissionValueSet(ApplicationPermissionValue... permissionValues) {
        this(Arrays.asList(permissionValues));
    }
    public ApplicationPermissionValueSet(Iterable<ApplicationPermissionValue> permissionValues) {
        this(permissionValues, null);
    }
    public ApplicationPermissionValueSet(Iterable<ApplicationPermissionValue> permissionValues, PermissionsEvaluationService permissionsEvaluationService) {
        this.values = Collections.unmodifiableList(Lists.newArrayList(permissionValues));
        for (ApplicationPermissionValue permissionValue : permissionValues) {
            final ApplicationFeatureId featureId = permissionValue.getFeatureId();
            permissionsByFeature.put(featureId, permissionValue);
        }
        this.permissionsEvaluationService =
                permissionsEvaluationService != null
                        ? permissionsEvaluationService
                        : PermissionsEvaluationService.DEFAULT;
    }
    //endregion



    //region > grants, evaluate

    public static class Evaluation {
        private final ApplicationPermissionValue permissionValue;
        private final boolean granted;

        public Evaluation(ApplicationPermissionValue permissionValue, boolean granted) {
            this.permissionValue = permissionValue;
            this.granted = granted;
        }

        public ApplicationPermissionValue getCause() {
            return permissionValue;
        }

        public boolean isGranted() {
            return granted;
        }
    }

    public boolean grants(ApplicationFeatureId featureId, ApplicationPermissionMode mode) {
        return evaluate(featureId, mode).isGranted();
    }

    public Evaluation evaluate(
            final ApplicationFeatureId featureId,
            final ApplicationPermissionMode mode) {
        final List<ApplicationFeatureId> pathIds = featureId.getPathIds();
        for (ApplicationFeatureId pathId : pathIds) {
            final Collection<ApplicationPermissionValue> permissionValues = permissionsByFeature.get(pathId);
            final Evaluation evaluation = permissionsEvaluationService.evaluate(featureId, mode, permissionValues);
            if(evaluation != null) {
                return evaluation;
            }
        }
        return new Evaluation(null, false);
    }

    //endregion

    //region > equals, hashCode, toString
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ApplicationPermissionValueSet that = (ApplicationPermissionValueSet) o;

        if (values != null ? !values.equals(that.values) : that.values != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return values != null ? values.hashCode() : 0;
    }


    @Override
    public String toString() {
        return "ApplicationPermissionValueSet{" +
                "values=" + values +
                '}';
    }

    //endregion
}
