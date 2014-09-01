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
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Where;

public class ApplicationClassAction extends ApplicationClassMember {

    //region > constructors

    public ApplicationClassAction() {
    }

    public ApplicationClassAction(ApplicationFeatureId featureId) {
        super(featureId);
    }
    //endregion



    //region > actionSemantics (property)
    @MemberOrder(name="Detail", sequence = "2.8")
    @Hidden(where= Where.OBJECT_FORMS)
    public ActionSemantics.Of getActionSemantics() {
        return getFeature().getActionSemantics();
    }
    //endregion


}