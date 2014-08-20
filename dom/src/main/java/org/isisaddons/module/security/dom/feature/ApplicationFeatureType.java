package org.isisaddons.module.security.dom.feature;

enum ApplicationFeatureType {
    PACKAGE {
        @Override
        void init(ApplicationFeatureId feature, String fullyQualifiedName) {
            feature.setPackageName(fullyQualifiedName);
            feature.setClassName(null);
            feature.setMemberName(null);
            feature.type = this;
        }
    },
    CLASS {
        @Override
        void init(ApplicationFeatureId feature, String fullyQualifiedName) {
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
        void init(ApplicationFeatureId feature, String fullyQualifiedName) {
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

    abstract void init(ApplicationFeatureId applicationFeatureId, String fullyQualifiedName);

    static void ensurePackage(ApplicationFeatureId feature) {
        if(feature.type != ApplicationFeatureType.PACKAGE) {
            throw new IllegalStateException("Can only be called for a package; " + feature.toString());
        }
    }

    static void ensurePackageOrClass(ApplicationFeatureId applicationFeatureId) {
        if(applicationFeatureId.type != ApplicationFeatureType.PACKAGE && applicationFeatureId.type != ApplicationFeatureType.CLASS) {
            throw new IllegalStateException("Can only be called for a package or a class; " + applicationFeatureId.toString());
        }
    }

    static void ensureClass(ApplicationFeatureId feature) {
        if(feature.type != ApplicationFeatureType.CLASS) {
            throw new IllegalStateException("Can only be called for a class; " + feature.toString());
        }
    }

    static void ensureMember(ApplicationFeatureId feature) {
        if(feature.type != ApplicationFeatureType.MEMBER) {
            throw new IllegalStateException("Can only be called for a member; " + feature.toString());
        }
    }

}
