package de.adorsys.cse.client.oauth;

import de.adorsys.cse.jwk.JWK;
import de.adorsys.cse.jwt.JWT;
import de.adorsys.cse.jwt.JWTNimbusImpl;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

public class PubicKeyExtractorImplTest {
    private PublicKeyExtractor publicKeyExtractor;

    @Before
    public void setUp() {
        publicKeyExtractor = new PubicKeyExtractorImpl();
    }

    @Test(expected = IllegalArgumentException.class)
    public void extractFromNullReturnsIllegalArgumentException() throws Exception {
        publicKeyExtractor.extractPublicKey(null);
        fail("Extract from null object returns IllegalArgumentException");
    }

    @Test
    public void returnsNoValueIfNoPKInToken() throws Exception {
        //There is no "res_pub_key" claim in this token
        JWT inputTokenWithoutPK = new JWTNimbusImpl("eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpZCI6IjYzMjIwNzg0YzUzODA3ZjVmZTc2Yjg4ZjZkNjdlMmExZTIxODlhZTEiLCJjbGllbnRfaWQiOiJUZXN0IENsaWVudCBJRCIsInVzZXJfaWQiOm51bGwsImV4cGlyZXMiOjEzODAwNDQ1NDIsInRva2VuX3R5cGUiOiJiZWFyZXIiLCJzY29wZSI6bnVsbH0.PcC4k8Q_etpU-J4yGFEuBUdeyMJhtpZFkVQ__sXpe78eSi7xTniqOOtgfWa62Y4sj5Npta8xPuDglH8Fueh_APZX4wGCiRE1P4nT4APQCOTbgcuCNXwjmP8znk9F76ID2WxThaMbmpsTTEkuyyUYQKCCdxlIcSbVvcLZUGKZ6-g");
        Optional<JWK> actualJWK = publicKeyExtractor.extractPublicKey(inputTokenWithoutPK);
        assertFalse("Extract returns no value if there is no such claim in the token", actualJWK.isPresent());
    }
}
