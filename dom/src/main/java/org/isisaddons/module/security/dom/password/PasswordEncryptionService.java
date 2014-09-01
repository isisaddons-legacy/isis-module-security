package org.isisaddons.module.security.dom.password;

import org.apache.isis.applib.annotation.Programmatic;

public interface PasswordEncryptionService {

    @Programmatic
    public String encrypt(String password);

    @Programmatic
    public boolean matches(String candidate, String encrypted);
}
