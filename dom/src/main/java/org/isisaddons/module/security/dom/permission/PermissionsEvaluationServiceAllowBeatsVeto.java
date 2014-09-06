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

import java.util.Collection;

/**
 * An implementation whereby a VETO permission for a feature overrides an ALLOW (for same scope).
 */
public class PermissionsEvaluationServiceAllowBeatsVeto extends PermissionsEvaluationServiceAbstract {

    /**
     * Returns the lists unchanged.
     *
     * <p>
     * This implementation relies on the fact that the {@link org.isisaddons.module.security.dom.permission.ApplicationPermissionValue}s are
     * passed through in natural order, with the leading part based on the
     * {@link org.isisaddons.module.security.dom.permission.ApplicationPermissionValue#getRule() rule} and with
     * {@link ApplicationPermissionRule} in turn comparable so that {@link ApplicationPermissionRule#ALLOW allow}
     * is ordered before {@link ApplicationPermissionRule#VETO veto}.
     * </p>
     */
    @Override
    protected Iterable<ApplicationPermissionValue> ordered(
            final Collection<ApplicationPermissionValue> permissionValues) {
        return permissionValues;
    }

}
