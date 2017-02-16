package de.adorsys.cse.jwt;

import de.adorsys.cse.client.oauth.AccessTokenExtractor;
import de.adorsys.cse.client.oauth.AccessTokenExtractorImpl;
import de.adorsys.cse.crypt.SecretCredentialEncryptor;
import de.adorsys.cse.nonce.NonceGenerator;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;
import java.util.Random;

import static de.adorsys.cse.Base64StringGenerator.generateRandomBase64String;
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
        final JWT accessToken = new JWTNimbusImpl(expectedBase64EncodedAccessToken);

        JWTBuilderNimbusImpl actualBuilder = (JWTBuilderNimbusImpl) jwtBuilder.withAccessToken(accessToken);

        assertEquals("stores access token for a future use in buildAndSign", accessToken, actualBuilder.getAccessToken());
    }

    @Test(expected = IllegalArgumentException.class)
    public void withNonceGenerator_passingNullCausesIllegalArgumentException() {
        jwtBuilder.withNonceGenerator(null);
        fail("calling withNonceGenerator(null) causes IllegalArgumentException");
    }

    @Test
    public void withNonceGenerator() {
        NonceGenerator someNonceGenerator = () -> null;
        JWTBuilderNimbusImpl actualBuilder = (JWTBuilderNimbusImpl) jwtBuilder.withNonceGenerator(someNonceGenerator);
        assertEquals("Stores nonceGenerator for a future use in buildAndSign", someNonceGenerator, actualBuilder.getNonceGenerator());
    }

    @Test
    public void withExpirationTimeInMs() {
        final long someExpirationTimeInMs = new Random(System.currentTimeMillis()).nextLong();

        JWTBuilderNimbusImpl actualBuilder = (JWTBuilderNimbusImpl) jwtBuilder.withExpirationTimeInMs(someExpirationTimeInMs);

        assertEquals("Stores expiration time for a future use in buildAndSign", someExpirationTimeInMs, actualBuilder.getExpirationTimeInMs());
    }

    @Test(expected = IllegalArgumentException.class)
    public void withEncryptedServerPublicKey_passingFirstNullCausesIllegalArgumentException() throws Exception {
        String someHmacSecret = "eyJwIjoiODF5OENuSTJ4WmtFb3R3MmI1YWYxT3dKRGl2MDlqVlBjQ1RSUGwwSkl4OWhLTFlsSkNnRjRxWU45MnZGN3l3WnNtSWVXUnFnT05udG11dUNzSW9YWm05d0huN0Vqb1hkTmRJZmZockU2b05rOVVzZEhUdmVhWmN0MGNhYy1VQ2Q4NmVCQUNLZGlwVVp4ZkVVb3VIS0dmdlRPOFB5cXBDSlJINzlfOHh5allrIiwia3R5IjoiUlNBIiwicSI6ImwxblBUWldjUW4wUGRHV3JlWm5HYnROTjdpSTFRc0NTZEF2ejd6WjZsZV9mbUN4bUdNbW1pTjBpemRzbjZFMXFXVXl4YzdQN2RaWGxXNEtfTU5jRHdNallfeXVFdVp4dGFVTzhqLXduVjVPX0FsMHI0VFhLc2IwcjZaMkxUbTdEREhhRElFX0p6SVNZcmZzNlNUYTdLS1l3QWpnNThPOV9FTjNoVm5rNGdscyIsImQiOiJFbGNjZmphRmdhMzhxbW1YNmIzRW44cGhfVzRvSmtCMWh3MW4tQkdZXzNhNVZ5eXpsY0dvcGI4RWxJczlZWV9HSXZKS0FWTUFLam5PeTVCSTdqVXZXS1FldXlLWVcxVkg3b1FjV3d4aGZ5Mlp2QUhHWU4yQmFIcFBHRUcwd2ZqSC1zbjFJZXk4S0ZxUW9uQ0dEY19sa25qal9qYXkzZGVoNnNzQkJBZkFYX2xKSllPU3JTQUZlZlR3bl9odXhoTlNYV0NvSkxFdFhTOENPQWtEcHdrM1VxaERfODRtOU9jQkV3eGRPX0hiUHd0b25FTmdrSjJ6Nlc1b0wxWGQtaFA0Z1drdVBhd2xUOUQwbmRjMW5xU2NtelJwa3FwV1A3eWh4REo1cTJNTndLQlNka3FzZWNaSDh1Z18xa0g0cndKYVV4czRhaHpRTkUtbU5WZE5sMlhGb1EiLCJlIjoiQVFBQiIsImtpZCI6ImZiYTg1YWU0LTEwZGUtNGFkYi1hZDFjLTFlODQ1YjBiMGFlNSIsInFpIjoibDVTN09Hc0t1Y1VVWl83S1NkUDFiYWhfNS0tWlJKOE5rUlUwc25WdmVIWVVtTHZ6SXB1eFcweHpWUTRDNE9wTFRuQ2JSTk5XeXdsRUNsWXN0N2lnN3QyNjRMNmhZczB4N2x1Y2lWbGozbXU3Tll0Q3UzY2wzYmptUEJJNW00SF9abDVVbkV6NFZxN05zX0ZHelBBaHdnYWpybXZsVXNqMmNGa2czTXJOTFY0IiwiZHAiOiJ0UjUwV0hYdWE1dFpodmZMajdzVFV6MGo5Q2NrMWNTUlRZNHBLSGZIWUJ0dmpRUWFtbEVyS0NhYnlHT3VXVFdDSG1fRjFGenJsOFFoZ1NYOEN2V0xPdEpfS0VNQ2VHd0tXWTc3bnplX0RDWWtFaWNJRVFUSW45OUMyNWdzcFpqQXRXRVpzZ2NSSnQyVzQtbHJpVm52dHV6anBkeE1rMEtrMXBTSTUwSzZrcmsiLCJkcSI6Ik1lOEplX2xlUW5sc1BlVEZQQ3RGMG82WWFYYVR4LUFzOFdoNEpIWC0zN1R5T2d4NzZyV3M0ZjdEV3ROeFNTMHhaeURzY3RYd29veV96UDlJQU44UGQtMUwyblFMS0FtNTl6N0gyVnY2WnVSeDRsX0ctRmg4OVVNS1Y5c0llT29HSV9oOXJvMWtjTHRXZkNBa3pMN241TE5mcDN2UmNmSXlJLWh3VGMxVW5YMCIsIm4iOiJqLUVUT1VZcWlrQU9lZWpuaHBRazhYRy1hb1htd3dsLUxWSUkzdVFsaW5EcXlvN2l0LWJ5RWNObGF4Q0xvMzJSNHpVcWRmTHVGSWZZY3pGNWZPaVRCa3ZhOVk5MXE3Zkh1engwNldoRWdhaGkySFJHY0lWdXI0VXFiYnJsOFZhSmhHRlNySVdNa1JkQ0tSazBXYW13Qjh3NTFCUGo4bGthLXRHWlg5RVEySWF4bUJzeFVXUXVGWW9tUFZGNlZhcGVmWXc2Nl9YeUQ3SXc1TS1XWVZpbF9zRDg4d2oyam5KdzJKdlpjMFlTVnJscXV3SDFnSWtRNW0wWEZ0eTc3a0F3Wk9URzJLSEFJbnZKcE1jam1JemUtMmlDNEIyaFNkbXhZMF9yX1FnamctMlJOR3ZKektTTy1GekxoT2pZWkI3VDNtMi0tZEpYQTd4cDNRSzZqNF9oc3cifQ";
        jwtBuilder.withEncryptedHMacSecretKey(null, someHmacSecret);
        fail("calling withEncryptedHMacSecretKey(null, AnyObject) causes IllegalArgumentException");
    }

    @Test(expected = IllegalArgumentException.class)
    public void withEncryptedServerPublicKey_passingSecondNullCausesIllegalArgumentException() {
        SecretCredentialEncryptor someSecretCredentialEncryptor = secret -> null;
        jwtBuilder.withEncryptedHMacSecretKey(someSecretCredentialEncryptor, null);
        fail("calling withEncryptedHMacSecretKey(AnyObject, null) causes IllegalArgumentException");
    }

    @Test(expected = IllegalArgumentException.class)
    public void withEncryptedServerPublicKey_passingTwoNullsCausesIllegalArgumentException() {
        jwtBuilder.withEncryptedHMacSecretKey(null, null);
        fail("calling withEncryptedHMacSecretKey(null, null) causes IllegalArgumentException");
    }

    @Test
    public void withEncryptedServerPublicKey() throws Exception {
        SecretCredentialEncryptor someSecretCredentialEncryptor = (secret -> secret + "whatever");
        String someHmacSecret = "eyJwIjoiODF5OENuSTJ4WmtFb3R3MmI1YWYxT3dKRGl2MDlqVlBjQ1RSUGwwSkl4OWhLTFlsSkNnRjRxWU45MnZGN3l3WnNtSWVXUnFnT05udG11dUNzSW9YWm05d0huN0Vqb1hkTmRJZmZockU2b05rOVVzZEhUdmVhWmN0MGNhYy1VQ2Q4NmVCQUNLZGlwVVp4ZkVVb3VIS0dmdlRPOFB5cXBDSlJINzlfOHh5allrIiwia3R5IjoiUlNBIiwicSI6ImwxblBUWldjUW4wUGRHV3JlWm5HYnROTjdpSTFRc0NTZEF2ejd6WjZsZV9mbUN4bUdNbW1pTjBpemRzbjZFMXFXVXl4YzdQN2RaWGxXNEtfTU5jRHdNallfeXVFdVp4dGFVTzhqLXduVjVPX0FsMHI0VFhLc2IwcjZaMkxUbTdEREhhRElFX0p6SVNZcmZzNlNUYTdLS1l3QWpnNThPOV9FTjNoVm5rNGdscyIsImQiOiJFbGNjZmphRmdhMzhxbW1YNmIzRW44cGhfVzRvSmtCMWh3MW4tQkdZXzNhNVZ5eXpsY0dvcGI4RWxJczlZWV9HSXZKS0FWTUFLam5PeTVCSTdqVXZXS1FldXlLWVcxVkg3b1FjV3d4aGZ5Mlp2QUhHWU4yQmFIcFBHRUcwd2ZqSC1zbjFJZXk4S0ZxUW9uQ0dEY19sa25qal9qYXkzZGVoNnNzQkJBZkFYX2xKSllPU3JTQUZlZlR3bl9odXhoTlNYV0NvSkxFdFhTOENPQWtEcHdrM1VxaERfODRtOU9jQkV3eGRPX0hiUHd0b25FTmdrSjJ6Nlc1b0wxWGQtaFA0Z1drdVBhd2xUOUQwbmRjMW5xU2NtelJwa3FwV1A3eWh4REo1cTJNTndLQlNka3FzZWNaSDh1Z18xa0g0cndKYVV4czRhaHpRTkUtbU5WZE5sMlhGb1EiLCJlIjoiQVFBQiIsImtpZCI6ImZiYTg1YWU0LTEwZGUtNGFkYi1hZDFjLTFlODQ1YjBiMGFlNSIsInFpIjoibDVTN09Hc0t1Y1VVWl83S1NkUDFiYWhfNS0tWlJKOE5rUlUwc25WdmVIWVVtTHZ6SXB1eFcweHpWUTRDNE9wTFRuQ2JSTk5XeXdsRUNsWXN0N2lnN3QyNjRMNmhZczB4N2x1Y2lWbGozbXU3Tll0Q3UzY2wzYmptUEJJNW00SF9abDVVbkV6NFZxN05zX0ZHelBBaHdnYWpybXZsVXNqMmNGa2czTXJOTFY0IiwiZHAiOiJ0UjUwV0hYdWE1dFpodmZMajdzVFV6MGo5Q2NrMWNTUlRZNHBLSGZIWUJ0dmpRUWFtbEVyS0NhYnlHT3VXVFdDSG1fRjFGenJsOFFoZ1NYOEN2V0xPdEpfS0VNQ2VHd0tXWTc3bnplX0RDWWtFaWNJRVFUSW45OUMyNWdzcFpqQXRXRVpzZ2NSSnQyVzQtbHJpVm52dHV6anBkeE1rMEtrMXBTSTUwSzZrcmsiLCJkcSI6Ik1lOEplX2xlUW5sc1BlVEZQQ3RGMG82WWFYYVR4LUFzOFdoNEpIWC0zN1R5T2d4NzZyV3M0ZjdEV3ROeFNTMHhaeURzY3RYd29veV96UDlJQU44UGQtMUwyblFMS0FtNTl6N0gyVnY2WnVSeDRsX0ctRmg4OVVNS1Y5c0llT29HSV9oOXJvMWtjTHRXZkNBa3pMN241TE5mcDN2UmNmSXlJLWh3VGMxVW5YMCIsIm4iOiJqLUVUT1VZcWlrQU9lZWpuaHBRazhYRy1hb1htd3dsLUxWSUkzdVFsaW5EcXlvN2l0LWJ5RWNObGF4Q0xvMzJSNHpVcWRmTHVGSWZZY3pGNWZPaVRCa3ZhOVk5MXE3Zkh1engwNldoRWdhaGkySFJHY0lWdXI0VXFiYnJsOFZhSmhHRlNySVdNa1JkQ0tSazBXYW13Qjh3NTFCUGo4bGthLXRHWlg5RVEySWF4bUJzeFVXUXVGWW9tUFZGNlZhcGVmWXc2Nl9YeUQ3SXc1TS1XWVZpbF9zRDg4d2oyam5KdzJKdlpjMFlTVnJscXV3SDFnSWtRNW0wWEZ0eTc3a0F3Wk9URzJLSEFJbnZKcE1jam1JemUtMmlDNEIyaFNkbXhZMF9yX1FnamctMlJOR3ZKektTTy1GekxoT2pZWkI3VDNtMi0tZEpYQTd4cDNRSzZqNF9oc3cifQ";
        JWTBuilderNimbusImpl actualBuilder = (JWTBuilderNimbusImpl) jwtBuilder.withEncryptedHMacSecretKey(someSecretCredentialEncryptor, someHmacSecret);
        assertEquals("Stores encrypted server public key for a future use in buildAndSign", someHmacSecret + "whatever", actualBuilder.getHmacEncryptedSecret());
    }

    @Test(expected = IllegalArgumentException.class)
    public void build_passingNullCausesIllegalArgumentException() {
        jwtBuilder.buildAndSign(null);
        fail("calling buildAndSign(null) causes IllegalArgumentException");
    }

    @Test(expected = IllegalArgumentException.class)
    public void build_passingEmptyKeyCausesIllegalArgumentException() {
        jwtBuilder.buildAndSign("");
        fail("calling buildAndSign(\"\") causes IllegalArgumentException");
    }

    @Test
    public void build() {
        JWT jwt = jwtBuilder.buildAndSign("any secret key");
        assertNotNull("Builder returns valuable jwt", jwt);
    }

    @Test
    public void build_withAccessToken() throws Exception {
        final String expectedBase64EncodedAccessToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpZCI6IjYzMjIwNzg0YzUzODA3ZjVmZTc2Yjg4ZjZkNjdlMmExZTIxODlhZTEiLCJjbGllbnRfaWQiOiJUZXN0IENsaWVudCBJRCIsInVzZXJfaWQiOm51bGwsImV4cGlyZXMiOjEzODAwNDQ1NDIsInRva2VuX3R5cGUiOiJiZWFyZXIiLCJzY29wZSI6bnVsbH0.PcC4k8Q_etpU-J4yGFEuBUdeyMJhtpZFkVQ__sXpe78eSi7xTniqOOtgfWa62Y4sj5Npta8xPuDglH8Fueh_APZX4wGCiRE1P4nT4APQCOTbgcuCNXwjmP8znk9F76ID2WxThaMbmpsTTEkuyyUYQKCCdxlIcSbVvcLZUGKZ6-g";

        JWT accessToken = new JWTNimbusImpl(expectedBase64EncodedAccessToken);
        JWT resultToken = jwtBuilder.withAccessToken(accessToken).buildAndSign("some hMacKey");

        AccessTokenExtractor accessTokenExtractor = new AccessTokenExtractorImpl();
        Optional<JWT> actualAccessToken = accessTokenExtractor.extractAccessToken(resultToken);
        assertTrue("actualAccessToken present", actualAccessToken.isPresent());
        assertEquals("embedded accessToken corresponds to the expected", expectedBase64EncodedAccessToken, actualAccessToken.get().encode());
    }

    @Test
    public void build_withNonceGenerator() throws Exception {
        NonceGenerator dummyNonceGenerator = () -> generateRandomBase64String(20);

        JWT resultToken = jwtBuilder.withNonceGenerator(dummyNonceGenerator).buildAndSign("some hMacKey");

        assertTrue("token contains nonce", resultToken.getClaim("jti").isPresent());
    }
}
