package de.adorsys.cse.client.oauth;

import de.adorsys.cse.jwt.Base64EncodedJWT;
import de.adorsys.cse.jwt.Base64EncodedJWTNimbusImpl;
import de.adorsys.cse.jwt.JWT;
import de.adorsys.cse.jwt.JWTNimbusImpl;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AccessTokenExtractorImplTest {
    private AccessTokenExtractor accessTokenExtractor;

    @Before
    public void setUp() {
        accessTokenExtractor = new AccessTokenExtractorImpl();
    }

    @Test
    @Ignore("Not ready yet")
    public void extractAccessToken() throws Exception {
        JWT expectedAccesToken = new JWTNimbusImpl("");

        Base64EncodedJWT inputBase64EncodedJWT = new Base64EncodedJWTNimbusImpl("");

        JWT actualAccessToken = accessTokenExtractor.extractAccessToken(inputBase64EncodedJWT);

        assertEquals("Access Token is extracted", expectedAccesToken, actualAccessToken);

    }

}
