package org.isisaddons.module.security.dom.password;

import java.util.Objects;
import org.apache.isis.applib.annotation.Programmatic;

public class PasswordEncryptionServiceNone implements PasswordEncryptionService {

    @Programmatic
    public String encrypt(String password) {
        return password;
    }

    @Override
    public boolean matches(String candidate, String encrypted) {
        return Objects.equals(candidate, encrypted);
    }

}
