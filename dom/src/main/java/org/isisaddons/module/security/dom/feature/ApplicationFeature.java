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
package org.isisaddons.module.security.dom.feature;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;

import com.google.common.collect.ComparisonChain;

import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;

@javax.jdo.annotations.PersistenceCapable(identityType = IdentityType.DATASTORE)
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@javax.jdo.annotations.DatastoreIdentity(strategy = IdGeneratorStrategy.NATIVE, column = "id")
@Queries({
        @Query(name = "allByName", language = "JDOQL",
                value = "SELECT FROM org.isisaddons.security.dom.ApplicationFeature "
                        + "ORDER BY name"),
        @Query(name = "findByName", language = "JDOQL",
                value = "SELECT FROM org.isisaddons.security.dom.ApplicationFeature "
                        + "WHERE name == :name"),
        @Query(name = "findByPackageName", language = "JDOQL",
                value = "SELECT FROM org.isisaddons.security.dom.ApplicationFeature "
                        + "WHERE packageName == :packageName"),
        @Query(name = "allPackageNames", language = "JDOQL",
                value = "SELECT DISTINCT packageName "
                        + "FROM org.isisaddons.security.dom.ApplicationFeature "
                        + "ORDER BY packageName"),
        @Query(name = "findPackageName", language = "JDOQL",
                value = "SELECT DISTINCT packageName "
                        + "FROM org.isisaddons.security.dom.ApplicationFeature "
                        + "WHERE packageName.matches(:matcher) "
                        + "ORDER BY packageName"),
})
public class ApplicationFeature implements Comparable<ApplicationFeature> {

    private String name;

    @Column(allowsNull = "false")
    @MemberOrder(sequence = "11")
    @Hidden
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String packageName;

    @Column(allowsNull = "false")
    @MemberOrder(sequence = "1")
    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    private String classType;

    @Column(allowsNull = "false")
    @MemberOrder(sequence = "2")
    public String getClassType() {
        return classType;
    }

    public void setClassType(String classType) {
        this.classType = classType;
    }

    private String className;

    @Column(allowsNull = "false")
    @MemberOrder(sequence = "3")
    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    private String memberType;

    @Column(allowsNull = "false")
    @MemberOrder(sequence = "4")
    public String getMemberType() {
        return memberType;
    }

    public void setMemberType(String memberType) {
        this.memberType = memberType;
    }

    private String memberName;

    @Column(allowsNull = "false")
    @MemberOrder(sequence = "5")
    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    @Override
    public int compareTo(ApplicationFeature other) {
        return ComparisonChain.start()
                .compare(getPackageName(), other.getPackageName())
                .compare(getClassName(), other.getClassName())
                .compare(getMemberType(), other.getMemberType())
                .compare(getMemberName(), other.getMemberName())
                .result();
    }

}
