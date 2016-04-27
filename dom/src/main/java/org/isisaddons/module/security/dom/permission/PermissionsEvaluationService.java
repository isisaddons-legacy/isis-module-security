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
import java.util.Collection;

import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.core.metamodel.services.appfeat.ApplicationFeatureId;

/**
 * Strategy for determining which permission should apply when there are multiple that apply for a particular target
 * feature Id, and which may conflict with each other.
 *
 * <p>
 *     All implementations of this interface must be {@link java.io.Serializable}, because
 *     an instance is serialized into {@link org.isisaddons.module.security.dom.permission.ApplicationPermissionValueSet}.
 * </p>
 */
public interface PermissionsEvaluationService extends Serializable {

    PermissionsEvaluationService DEFAULT = new PermissionsEvaluationServiceAllowBeatsVeto ();

    /**
     * @param targetMemberId - the target (member) feature to be evaluated
     * @param mode - the mode required, ie {@link org.isisaddons.module.security.dom.permission.ApplicationPermissionMode#VIEWING viewing} or {@link org.isisaddons.module.security.dom.permission.ApplicationPermissionMode#CHANGING changing}.
     * @param permissionValues - permissions to evaluate, guaranteed to passed through in natural order, as per {@link org.isisaddons.module.security.dom.permission.ApplicationPermissionValue.Comparators#natural()}.
     */
    @Programmatic
    ApplicationPermissionValueSet.Evaluation evaluate(
            final ApplicationFeatureId targetMemberId,
            final ApplicationPermissionMode mode,
            final Collection<ApplicationPermissionValue> permissionValues);

}
