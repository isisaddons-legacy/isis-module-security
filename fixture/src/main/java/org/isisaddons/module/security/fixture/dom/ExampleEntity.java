package org.isisaddons.module.security.fixture.dom;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;
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
                name = "ExampleEntity_name_UNQ", members = { "name" })
})
@MemberGroupLayout(columnSpans = {4,4,4,12},
        left = {"General"},
        middle = {},
        right = {}
)
public class ExampleEntity {

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
