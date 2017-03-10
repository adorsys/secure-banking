package de.adorsys.cse.jwt;

import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.RSADecrypter;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWTClaimsSet;
import de.adorsys.cse.crypt.JWTEncryptor;
import de.adorsys.cse.jwk.JWK;
import de.adorsys.cse.jwk.JWKNimbusImpl;
import de.adorsys.cse.jwk.JWKPublicKeyBuilderNimbusImpl;
import org.junit.Before;
import org.junit.Test;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Map;

import static org.junit.Assert.*;

public class JWTEncryptorNimbusImplTest {
    private JWTEncryptor jwtEncryptor;

    private KeyPairGenerator keyPairGenerator;
    private KeyPair keyPair;
    private JWK jwkPublicKey;

    public JWTEncryptorNimbusImplTest() throws Exception {
        keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(1024);
    }

    @Before
    public void setUp() {
        keyPair = keyPairGenerator.generateKeyPair();
        jwkPublicKey = new JWKPublicKeyBuilderNimbusImpl().build(keyPair.getPublic());
        jwtEncryptor=  new JWTEncryptorNimbusImpl(jwkPublicKey);
    }


    @Test(expected = IllegalArgumentException.class)
    public void instanceRequresAPublicKey() {
        new JWTEncryptorNimbusImpl(null);
        fail("Creation of JWTEncryptorNimbusImpl with null-key lead to IllegalArgumentException");
    }

    @Test
    public void instanceRequresARsaPublicKey() throws Exception {
        String inputStringWithRSAPublicKey = "{\"p\":\"81y8CnI2xZkEotw2b5af1OwJDiv09jVPcCTRPl0JIx9hKLYlJCgF4qYN92vF7ywZsmIeWRqgONntmuuCsIoXZm9wHn7EjoXdNdIffhrE6oNk9UsdHTveaZct0cac-UCd86eBACKdipUZxfEUouHKGfvTO8PyqpCJRH79_8xyjYk\",\"kty\":\"RSA\",\"q\":\"l1nPTZWcQn0PdGWreZnGbtNN7iI1QsCSdAvz7zZ6le_fmCxmGMmmiN0izdsn6E1qWUyxc7P7dZXlW4K_MNcDwMjY_yuEuZxtaUO8j-wnV5O_Al0r4TXKsb0r6Z2LTm7DDHaDIE_JzISYrfs6STa7KKYwAjg58O9_EN3hVnk4gls\",\"d\":\"ElccfjaFga38qmmX6b3En8ph_W4oJkB1hw1n-BGY_3a5VyyzlcGopb8ElIs9YY_GIvJKAVMAKjnOy5BI7jUvWKQeuyKYW1VH7oQcWwxhfy2ZvAHGYN2BaHpPGEG0wfjH-sn1Iey8KFqQonCGDc_lknjj_jay3deh6ssBBAfAX_lJJYOSrSAFefTwn_huxhNSXWCoJLEtXS8COAkDpwk3UqhD_84m9OcBEwxdO_HbPwtonENgkJ2z6W5oL1Xd-hP4gWkuPawlT9D0ndc1nqScmzRpkqpWP7yhxDJ5q2MNwKBSdkqsecZH8ug_1kH4rwJaUxs4ahzQNE-mNVdNl2XFoQ\",\"e\":\"AQAB\",\"kid\":\"fba85ae4-10de-4adb-ad1c-1e845b0b0ae5\",\"qi\":\"l5S7OGsKucUUZ_7KSdP1bah_5--ZRJ8NkRU0snVveHYUmLvzIpuxW0xzVQ4C4OpLTnCbRNNWywlEClYst7ig7t264L6hYs0x7luciVlj3mu7NYtCu3cl3bjmPBI5m4H_Zl5UnEz4Vq7Ns_FGzPAhwgajrmvlUsj2cFkg3MrNLV4\",\"dp\":\"tR50WHXua5tZhvfLj7sTUz0j9Cck1cSRTY4pKHfHYBtvjQQamlErKCabyGOuWTWCHm_F1Fzrl8QhgSX8CvWLOtJ_KEMCeGwKWY77nze_DCYkEicIEQTIn99C25gspZjAtWEZsgcRJt2W4-lriVnvtuzjpdxMk0Kk1pSI50K6krk\",\"dq\":\"Me8Je_leQnlsPeTFPCtF0o6YaXaTx-As8Wh4JHX-37TyOgx76rWs4f7DWtNxSS0xZyDsctXwooy_zP9IAN8Pd-1L2nQLKAm59z7H2Vv6ZuRx4l_G-Fh89UMKV9sIeOoGI_h9ro1kcLtWfCAkzL7n5LNfp3vRcfIyI-hwTc1UnX0\",\"n\":\"j-ETOUYqikAOeejnhpQk8XG-aoXmwwl-LVII3uQlinDqyo7it-byEcNlaxCLo32R4zUqdfLuFIfYczF5fOiTBkva9Y91q7fHuzx06WhEgahi2HRGcIVur4Uqbbrl8VaJhGFSrIWMkRdCKRk0WamwB8w51BPj8lka-tGZX9EQ2IaxmBsxUWQuFYomPVF6VapefYw66_XyD7Iw5M-WYVil_sD88wj2jnJw2JvZc0YSVrlquwH1gIkQ5m0XFty77kAwZOTG2KHAInvJpMcjmIze-2iC4B2hSdmxY0_r_Qgjg-2RNGvJzKSO-FzLhOjYZB7T3m2--dJXA7xp3QK6j4_hsw\"}";
        JWK someRsaPublicKey = new JWKNimbusImpl(inputStringWithRSAPublicKey);
        new JWTEncryptorNimbusImpl(someRsaPublicKey);
    }

    @Test
    public void encrypt() throws Exception {
        JWT someJWT = new JWTBuilderNimbusImpl().withPayload("Some Payload").build();
        JWE jwe = jwtEncryptor.encrypt(someJWT);
        assertNotNull("JWE object is not null", jwe);

        JWT decryptedJWE = decryptInternally(jwe.encode());

        assertEquals("Base64 encoded values are same", someJWT.encode(), decryptedJWE.encode());

        Map<String, Object> decryptedPayloadClaims = decryptedJWE.getPayloadClaims();
        assertEquals("Payload claim returned back after decryption", "Some Payload", decryptedPayloadClaims.get("0"));
    }

    @Test
    public void encryptSignedJWT() throws Exception {
        JWS someSignedJWT = new JWTBuilderNimbusImpl().withPayload("Some Payload").buildAndSign("hmacSecret");

        JWE jwe = jwtEncryptor.encrypt(someSignedJWT);
        assertNotNull("JWE object is not null", jwe);

        JWS decryptedJWE = decryptSignedInternally(jwe.encode());

        assertEquals("Base64 encoded values are same", someSignedJWT.encode(), decryptedJWE.encode());

        Map<String, Object> decryptedPayloadClaims = decryptedJWE.getPayloadClaims();
        assertEquals("Payload claim returned back after decryption", "Some Payload", decryptedPayloadClaims.get("0"));
    }

    private JWS decryptSignedInternally(String jwe) throws Exception {
        EncryptedJWT jwt = EncryptedJWT.parse(jwe);

        // Create a decrypter with the specified private RSA key
        RSADecrypter decrypter = new RSADecrypter(keyPair.getPrivate());

        // Decrypt
        jwt.decrypt(decrypter);
        Payload payload = jwt.getPayload();

        String serialize = payload.toSignedJWT().serialize();
        return new JWSNimbusImpl(serialize);
    }

    private JWT decryptInternally(String jwe) throws Exception {
        EncryptedJWT jwt = EncryptedJWT.parse(jwe);

        // Create a decrypter with the specified private RSA key
        RSADecrypter decrypter = new RSADecrypter(keyPair.getPrivate());

        // Decrypt
        jwt.decrypt(decrypter);
        Payload payload = jwt.getPayload();
        JWTClaimsSet claimsSet = JWTClaimsSet.parse(payload.toJSONObject());
        return new JWTNimbusImpl(claimsSet);
    }
}
