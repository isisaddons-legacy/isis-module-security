package org.isisaddons.module.security.dom.password;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordEncryptionServiceUsingJBcryptTest extends PasswordEncryptionServiceContractTest {

    @Override
    protected PasswordEncryptionService newPasswordEncryptionService() {
        final PasswordEncryptionServiceUsingJBcrypt service = new PasswordEncryptionServiceUsingJBcrypt();
        service.init();
        return service;
    }

    @Override
    protected PasswordEncryptionService newPasswordEncryptionServiceDifferentSalt() {
        final PasswordEncryptionServiceUsingJBcrypt service = new PasswordEncryptionServiceUsingJBcrypt();
        service.salt = BCrypt.gensalt(12); // a different salt
        return service;
    }
}