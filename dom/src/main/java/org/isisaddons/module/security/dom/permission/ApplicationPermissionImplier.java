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


import org.isisaddons.module.security.dom.feature.ApplicationFeatureId;

public interface ApplicationPermissionImplier  {

    /**
     * aka grants or allows.
     */
    boolean implies(ApplicationFeatureId featureId, ApplicationPermissionMode mode);

    /**
     * aka revokes or denies.
     */
    boolean refutes(ApplicationFeatureId featureId, ApplicationPermissionMode mode);

}
