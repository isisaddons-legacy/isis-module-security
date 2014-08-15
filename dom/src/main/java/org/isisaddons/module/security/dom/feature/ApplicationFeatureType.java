package org.isisaddons.module.security.dom.feature;

/**
* Created by Dan on 15/08/2014.
*/
enum ApplicationFeatureType {
    PACKAGE {
        @Override
        void init(ApplicationFeature feature, String fullyQualifiedName) {
            feature.setPackageName(fullyQualifiedName);
            feature.setClassName(null);
            feature.setMemberName(null);
            feature.type = this;
        }
    },
    CLASS {
        @Override
        void init(ApplicationFeature feature, String fullyQualifiedName) {
            final int i = fullyQualifiedName.lastIndexOf(".");
            if(i != -1) {
                feature.setPackageName(fullyQualifiedName.substring(0, i));
                feature.setClassName(fullyQualifiedName.substring(i+1));
            } else {
                feature.setPackageName("");
                feature.setClassName(fullyQualifiedName);
            }
            feature.setMemberName(null);
            feature.type = this;
        }
    },
    MEMBER {
        @Override
        void init(ApplicationFeature feature, String fullyQualifiedName) {
            final int i = fullyQualifiedName.lastIndexOf("#");
            if(i == -1) {
                throw new IllegalArgumentException("Malformed, expected a '#': " + fullyQualifiedName);
            }
            final String className = fullyQualifiedName.substring(0, i);
            final String memberName = fullyQualifiedName.substring(i+1);
            CLASS.init(feature, className);
            feature.setMemberName(memberName);
            feature.type = this;
        }
    };

    public boolean hideClassName() {
        return this == ApplicationFeatureType.PACKAGE;
    }
    public boolean hideMemberName() {
        return this == ApplicationFeatureType.PACKAGE || this == ApplicationFeatureType.CLASS;
    }

    abstract void init(ApplicationFeature applicationFeature, String fullyQualifiedName);

    static void ensurePackage(ApplicationFeature feature) {
        if(feature.type != ApplicationFeatureType.PACKAGE) {
            throw new IllegalStateException("Can only be called for a package; " + feature.toString());
        }
    }

    static void ensurePackageOrClass(ApplicationFeature feature) {
        if(feature.type != ApplicationFeatureType.PACKAGE && feature.type != ApplicationFeatureType.CLASS) {
            throw new IllegalStateException("Can only be called for a package or a class; " + feature.toString());
        }
    }

    static void ensureClass(ApplicationFeature feature) {
        if(feature.type != ApplicationFeatureType.CLASS) {
            throw new IllegalStateException("Can only be called for a class; " + feature.toString());
        }
    }

    static void ensureMember(ApplicationFeature feature) {
        if(feature.type != ApplicationFeatureType.MEMBER) {
            throw new IllegalStateException("Can only be called for a member; " + feature.toString());
        }
    }
}
