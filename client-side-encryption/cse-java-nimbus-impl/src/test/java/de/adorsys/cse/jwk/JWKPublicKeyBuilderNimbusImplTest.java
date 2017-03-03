package de.adorsys.cse.jwk;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.Base64;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import static org.junit.Assert.*;

public class JWKPublicKeyBuilderNimbusImplTest {

    private JWKPublicKeyBuilder jwkPublicKeyBuilder;

    @Before
    public void setUp() {
        jwkPublicKeyBuilder = new JWKPublicKeyBuilderNimbusImpl();
    }

    @Test(expected = IllegalArgumentException.class)
    public void passingNullStringCausesIllegalArgumentException() throws Exception {
        jwkPublicKeyBuilder.build((String) null);
        fail("Passing null-String causes IllegalArgumentException");
    }

    @Test(expected = IllegalArgumentException.class)
    public void passingNullKeyCausesIllegalArgumentException() throws Exception {
        jwkPublicKeyBuilder.build((PublicKey) null);
        fail("Passing null-key causes IllegalArgumentException");
    }

    @Test(expected = IllegalArgumentException.class)
    public void passingInvalidKeyCausesIllegalArgumentException() throws Exception {
        jwkPublicKeyBuilder.build("bla-bla-bla");
        fail("Passing invalid key-string causes IllegalArgumentException");
    }

    @Test
    public void buildWithRSAEncodedPEMString() throws Exception {

        String pem = //"-----BEGIN PUBLIC KEY-----" +
                "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAxLWNUWPY9lZbUz6BnemY" +
                        "zuri0tXkhAT1CdlDc2h7SY8mJRY7WiEL+oWfR66AiRtb/mZ/+DK8/AZeWZC4mcZn" +
                        "9YbzwbnqXNxMzsrcJHPJS2vDyxOp11yo+sljsXdEvesDQDELetGkIcXfgLcSGVxD" +
                        "CjsmZX/qwM0J0JjkupVKmYdvERQXetQ12YUFvUr7HnMaT8OqM6/iD9SJtrlf2Hb/" +
                        "bnJdo2PCzQVP8wcalSk2BK9tzxA537Cmxam208ahFO37IPMSF06dh6ygGFFVA/fU" +
                        "O04aBKK5cd0w1JevhfYNMNYr5K4PUEldxyfiRVJu3sAXWP62eYDPUl7JNETJGZqT" +
                        "8wIDAQAB"
                //+ "-----END PUBLIC KEY-----"
                ;

        JWK jwk = jwkPublicKeyBuilder.build(pem);
        assertNotNull("Returned key is provided", jwk);
        assertNotNull("Returned key is provided", jwk.toJSONString());
        assertTrue("Returned key is not empty", isJSONValid(jwk.toJSONString()));
    }


    @Test
    public void buildWithSurroundedKeyString() throws Exception {

        String pem =
                "-----BEGIN ENCRYPTED PRIVATE KEY-----\n" +
                        "MIIFDjBABgkqhkiG9w0BBQ0wMzAbBgkqhkiG9w0BBQwwDg\n" +
                        "MBQGCCqGSIb3DQMHBAgD1kGN4ZslJgSCBMi1xk9jhlPxPc\n" +
                        "9g73NQbtqZwI+9X5OhpSg/2ALxlCCjbqvzgSu8gfFZ4yo+\n" +
                        "A .... MANY LINES LIKE THAT ....\n" +
                        "X0R+meOaudPTBxoSgCCM51poFgaqt4l6VlTN4FRpj+c/Wc\n" +
                        "blK948UAda/bWVmZjXfY4Tztah0CuqlAldOQBzu8TwE7WD\n" +
                        "H0ga/iLNvWYexG7FHLRiq5hTj0g9mUPEbeTXuPtOkTEb/0\n" +
                        "GEs=\n" +
                        "-----END ENCRYPTED PRIVATE KEY-----\n"
                        +
                        "-----BEGIN PUBLIC KEY-----\n" +
                        "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAxLWNUWPY9lZbUz6BnemY\n" +
                        "zuri0tXkhAT1CdlDc2h7SY8mJRY7WiEL+oWfR66AiRtb/mZ/+DK8/AZeWZC4mcZn\n" +
                        "9YbzwbnqXNxMzsrcJHPJS2vDyxOp11yo+sljsXdEvesDQDELetGkIcXfgLcSGVxD\n" +
                        "CjsmZX/qwM0J0JjkupVKmYdvERQXetQ12YUFvUr7HnMaT8OqM6/iD9SJtrlf2Hb/\n" +
                        "bnJdo2PCzQVP8wcalSk2BK9tzxA537Cmxam208ahFO37IPMSF06dh6ygGFFVA/fU\n" +
                        "O04aBKK5cd0w1JevhfYNMNYr5K4PUEldxyfiRVJu3sAXWP62eYDPUl7JNETJGZqT\n" +
                        "8wIDAQAB\n" +
                        "-----END PUBLIC KEY-----\n"
                        +
                        "-----BEGIN CERTIFICATE-----\n" +
                        "MIIDXTCCAkWgAwIBAgIJAJC1HiIAZAiIMA0GCSqGSIb3Df\n" +
                        "BAYTAkFVMRMwEQYDVQQIDApTb21lLVN0YXRlMSEwHwYDVx\n" +
                        "aWRnaXRzIFB0eSBMdGQwHhcNMTExMjMxMDg1OTQ0WhcNMT\n" +
                        "A .... MANY LINES LIKE THAT ....\n" +
                        "JjyzfN746vaInA1KxYEeI1Rx5KXY8zIdj6a7hhphpj2E04\n" +
                        "C3Fayua4DRHyZOLmlvQ6tIChY0ClXXuefbmVSDeUHwc8Yu\n" +
                        "B7xxt8BVc69rLeHV15A0qyx77CLSj3tCx2IUXVqRs5mlSb\n" +
                        "vA==\n" +
                        "-----END CERTIFICATE-----";

        JWK jwk = jwkPublicKeyBuilder.build(pem);
        assertNotNull("Returned key is provided", jwk);
        assertNotNull("Returned key is provided", jwk.toJSONString());
        assertTrue("Returned key is not empty", isJSONValid(jwk.toJSONString()));
    }

    @Test
    public void buildWithRandomRSA2048Key() throws Exception {
        KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
        gen.initialize(2048);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        testWithRandomKeys(keyFactory, gen, 50);
    }

    @Test
    public void buildWithRandomRSA1024Key() throws Exception {
        KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
        gen.initialize(1024);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        testWithRandomKeys(keyFactory, gen, 50);
    }

    private void testWithRandomKeys(KeyFactory keyFactory, KeyPairGenerator keyPairGenerator, int repeats) throws Exception {
        for (int i = 0; i < repeats; i++) {
            // Generate the RSA key pair
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            X509EncodedKeySpec spec = keyFactory.getKeySpec(keyPair.getPublic(),
                    X509EncodedKeySpec.class);
            String key = Base64.encodeBase64String(spec.getEncoded());

            JWK jwk = jwkPublicKeyBuilder.build(key);
            assertNotNull("Returned key is provided", jwk);
            assertNotNull("Returned key is provided", jwk.toJSONString());
            assertTrue("Returned key is not empty", isJSONValid(jwk.toJSONString()));
        }

    }

    private boolean isJSONValid(String jsonInString) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            mapper.readTree(jsonInString);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
