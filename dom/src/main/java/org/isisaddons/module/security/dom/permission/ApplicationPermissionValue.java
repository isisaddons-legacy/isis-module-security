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

    //region > equals, hashCode, compareTo, toString

    private final static String propertyNames = "rule, mode, featureId";

    @Override
    public int compareTo(ApplicationPermissionValue o) {
        return ObjectContracts.compare(this, o, propertyNames);
    }

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

}
