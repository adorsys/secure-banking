package de.adorsys.cse.crypt;

import de.adorsys.cse.jwk.JWK;
import de.adorsys.cse.jwk.JWKNimbusImpl;
import de.adorsys.cse.jwk.JWKPublicKeyBuilder;
import de.adorsys.cse.jwk.JWKPublicKeyBuilderNimbusImpl;
import org.apache.commons.codec.binary.Base64;
import org.junit.Ignore;
import org.junit.Test;

import javax.crypto.Cipher;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;

import static de.adorsys.cse.Base64StringGenerator.generateRandomBase64String;
import static org.junit.Assert.*;

public class SecretCredentialEncryptorImplTest {

    private static final JWKPublicKeyBuilder JWK_BUILDER = new JWKPublicKeyBuilderNimbusImpl();

    @Test(expected = IllegalArgumentException.class)
    public void createWithNullThrowsIllegalArgumentException() {
        new SecretCredentialEncryptorImpl(null);
        fail("new SecretCredentialEncryptorImpl(null) throws IllegalArgumentException");
    }

    @Test(expected = IllegalArgumentException.class)
    @Ignore("Current implementation of JWK supports only RSA keys")
    public void createWithNonRSAKeyThrowsIllegalArgumentException() throws Exception {
        JWK nonRSAJWK = new JWKNimbusImpl("{\"p\":\"81y8CnI2xZkEotw2b5af1OwJDiv09jVPcCTRPl0JIx9hKLYlJCgF4qYN92vF7ywZsmIeWRqgONntmuuCsIoXZm9wHn7EjoXdNdIffhrE6oNk9UsdHTveaZct0cac-UCd86eBACKdipUZxfEUouHKGfvTO8PyqpCJRH79_8xyjYk\",\"kty\":\"DSA\",\"q\":\"l1nPTZWcQn0PdGWreZnGbtNN7iI1QsCSdAvz7zZ6le_fmCxmGMmmiN0izdsn6E1qWUyxc7P7dZXlW4K_MNcDwMjY_yuEuZxtaUO8j-wnV5O_Al0r4TXKsb0r6Z2LTm7DDHaDIE_JzISYrfs6STa7KKYwAjg58O9_EN3hVnk4gls\",\"d\":\"ElccfjaFga38qmmX6b3En8ph_W4oJkB1hw1n-BGY_3a5VyyzlcGopb8ElIs9YY_GIvJKAVMAKjnOy5BI7jUvWKQeuyKYW1VH7oQcWwxhfy2ZvAHGYN2BaHpPGEG0wfjH-sn1Iey8KFqQonCGDc_lknjj_jay3deh6ssBBAfAX_lJJYOSrSAFefTwn_huxhNSXWCoJLEtXS8COAkDpwk3UqhD_84m9OcBEwxdO_HbPwtonENgkJ2z6W5oL1Xd-hP4gWkuPawlT9D0ndc1nqScmzRpkqpWP7yhxDJ5q2MNwKBSdkqsecZH8ug_1kH4rwJaUxs4ahzQNE-mNVdNl2XFoQ\",\"e\":\"AQAB\",\"kid\":\"fba85ae4-10de-4adb-ad1c-1e845b0b0ae5\",\"qi\":\"l5S7OGsKucUUZ_7KSdP1bah_5--ZRJ8NkRU0snVveHYUmLvzIpuxW0xzVQ4C4OpLTnCbRNNWywlEClYst7ig7t264L6hYs0x7luciVlj3mu7NYtCu3cl3bjmPBI5m4H_Zl5UnEz4Vq7Ns_FGzPAhwgajrmvlUsj2cFkg3MrNLV4\",\"dp\":\"tR50WHXua5tZhvfLj7sTUz0j9Cck1cSRTY4pKHfHYBtvjQQamlErKCabyGOuWTWCHm_F1Fzrl8QhgSX8CvWLOtJ_KEMCeGwKWY77nze_DCYkEicIEQTIn99C25gspZjAtWEZsgcRJt2W4-lriVnvtuzjpdxMk0Kk1pSI50K6krk\",\"dq\":\"Me8Je_leQnlsPeTFPCtF0o6YaXaTx-As8Wh4JHX-37TyOgx76rWs4f7DWtNxSS0xZyDsctXwooy_zP9IAN8Pd-1L2nQLKAm59z7H2Vv6ZuRx4l_G-Fh89UMKV9sIeOoGI_h9ro1kcLtWfCAkzL7n5LNfp3vRcfIyI-hwTc1UnX0\",\"n\":\"j-ETOUYqikAOeejnhpQk8XG-aoXmwwl-LVII3uQlinDqyo7it-byEcNlaxCLo32R4zUqdfLuFIfYczF5fOiTBkva9Y91q7fHuzx06WhEgahi2HRGcIVur4Uqbbrl8VaJhGFSrIWMkRdCKRk0WamwB8w51BPj8lka-tGZX9EQ2IaxmBsxUWQuFYomPVF6VapefYw66_XyD7Iw5M-WYVil_sD88wj2jnJw2JvZc0YSVrlquwH1gIkQ5m0XFty77kAwZOTG2KHAInvJpMcjmIze-2iC4B2hSdmxY0_r_Qgjg-2RNGvJzKSO-FzLhOjYZB7T3m2--dJXA7xp3QK6j4_hsw\"}");

        new SecretCredentialEncryptorImpl(nonRSAJWK);
        fail("creation of SecretCredentialEncryptorImpl() with non-RSA JWK throws IllegalArgumentException");
    }

    @Test(expected = IllegalArgumentException.class)
    public void encryptWithNullThrowsIllegalArgumentException() throws Exception {
        KeyPair keyPair = generateRSA2048KeyPair();
        JWK publicKey = convertPublicKeyToJWK(keyPair);

        SecretCredentialEncryptor secretCredentialEncryptor = new SecretCredentialEncryptorImpl(publicKey);
        secretCredentialEncryptor.encrypt(null);
        fail("calling encrypt(null) throws IllegalArgumentException");
    }

    @Test(expected = IllegalArgumentException.class)
    public void encryptWithEmptyStringThrowsIllegalArgumentException() throws Exception {
        KeyPair keyPair = generateRSA2048KeyPair();
        JWK publicKey = convertPublicKeyToJWK(keyPair);

        SecretCredentialEncryptor secretCredentialEncryptor = new SecretCredentialEncryptorImpl(publicKey);
        secretCredentialEncryptor.encrypt("");
        fail("calling encrypt(\"\") throws IllegalArgumentException");
    }

    @Test
    @Ignore("not implemented")
    public void encryptUsingRSA2048ToEncryptSecret() throws Exception {
        final String secret = generateRandomBase64String(1242);

        KeyPair keyPair = generateRSA2048KeyPair();
        JWK publicKey = convertPublicKeyToJWK(keyPair);

        SecretCredentialEncryptor secretCredentialEncryptor = new SecretCredentialEncryptorImpl(publicKey);
        String encryptedSecret = secretCredentialEncryptor.encrypt(secret);

        assertNotNull("encrypted secret is returned", encryptedSecret);

        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        String actualDecryptedSecret = new String(cipher.doFinal(Base64.decodeBase64(encryptedSecret)), "UTF-8");
        assertEquals("Encrypted secret decripts with private key sucessfully", secret, actualDecryptedSecret);
    }


    private static JWK convertPublicKeyToJWK(KeyPair keyPair) {
        return JWK_BUILDER.build(keyPair.getPublic());
    }

    private KeyPair generateRSA2048KeyPair() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);

        return keyPairGenerator.generateKeyPair();
    }
}
