package org.isisaddons.module.security.dom.feature;

import org.apache.isis.core.commons.lang.StringExtensions;

public enum ApplicationMemberType {
    PROPERTY,
    COLLECTION,
    ACTION;

    @Override
    public String toString() {
        return StringExtensions.capitalize(name());
    }

}
