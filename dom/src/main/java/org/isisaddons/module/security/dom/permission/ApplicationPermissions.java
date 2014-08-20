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

import java.util.List;
import javax.inject.Inject;
import org.isisaddons.module.security.dom.actor.ApplicationRole;
import org.isisaddons.module.security.dom.feature.ApplicationFeature;
import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.*;

@DomainService
public class ApplicationPermissions {

    //region > newPermission (action)
    @MemberOrder(sequence = "1")
    public ApplicationPermission newPermission(
            final ApplicationRole role,
            final ApplicationFeature feature,
            final @Named("Type") ApplicationPermissionRule type,
            final @Named("Mode") ApplicationPermissionMode mode) {
        final ApplicationPermission permission = container.newTransientInstance(ApplicationPermission.class);
        permission.setFeatureType(feature.getFeatureId().getType());
        permission.setFeatureFqn(feature.getFeatureId().getFullyQualifiedName());
        return null; // TODO: business logic here
    }
    //endregion

    //region > newPermission (action)
    @Prototype
    @ActionSemantics(ActionSemantics.Of.SAFE)
    @MemberOrder(sequence = "99")
    public List<ApplicationPermission> findByRole(final ApplicationRole role) {
        return container.allInstances(ApplicationPermission.class);
    }
    //endregion

    //region > newPermission (action)
    @Prototype
    @ActionSemantics(ActionSemantics.Of.SAFE)
    @MemberOrder(sequence = "99")
    public List<ApplicationPermission> allPermissions() {
        return container.allInstances(ApplicationPermission.class);
    }
    //endregion


    //region  >  (injected)
    @Inject
    DomainObjectContainer container;
    //endregion
}
