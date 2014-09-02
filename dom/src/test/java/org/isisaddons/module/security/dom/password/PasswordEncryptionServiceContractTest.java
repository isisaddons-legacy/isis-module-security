package org.isisaddons.module.security.dom.password;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public abstract class PasswordEncryptionServiceContractTest {

    public static final String PASSWORD = "abcdef12345ghijk^&*()";
    public static final String PASSWORD_DIFFERENT = "Xabcdef12345ghijk^&*()";

    private PasswordEncryptionService service;

    @Before
    public void setUp() throws Exception {
        service = newPasswordEncryptionService();
    }

    @Test
    public void encrypt_forNonNull() throws Exception {

        // given
        final String encrypted = service.encrypt(PASSWORD);

        // when, then
        assertThat(encrypted, is(notNullValue()));
    }

    @Test
    public void encrypt_forNull() throws Exception {

        // given
        final String encrypted = service.encrypt(null);

        // when, then
        assertThat(encrypted, is(nullValue()));
    }


    @Test
    public void matches_whenMatches() throws Exception {

        // given
        final String encrypted = service.encrypt(PASSWORD);

        // when, then
        assertThat(service.matches(PASSWORD, encrypted), is(true));
    }


    @Test
    public void matches_whenDoesNotMatch() throws Exception {

        // given
        final String encrypted = service.encrypt(PASSWORD);

        // when, then
        assertThat(service.matches(PASSWORD_DIFFERENT, encrypted), is(false));
    }

    @Test
    public void matches_whenMatchingNullAgainstEncrypted() throws Exception {

        // when
        final String encrypted = service.encrypt(PASSWORD);

        // then
        assertThat(service.matches(null, encrypted), is(false));
    }

    @Test
    public void matches_whenMatchingNullAgainstNull() throws Exception {

        // then
        assertThat(service.matches(null, null), is(true));
    }

    @Test
    public void matches_whenMatchingNonNullAgainstNull() throws Exception {

        // then
        assertThat(service.matches(PASSWORD, null), is(false));
    }

    @Test
    public void whenCheckedWithAnotherServiceInstance() throws Exception {

        // given
        final String encrypted = service.encrypt(PASSWORD);
        final PasswordEncryptionService service2 = newPasswordEncryptionServiceDifferentSalt();

        // when, then
        assertThat(service2.matches(PASSWORD, encrypted), is(true));
    }


    protected abstract PasswordEncryptionService newPasswordEncryptionService();
    protected abstract PasswordEncryptionService newPasswordEncryptionServiceDifferentSalt();

}