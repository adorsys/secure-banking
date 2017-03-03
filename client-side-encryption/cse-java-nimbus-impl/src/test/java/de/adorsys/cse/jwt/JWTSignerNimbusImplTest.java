package de.adorsys.cse.jwt;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static de.adorsys.cse.Base64StringGenerator.generateRandomBase64String;
import static org.junit.Assert.*;

public class JWTSignerNimbusImplTest {
    private JWTSigner jwtSigner = new JWTSignerNimbusImpl();

    @Test
    public void generateHMacSecretReturnsRandomStringOf32Bytes() {
        Set<byte[]> strings = new HashSet<>(100);
        for (int i = 0; i < 100; i++) {
            byte[] actual = jwtSigner.generateHMacSecret();
            assertEquals("HMAC-key length is 32", 32, actual.length);
            assertFalse("String is always new", strings.contains(actual));
            strings.add(actual);
        }
    }

    @Test
    public void providingNotLongEnoughSecretLeadToExtendingItToMinimalLength() throws Exception {
        String notLongEnoughString = generateRandomBase64String(22);
        //we need a string with length of 32 chars, so we add 10 chars
        String expectedSecret = "AAAAAAAAAA".concat(notLongEnoughString);

        JWT someJWT = new JWTBuilderNimbusImpl().build();

        JWS actual = jwtSigner.sign(someJWT, notLongEnoughString);

        assertEquals("jwt is signed", 3, actual.encode().split("\\.").length);
        assertTrue("jwt is signed", actual.isSigned());

        assertTrue("signature pass verification", jwtSigner.verify(actual, expectedSecret));

    }

    @Test
    public void longSecretKeyHashedSilently() throws Exception {
        String longSecretString = generateRandomBase64String(1_000_000);

        JWT someJWT = new JWTBuilderNimbusImpl().build();

        JWS actual = jwtSigner.sign(someJWT, longSecretString);

        assertEquals("jwt is signed", 3, actual.encode().split("\\.").length);
        assertTrue("jwt is signed", actual.isSigned());

        assertTrue("signature pass verification", jwtSigner.verify(actual, longSecretString));
    }

}
