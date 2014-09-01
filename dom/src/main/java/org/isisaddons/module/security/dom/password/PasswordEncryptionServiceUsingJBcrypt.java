package org.isisaddons.module.security.dom.password;

import javax.annotation.PostConstruct;
import org.mindrot.jbcrypt.BCrypt;
import org.apache.isis.applib.annotation.Programmatic;

public class PasswordEncryptionServiceUsingJBcrypt implements PasswordEncryptionService {

    String salt;

    @PostConstruct
    public void init() {
        salt = BCrypt.gensalt();
    }

    @Programmatic
    public String encrypt(String password) {
        return BCrypt.hashpw(password, salt);
    }

    @Programmatic
    public boolean matches(String candidate, String encrypted) {
        return BCrypt.checkpw(candidate, encrypted);
    }
}
