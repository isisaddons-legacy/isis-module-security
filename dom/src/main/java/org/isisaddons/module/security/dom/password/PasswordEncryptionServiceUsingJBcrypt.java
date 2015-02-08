/*
 *  Copyright 2014 Dan Haywood
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
package org.isisaddons.module.security.dom.password;

import org.mindrot.jbcrypt.BCrypt;

import org.apache.isis.applib.annotation.Programmatic;

public class PasswordEncryptionServiceUsingJBcrypt implements PasswordEncryptionService {

    String salt;

    private String getSalt() {
        if (salt == null) {
            salt = BCrypt.gensalt();
        }
        return salt;
    }

    @Programmatic
    @Override
    public String encrypt(String password) {
        return password == null ? null : BCrypt.hashpw(password, getSalt());
    }

    @Programmatic
    @Override
    public boolean matches(String candidate, String encrypted) {
        if (candidate == null && encrypted == null) {
            return true;
        }
        if (candidate == null || encrypted == null) {
            return false;
        }
        return BCrypt.checkpw(candidate, encrypted);
    }
}
