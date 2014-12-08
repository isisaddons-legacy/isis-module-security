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
package org.isisaddons.module.security.app.feature;

import java.util.List;
import java.util.SortedSet;
import org.isisaddons.module.security.dom.feature.ApplicationFeatureId;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.Bookmarkable;
import org.apache.isis.applib.annotation.ClassLayout;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.MemberOrder;

@ClassLayout(paged=100)
@Bookmarkable(BookmarkPolicy.AS_ROOT)
public class ApplicationClass extends ApplicationFeatureViewModel {

    //region > constructors

    public ApplicationClass() {
    }

    public ApplicationClass(ApplicationFeatureId featureId) {
        super(featureId);
    }
    //endregion

    //region > actions (collection)
    @MemberOrder(sequence = "20.1")
    @CollectionLayout(render= CollectionLayout.RenderType.EAGERLY)
    public List<ApplicationClassAction> getActions() {
        final SortedSet<ApplicationFeatureId> members = getFeature().getActions();
        return asViewModels(members);
    }
    //endregion

    //region > properties (collection)
    @MemberOrder(sequence = "20.2")
    @CollectionLayout(render= CollectionLayout.RenderType.EAGERLY)
    public List<ApplicationClassProperty> getProperties() {
        final SortedSet<ApplicationFeatureId> members = getFeature().getProperties();
        return asViewModels(members);
    }
    //endregion

    //region > collections (collection)
    @MemberOrder(sequence = "20.3")
    @CollectionLayout(render= CollectionLayout.RenderType.EAGERLY)
    public List<ApplicationClassCollection> getCollections() {
        final SortedSet<ApplicationFeatureId> members = getFeature().getCollections();
        return asViewModels(members);
    }
    //endregion

}
