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
package org.isisaddons.module.security.app.user;

import java.util.Collection;
import java.util.List;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.isisaddons.module.security.dom.feature.ApplicationFeature;
import org.isisaddons.module.security.dom.feature.ApplicationFeatures;
import org.isisaddons.module.security.dom.user.ApplicationUser;
import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.*;

@DomainService
public class UserPermissionViewModelContributions  {

    //region > Permissions (derived collection)

    @MemberOrder(sequence = "30")
    @Render(Render.Type.EAGERLY)
    @Paged(50)
    @NotInServiceMenu
    @NotContributed(NotContributed.As.ACTION) // ie contributed as property
    @ActionSemantics(ActionSemantics.Of.SAFE)
    public List<UserPermissionViewModel> permissions(ApplicationUser user) {
        final Collection<ApplicationFeature> allMembers = applicationFeatures.allMembers();
        return Lists.newArrayList(
                Iterables.transform(
                        allMembers,
                        UserPermissionViewModel.Functions.asViewModel(user, container))
        );
    }
    //endregion

    //region > injected
    @javax.inject.Inject
    DomainObjectContainer container;
    @javax.inject.Inject
    ApplicationFeatures applicationFeatures;
    //endregion

}
