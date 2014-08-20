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
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.util.ObjectContracts;
import org.apache.isis.applib.util.TitleBuffer;

/**
 * Value type (compares only package, class and member names).
 */
public class ApplicationFeatureId implements Comparable<ApplicationFeatureId> {

    //region > factory methods
    public static ApplicationFeatureId newPackage(final String packageName) {
        final ApplicationFeatureId feature = new ApplicationFeatureId(ApplicationFeatureType.PACKAGE);
        feature.setPackageName(packageName);
        return feature;
    }

    public static ApplicationFeatureId newClass(final String fullyQualifiedClassName) {
        final ApplicationFeatureId feature = new ApplicationFeatureId(ApplicationFeatureType.CLASS);
        feature.type.init(feature, fullyQualifiedClassName);
        return feature;
    }

    public static ApplicationFeatureId newMember(final String fullyQualifiedClassName, final String memberName) {
        final ApplicationFeatureId feature = new ApplicationFeatureId(ApplicationFeatureType.MEMBER);
        ApplicationFeatureType.CLASS.init(feature, fullyQualifiedClassName);
        feature.type = ApplicationFeatureType.MEMBER;
        feature.setMemberName(memberName);
        return feature;
    }

    /**
     * Round-trip with {@link #asString()}
     */
    public static ApplicationFeatureId parse(String asString) {
        return new ApplicationFeatureId(asString);
    }

    /**
     * Round-trip with {@link #asEncodedString()}
     */
    public static ApplicationFeatureId parseEncoded(String encodedString) {
        return new ApplicationFeatureId(base64UrlDecode(encodedString));
    }
    //endregion

    //region > constructor

    private ApplicationFeatureId(String asString) {
        final Iterator<String> iterator = Splitter.on(":").split(asString).iterator();
        final ApplicationFeatureType type1 = ApplicationFeatureType.valueOf(iterator.next());
        type1.init(this, iterator.next());
    }

    ApplicationFeatureId(ApplicationFeatureType type) {
        this.type = type;
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
    ApplicationFeatureType type;

    public ApplicationFeatureType getType() {
        return type;
    }
    //endregion

    //region > PackageName

    private String packageName;

    @Programmatic
    public String getPackageName() {
        return packageName;
    }

    void setPackageName(String packageName) {
        this.packageName = packageName;
    }
    //endregion

    //region > ClassName (optional)

    private String className;

    @Programmatic
    public String getClassName() {
        return className;
    }

    void setClassName(String className) {
        this.className = className;
    }
    //endregion

    //region > MemberName (optional)
    private String memberName;

    @Programmatic
    public String getMemberName() {
        return memberName;
    }

    void setMemberName(String memberName) {
        this.memberName = memberName;
    }
    //endregion

    //region > Package or Class: getParentPackageId

    /**
     * The parent package feature of this class or package.
     *
     * <p>
     *  Note that the feature will <i>not</i> be the same instance as the package found using
     *  {@link org.isisaddons.module.security.dom.feature.ApplicationFeatures#findPackage(org.isisaddons.module.security.dom.feature.ApplicationFeatureId)}; the
     *  latter (<i>canonical</i> instance) will have its {@link org.isisaddons.module.security.dom.feature.ApplicationFeatureId#getContents() contents} populated.
     * </p>
     * @return
     */
    @Programmatic
    public ApplicationFeatureId getParentPackageId() {
        type.ensurePackageOrClass(this);

        if(type == ApplicationFeatureType.CLASS) {
            return ApplicationFeatureId.newPackage(getPackageName());
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

    //region > asString, asEncodedString

    @Programmatic
    public String asString() {
        return Joiner.on(":").join(type, getFullyQualifiedName());
    }

    @Programmatic
    public String asEncodedString() {
        return base64UrlEncode(asString());
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

    //region > equals, hashCode, compareTo, toString

    private final static String propertyNames = "type, packageName, className, memberName";

    @Override
    public int compareTo(ApplicationFeatureId o) {
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
            case MEMBER:
                return ObjectContracts.toString(this, propertyNames);
        }
        throw new IllegalStateException("Unknown feature type " + type);
    }
    //endregion

}
