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

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import com.google.common.collect.Lists;
import org.apache.isis.applib.annotation.Hidden;

/**
 * A serializable value object representing a set of (anonymized){@link org.isisaddons.module.security.dom.permission.ApplicationPermissionValue permission}s.
 *
 * <p>
 *     Intended for value type arithmetic and also for caching.
 * </p>
 */
@Hidden
public class ApplicationPermissionValueSet implements Serializable {

    //region > constructor
    public ApplicationPermissionValueSet(ApplicationPermissionValue... values) {
        this(Lists.newArrayList(values));
    }
    public ApplicationPermissionValueSet(List<ApplicationPermissionValue> values) {
        this.values = Collections.unmodifiableList(values);
    }
    //endregion

    //region > values
    private final List<ApplicationPermissionValue> values;

    public List<ApplicationPermissionValue> getValues() {
        return values;
    }
    //endregion

    //region > equals, hashCode, toString
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ApplicationPermissionValueSet that = (ApplicationPermissionValueSet) o;

        if (values != null ? !values.equals(that.values) : that.values != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return values != null ? values.hashCode() : 0;
    }


    @Override
    public String toString() {
        return "ApplicationPermissionValueSet{" +
                "values=" + values +
                '}';
    }
    //endregion
}
