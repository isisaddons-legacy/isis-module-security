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
package org.isisaddons.module.security.fixture.dom.example.nontenanted;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.MemberGroupLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Title;

@javax.jdo.annotations.PersistenceCapable(identityType= IdentityType.DATASTORE)
@javax.jdo.annotations.DatastoreIdentity(
        strategy= IdGeneratorStrategy.NATIVE,
        column="id")
@javax.jdo.annotations.Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@javax.jdo.annotations.Uniques({
        @javax.jdo.annotations.Unique(
                name = "NonTenantedEntity_name_UNQ", members = { "name" })
})
@DomainObject(
        objectType = "isissecurityDemo.NonTenantedEntity"
)
@MemberGroupLayout(columnSpans = {4,4,4,12},
        left = {"General"},
        middle = {},
        right = {}
)
public class NonTenantedEntity {

    public static final int MAX_LENGTH_NAME = 30;
    public static final int MAX_LENGTH_DESCRIPTION = 254;

    //region > name

    private String name;

    @javax.jdo.annotations.Column(allowsNull="false", length = MAX_LENGTH_NAME)
    @Title(sequence="1")
    @MemberOrder(sequence="1")
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }
    //endregion

    //region > description

    private String description;

    @javax.jdo.annotations.Column(allowsNull="true", length = MAX_LENGTH_DESCRIPTION)
    @MemberOrder(sequence="2")
    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }
    //endregion


}
