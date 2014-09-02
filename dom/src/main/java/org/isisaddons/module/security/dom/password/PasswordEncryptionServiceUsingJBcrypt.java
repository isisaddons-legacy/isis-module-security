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
        return password == null? null: BCrypt.hashpw(password, salt);
    }

    @Programmatic
    public boolean matches(String candidate, String encrypted) {
        if(candidate == null && encrypted == null) {
            return true;
        }
        if(candidate == null || encrypted == null) {
            return false;
        }
        return BCrypt.checkpw(candidate, encrypted);
    }
}
