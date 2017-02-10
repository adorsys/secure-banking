package de.adorsys.cse.jwt;

import de.adorsys.cse.client.oauth.AccessTokenExtractor;
import de.adorsys.cse.client.oauth.AccessTokenExtractorImpl;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;

public class JWTBuilderNimbusImplTest {
    private JWTBuilder jwtBuilder;

    @Before
    public void setUp() {
        jwtBuilder = new JWTBuilderNimbusImpl();
    }

    @Test(expected = IllegalArgumentException.class)
    public void withAccessToken_passingNullCausesIllegalArgumentException() {
        jwtBuilder.withAccessToken(null);
        fail("calling withAccessToken(null) causes IllegalArgumentException");
    }

    @Test
    public void withAccessToken() throws Exception {
        final String expectedBase64EncodedAccessToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpZCI6IjYzMjIwNzg0YzUzODA3ZjVmZTc2Yjg4ZjZkNjdlMmExZTIxODlhZTEiLCJjbGllbnRfaWQiOiJUZXN0IENsaWVudCBJRCIsInVzZXJfaWQiOm51bGwsImV4cGlyZXMiOjEzODAwNDQ1NDIsInRva2VuX3R5cGUiOiJiZWFyZXIiLCJzY29wZSI6bnVsbH0.PcC4k8Q_etpU-J4yGFEuBUdeyMJhtpZFkVQ__sXpe78eSi7xTniqOOtgfWa62Y4sj5Npta8xPuDglH8Fueh_APZX4wGCiRE1P4nT4APQCOTbgcuCNXwjmP8znk9F76ID2WxThaMbmpsTTEkuyyUYQKCCdxlIcSbVvcLZUGKZ6-g";

        JWT accessToken = new JWTNimbusImpl(expectedBase64EncodedAccessToken);
        JWT resultToken = jwtBuilder.withAccessToken(accessToken).build("some hMacKey");

        AccessTokenExtractor accessTokenExtractor = new AccessTokenExtractorImpl();
        Optional<JWT> actualAccessToken = accessTokenExtractor.extractAccessToken(resultToken);
        assertTrue("actualAccessToken present", actualAccessToken.isPresent());
        assertEquals("embedded accessToken corresponds to the expected", expectedBase64EncodedAccessToken, actualAccessToken.get().encode());
    }

    @Test
    public void withNonceGenerator() {
        fail("Not implemented");
    }

    @Test
    public void withTimestampGenerator() {
        fail("Not implemented");
    }

    @Test
    public void withExpirationTimeInMs() {
        fail("Not implemented");
    }

    @Test
    public void withEncryptedServerPublicKey() {
        fail("Not implemented");
    }

    @Test
    public void build() {
        fail("Not implemented");
    }

}
