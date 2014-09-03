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
package org.isisaddons.module.security.app.feature;

import org.isisaddons.module.security.dom.feature.ApplicationFeatureId;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.Bookmarkable;
import org.apache.isis.applib.annotation.MemberOrder;

@Bookmarkable(BookmarkPolicy.AS_CHILD)
public abstract class ApplicationClassMember extends ApplicationFeatureViewModel {

    //region > constructors
    public ApplicationClassMember() {
    }

    public ApplicationClassMember(ApplicationFeatureId featureId) {
        super(featureId);
    }
    //endregion

    //region > memberName (properties)
    @MemberOrder(name="Id", sequence = "2.4")
    public String getMemberName() {
        return super.getMemberName();
    }
    //endregion



}


