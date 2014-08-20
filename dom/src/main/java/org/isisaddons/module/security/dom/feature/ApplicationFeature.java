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

import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.BaseEncoding;
import org.apache.isis.applib.ViewModel;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.util.ObjectContracts;
import org.apache.isis.applib.util.TitleBuffer;

/**
 * Value type (compares only package, class and member names).
 */
public class ApplicationFeature implements ViewModel, Comparable<ApplicationFeature> {

    //region > factory methods
    public static ApplicationFeature newPackage(final String packageName) {
        final ApplicationFeature feature = new ApplicationFeature(ApplicationFeatureType.PACKAGE);
        feature.setPackageName(packageName);
        return feature;
    }

    public static ApplicationFeature newClass(final String fullyQualifiedClassName) {
        final ApplicationFeature feature = new ApplicationFeature(ApplicationFeatureType.CLASS);
        feature.type.init(feature, fullyQualifiedClassName);
        return feature;
    }

    public static ApplicationFeature newMember(final String fullyQualifiedClassName, final String memberName) {
        final ApplicationFeature feature = new ApplicationFeature(ApplicationFeatureType.MEMBER);
        ApplicationFeatureType.CLASS.init(feature, fullyQualifiedClassName);
        feature.type = ApplicationFeatureType.MEMBER;
        feature.setMemberName(memberName);
        return feature;
    }

    //endregion


    //region > constructor

    ApplicationFeatureType type;

    public ApplicationFeature(String asString) {
        viewModelInit(asString);
    }

    ApplicationFeature(ApplicationFeatureType type) {
        this.type = type;
    }

    ApplicationFeature() {
    }

    //endregion

    //region > ViewModel impl

    @Override
    public String viewModelMemento() {
        final String join = Joiner.on(":").join(type, getFullyQualifiedName());
        return base64UrlEncode(join);
    }

    @Override
    public void viewModelInit(String encodedMemento) {
        final String s = base64UrlDecode(encodedMemento);
        final Iterator<String> iterator = Splitter.on(":").split(s).iterator();
        final ApplicationFeatureType type = ApplicationFeatureType.valueOf(iterator.next());
        type.init(this, iterator.next());
    }

    private static String base64UrlDecode(String str) {
        final byte[] bytes = BaseEncoding.base64Url().decode(str);
        return new String(bytes, Charset.forName("UTF-8"));
    }

    private static String base64UrlEncode(final String str) {
        byte[] bytes = str.getBytes(Charset.forName("UTF-8"));
        return BaseEncoding.base64Url().encode(bytes);
    }


    //endregion

    //region > identification
    /**
     * having a title() method (rather than using @Title annotation) is necessary as a workaround to be able to use
     * wrapperFactory#unwrap(...) method, which is otherwise broken in Isis 1.6.0
     */
    public String title() {
        final TitleBuffer buf = new TitleBuffer();
        buf.append(getFullyQualifiedName());
        return buf.toString();
    }
    //endregion

    //region > FullyQualifiedName

    @MemberOrder(sequence = "1.2")
    public String getFullyQualifiedName() {
        final StringBuilder buf = new StringBuilder();
        buf.append(getPackageName());
        if(getClassName() != null) {
            buf.append(".").append(getClassName());
        }
        if(getMemberName() != null) {
            buf.append("#").append(getMemberName());
        }
        return buf.toString();
    }

    //endregion

    //region > Type
    public ApplicationFeatureType getType() {
        return type;
    }
    //endregion


    //region > PackageName

    private String packageName;

    @MemberOrder(sequence = "1.2")
    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    //endregion

    //region > ClassName (optional)

    private String className;

    @MemberOrder(sequence = "1")
    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public boolean hideClassName() {
        return type.hideClassName();
    }
    //endregion

    //region > MemberName (optional)

    private String memberName;

    @MemberOrder(sequence = "3")
    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public boolean hideMemberName() {
        return type.hideMemberName();
    }
    //endregion



    //region > Packages: Contents
    private SortedSet<ApplicationFeature> contents = Sets.newTreeSet();

    public SortedSet<ApplicationFeature> getContents() {
        type.ensurePackage(this);
        return contents;
    }

    void addToContents(ApplicationFeature content) {
        type.ensurePackage(this);
        type.ensurePackageOrClass(content);
        this.contents.add(content);
    }
    //endregion

    //region > Package or Class: getParentPackage

    /**
     * The parent package feature of this class or package.
     *
     * <p>
     *  Note that the feature will <i>not</i> be the same instance as the package found using
     *  {@link ApplicationFeatures#findPackage(org.isisaddons.module.security.dom.feature.ApplicationFeature)}; the
     *  latter (<i>canonical</i> instance) will have its {@link ApplicationFeature#getContents() contents} populated.
     * </p>
     * @return
     */
    @Programmatic
    public ApplicationFeature getParentPackage() {
        type.ensurePackageOrClass(this);

        if(type == ApplicationFeatureType.CLASS) {
            return ApplicationFeature.newPackage(getPackageName());
        } else {
            final String packageName = getPackageName(); // eg aaa.bbb.ccc

            if(Strings.isNullOrEmpty(packageName)) {
                return null; // this is root
            }

            if(!packageName.contains(".")) {
                return newPackage(""); // parent is root
            }

            final Iterable<String> split = Splitter.on(".").split(packageName);
            final List<String> parts = Lists.newArrayList(split); // eg [aaa,bbb,ccc]
            parts.remove(parts.size()-1); // remove last, eg [aaa,bbb]

            final String parentPackageName = Joiner.on(".").join(parts); // eg aaa.bbb

            return newPackage(parentPackageName);
        }
    }

    //endregion



    //region > Classes: Members
    private SortedSet<ApplicationFeature> members = Sets.newTreeSet();

    public SortedSet<ApplicationFeature> getMembers() {
        type.ensureClass(this);
        return members;
    }

    void addToMembers(ApplicationFeature member) {
        type.ensureClass(this);
        type.ensureMember(member);
        this.members.add(member);
    }
    //endregion




    //region > equals, hashCode, compareTo, toString

    private final static String propertyNames = "type, packageName, className, memberName";

    @Override
    public int compareTo(ApplicationFeature o) {
        return ObjectContracts.compare(this, o, propertyNames);
    }

    @Override
    public boolean equals(Object obj) {
        return ObjectContracts.equals(this, obj, propertyNames);
    }

    @Override
    public int hashCode() {
        return ObjectContracts.hashCode(this, propertyNames);
    }

    @Override
    public String toString() {
        switch (type) {
            case PACKAGE:
                return ObjectContracts.toString(this, "type, packageName");
            case CLASS:
                return ObjectContracts.toString(this, "type, packageName, className");
            default:
                return ObjectContracts.toString(this, propertyNames);
        }
    }

    //endregion


    //region > injected services
    //endregion


}
