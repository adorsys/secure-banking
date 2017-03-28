package de.adorsys.cse.jwt;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSAEncrypter;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import de.adorsys.cse.crypt.JWTDecryptor;
import de.adorsys.cse.crypt.JWTEncryptionException;
import org.bouncycastle.jcajce.provider.asymmetric.RSA;
import org.junit.Before;
import org.junit.Test;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Map;

import static org.junit.Assert.*;

public class JWTDecryptorNimbusImplTest {

    private KeyPairGenerator keyPairGenerator;
    private KeyPair keyPair;

    public JWTDecryptorNimbusImplTest() throws Exception {
        keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(1024);
    }

    @Before
    public void setUp() {
        keyPair = keyPairGenerator.generateKeyPair();
    }

    @Test(expected = IllegalArgumentException.class)
    public void createWithoutPKLeadsToException() throws Exception {
        new JWTDecryptorNimbusImpl(null);
        fail("Creation with null-private key leads to exception");
    }

    @Test(expected = IllegalArgumentException.class)
    public void createWithoutPKOrInvalidPKLeadsToException() throws Exception {
        PrivateKey someNonRSAPrivateKey = new PrivateKey() {
            @Override
            public String getAlgorithm() {
                return "Something not RSA";
            }

            @Override
            public String getFormat() {
                return null;
            }

            @Override
            public byte[] getEncoded() {
                return new byte[0];
            }
        };
        new JWTDecryptorNimbusImpl(someNonRSAPrivateKey);
        fail("Creation with non-RSA private key leads to exception");
    }

    @Test
    public void instanceRequresAnRSAPrivateKey() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(1024);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        new JWTDecryptorNimbusImpl(keyPair.getPrivate());
    }


    @Test(expected = IllegalArgumentException.class)
    public void decryptUnsignedWithNullJWEThrowsException() throws Exception {
        JWTDecryptor jwtDecryptor = new JWTDecryptorNimbusImpl(keyPair.getPrivate());

        JWT decryptedJWT = jwtDecryptor.decryptUnsigned((JWE) null);
        fail("Call decryptUnsigned with null jwe leads to exception");
    }

    @Test(expected = IllegalArgumentException.class)
    public void decryptSignedWithNullJWEThrowsException() throws Exception {
        JWTDecryptor jwtDecryptor = new JWTDecryptorNimbusImpl(keyPair.getPrivate());

        JWT decryptedJWT = jwtDecryptor.decryptSigned((JWE) null);
        fail("Call decryptSigned with null jwe leads to exception");
    }

    @Test
    public void decryptSignedJWE() throws Exception {
        JWS someJWT = new JWTBuilderNimbusImpl().withPayload("some secret").buildAndSign("hmacSecret");
        JWE encryptedJWT = encryptSignedInternally(someJWT);

        JWTDecryptor jwtDecryptor = new JWTDecryptorNimbusImpl(keyPair.getPrivate());

        JWS decryptedSignedJWS = jwtDecryptor.decryptSigned(encryptedJWT);

        assertNotNull("Returned non-null jws object", decryptedSignedJWS);
        assertEquals("Returned decrypted object equals to original one", someJWT.encode(), decryptedSignedJWS.encode());

        JWTSigner jwtSigner = new JWTSignerNimbusImpl();
        assertTrue("Signature is correct", jwtSigner.verify(decryptedSignedJWS, "hmacSecret"));
    }

    @Test
    public void decryptUnsignedJWE() throws Exception {
        JWT someJWT = new JWTBuilderNimbusImpl().withPayload("some secret").build();
        JWE encryptedJWT = encryptUnsignedInternally(someJWT);

        JWTDecryptor jwtDecryptor = new JWTDecryptorNimbusImpl(keyPair.getPrivate());

        JWT decryptedJWT = jwtDecryptor.decryptUnsigned(encryptedJWT);

        assertNotNull("Returned non-null jwt object", decryptedJWT);
        assertEquals("Returned decrypted object equals to original one", someJWT.encode(), decryptedJWT.encode());
    }

    @Test(expected = IllegalArgumentException.class)
    public void decryptUnsignedWithNullStringThrowsException() throws Exception {
        JWTDecryptor jwtDecryptor = new JWTDecryptorNimbusImpl(keyPair.getPrivate());

        JWT decryptedJWT = jwtDecryptor.decryptUnsigned((String) null);
        fail("Call decryptUnsigned with null String leads to exception");
    }

    @Test(expected = IllegalArgumentException.class)
    public void decryptSignedWithNullStringThrowsException() throws Exception {
        JWTDecryptor jwtDecryptor = new JWTDecryptorNimbusImpl(keyPair.getPrivate());

        JWT decryptedJWT = jwtDecryptor.decryptSigned((String) null);
        fail("Call decryptSigned with null String leads to exception");
    }

    @Test
    public void decryptSignedBase64String() throws Exception {
        JWS someJWT = new JWTBuilderNimbusImpl().withPayload("some secret").buildAndSign("hmacSecret");
        JWE encryptedJWT = encryptSignedInternally(someJWT);
        String base64Encoded = encryptedJWT.encode();

        JWTDecryptor jwtDecryptor = new JWTDecryptorNimbusImpl(keyPair.getPrivate());

        JWS decryptedSignedJWS = jwtDecryptor.decryptSigned(base64Encoded);

        assertNotNull("Returned non-null jws object", decryptedSignedJWS);
        assertEquals("Returned decrypted object equals to original one", someJWT.encode(), decryptedSignedJWS.encode());

        JWTSigner jwtSigner = new JWTSignerNimbusImpl();
        assertTrue("Signature is correct", jwtSigner.verify(decryptedSignedJWS, "hmacSecret"));
    }

    @Test
    public void decryptUnsignedBase64String() throws Exception {
        JWT someJWT = new JWTBuilderNimbusImpl().withPayload("some secret").build();
        JWE encryptedJWT = encryptUnsignedInternally(someJWT);
        String encryptedBase64 = encryptedJWT.encode();

        JWTDecryptor jwtDecryptor = new JWTDecryptorNimbusImpl(keyPair.getPrivate());

        JWT decryptedJWT = jwtDecryptor.decryptUnsigned(encryptedBase64);

        assertNotNull("Returned non-null jwt object", decryptedJWT);
        assertEquals("Returned decrypted object equals to original one", someJWT.encode(), decryptedJWT.encode());
    }

    private JWE encryptUnsignedInternally(JWT jwt) throws Exception {
        RSAEncrypter encrypter = new RSAEncrypter((RSAPublicKey) keyPair.getPublic());

        JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder();

        Map<String, Object> allClaims = jwt.getAllClaims();
        allClaims.forEach(builder::claim);

        JWTClaimsSet claimsSet = builder.build();

        JWEObject jweObject = new JWEObject(
                new JWEHeader.Builder(JWEAlgorithm.RSA_OAEP, EncryptionMethod.A128GCM)
                        .contentType("JWT") // required to signal nested JWT
                        .build(),
                new Payload(claimsSet.toJSONObject()));

        jweObject.encrypt(encrypter);
        return new JWENimbusImpl(jweObject);
    }


    private JWE encryptSignedInternally(JWS jws) throws Exception {
        RSAEncrypter encrypter = new RSAEncrypter((RSAPublicKey) keyPair.getPublic());

        SignedJWT signedJWT = SignedJWT.parse(jws.encode());

        JWEObject jweObject = new JWEObject(
                new JWEHeader.Builder(JWEAlgorithm.RSA_OAEP, EncryptionMethod.A128GCM)
                        .contentType("JWT") // required to signal nested JWT
                        .build(),
                new Payload(signedJWT));

        jweObject.encrypt(encrypter);
        return new JWENimbusImpl(jweObject);

    }

}
