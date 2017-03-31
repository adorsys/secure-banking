package de.adorsys.cse.jwt;

import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.SignedJWT;
import de.adorsys.cse.client.oauth.AccessTokenExtractor;
import de.adorsys.cse.client.oauth.AccessTokenExtractorImpl;
import de.adorsys.cse.crypt.SecretCredentialEncryptor;
import de.adorsys.cse.nonce.NonceGenerator;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import static de.adorsys.cse.Base64StringGenerator.generateRandomBase64String;
import static java.time.temporal.ChronoUnit.MILLIS;
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

    @Test
    public void withPayloadWithoutClaimNameStoresAnIndexedClaim() throws Exception {
        String secret = generateRandomBase64String(20);
        JWTBuilderNimbusImpl actualBuilder = (JWTBuilderNimbusImpl) jwtBuilder.withPayload(secret);
        assertEquals("stored in payload claims", 1, actualBuilder.getPayloadClaims().size());
        assertTrue("key is an index", actualBuilder.getPayloadClaims().containsKey("0"));
        assertEquals("Stores a secret as JSON object", secret, actualBuilder.getPayloadClaims().get("0"));

        String secret2 = generateRandomBase64String(20);
        actualBuilder = (JWTBuilderNimbusImpl) actualBuilder.withPayload(secret2);
        assertEquals("stored in payload claims", 2, actualBuilder.getPayloadClaims().size());
        assertTrue("key is an index", actualBuilder.getPayloadClaims().containsKey("0"));
        assertTrue("key is an index", actualBuilder.getPayloadClaims().containsKey("1"));
        assertEquals("Stores a secret as JSON object", secret, actualBuilder.getPayloadClaims().get("0"));
        assertEquals("Stores a secret as JSON object", secret2, actualBuilder.getPayloadClaims().get("1"));

    }

    @Test
    public void withPayloadWithClaimNameStoresAnNamedClaim() throws Exception {
        String secret = generateRandomBase64String(20);
        JWTBuilderNimbusImpl actualBuilder = (JWTBuilderNimbusImpl) jwtBuilder.withPayload("First claim name", secret);
        assertEquals("stored in payload claims", 1, actualBuilder.getPayloadClaims().size());
        assertTrue("key is an claim name", actualBuilder.getPayloadClaims().containsKey("First claim name"));
        assertEquals("Stores a secret as JSON object", secret, actualBuilder.getPayloadClaims().get("First claim name"));

        String secret2 = generateRandomBase64String(20);
        actualBuilder = (JWTBuilderNimbusImpl) actualBuilder.withPayload(secret2);
        assertEquals("stored in payload claims", 2, actualBuilder.getPayloadClaims().size());
        assertTrue("key is an claim name", actualBuilder.getPayloadClaims().containsKey("First claim name"));
        assertTrue("key is an index", actualBuilder.getPayloadClaims().containsKey("1"));
        assertEquals("Stores a secret as JSON object", secret, actualBuilder.getPayloadClaims().get("First claim name"));
        assertEquals("Stores a secret as JSON object", secret2, actualBuilder.getPayloadClaims().get("1"));

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
    public void buildReturnsJWTObject() {
        JWT jwt = jwtBuilder.build();
        assertNotNull("Builder returns valuable jwt", jwt);
        JWT signedJWT = jwtBuilder.buildAndSign("any secret key");
        assertNotNull("Builder returns valuable jwt", signedJWT);
    }

    @Test
    public void build_withAccessToken() throws Exception {
        final String expectedBase64EncodedAccessToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpZCI6IjYzMjIwNzg0YzUzODA3ZjVmZTc2Yjg4ZjZkNjdlMmExZTIxODlhZTEiLCJjbGllbnRfaWQiOiJUZXN0IENsaWVudCBJRCIsInVzZXJfaWQiOm51bGwsImV4cGlyZXMiOjEzODAwNDQ1NDIsInRva2VuX3R5cGUiOiJiZWFyZXIiLCJzY29wZSI6bnVsbH0.PcC4k8Q_etpU-J4yGFEuBUdeyMJhtpZFkVQ__sXpe78eSi7xTniqOOtgfWa62Y4sj5Npta8xPuDglH8Fueh_APZX4wGCiRE1P4nT4APQCOTbgcuCNXwjmP8znk9F76ID2WxThaMbmpsTTEkuyyUYQKCCdxlIcSbVvcLZUGKZ6-g";

        JWT accessToken = new JWTNimbusImpl(expectedBase64EncodedAccessToken);
        JWT resultToken = jwtBuilder.withAccessToken(accessToken).build();

        AccessTokenExtractor accessTokenExtractor = new AccessTokenExtractorImpl();
        Optional<JWT> actualAccessToken = accessTokenExtractor.extractAccessToken(resultToken);
        assertTrue("actualAccessToken present", actualAccessToken.isPresent());
        assertEquals("embedded accessToken corresponds to the expected", expectedBase64EncodedAccessToken, actualAccessToken.get().encode());
    }

    @Test
    public void build_withNonceGenerator() throws Exception {
        NonceGenerator dummyNonceGenerator = () -> generateRandomBase64String(20);

        JWT resultToken = jwtBuilder.withNonceGenerator(dummyNonceGenerator).build();

        assertTrue("token contains nonce", resultToken.getClaim("jti").isPresent());
        assertEquals("token contains nonce with specified length", 20, resultToken.getClaim("jti").get().length());
    }

    @Test
    public void build_withExpirationTime() {
        final Random random = new Random(Instant.now().toEpochMilli());

        //Since internally object is several times converted between various data types,
        //i.e. Instant -> Date -> long -> String -> long -> Date -> Instant
        //some milliseconds difference occur. This is not relevant for productive use of expiration time use-case
        //To handle with this we use this delta in our comparisons
        final long DELTA_MS = 10;

        for (int i = 0; i < 1000; i++) {
            long randomExpirationTime = Math.abs(random.nextLong());

            JWTBuilder builder = new JWTBuilderNimbusImpl().withExpirationTimeInMs(randomExpirationTime);

            Instant startTime = Instant.now();
            JWTNimbusImpl resultToken = (JWTNimbusImpl) builder.build();
            Instant finishTime = Instant.now();

            Instant expectedExpirationTime = startTime.plus(randomExpirationTime, MILLIS);

            assertTrue("start time claim is present", resultToken.getTokenIssueTime().isPresent());
            assertTrue("expiration time claim is present", resultToken.getTokenExpirationTime().isPresent());

            Instant actualIssueTime = resultToken.getTokenIssueTime().get();
            assertTrue("token start time is between start and finish time", actualIssueTime.isAfter(startTime.minus(DELTA_MS, MILLIS)) && actualIssueTime.isBefore(finishTime.plus(DELTA_MS, MILLIS)));

            Instant actualExpirationTime = resultToken.getTokenExpirationTime().get();

            long actualDeltaMs = Math.abs(actualExpirationTime.toEpochMilli() - expectedExpirationTime.toEpochMilli());

            assertTrue("expiration time is set as provided", actualDeltaMs < DELTA_MS);
        }

    }

    @Test
    public void providingNotLongEnoughSecretLeadToExtendingItToMinimalLength() throws Exception {
        String notLongEnoughString = generateRandomBase64String(22);
        //we need a string with length of 32 chars, so we add 10 chars
        String expectedSecret = notLongEnoughString;
        while (expectedSecret.length() < 32) {
            expectedSecret = expectedSecret.concat("\0");
        }

        JWT actualJWT = jwtBuilder.buildAndSign(notLongEnoughString);

        assertEquals("jwt is signed", 3, actualJWT.encode().split("\\.").length);
        assertTrue("jwt is signed", actualJWT.isSigned());

        JWSVerifier verifier = new MACVerifier(expectedSecret);

        SignedJWT expectedInternalJWT = SignedJWT.parse(actualJWT.encode());
        assertTrue("signature pass verification", expectedInternalJWT.verify(verifier));

    }

    @Test
    public void buildWithHMACSecretReturnsSignedToken() throws Exception {
        String longSecretString = generateRandomBase64String(2454);

        JWT actualJWT = jwtBuilder.buildAndSign(longSecretString);

        assertEquals("jwt is signed", 3, actualJWT.encode().split("\\.").length);
        assertTrue("jwt is signed", actualJWT.isSigned());

        JWSVerifier verifier = new MACVerifier(longSecretString);

        SignedJWT expectedInternalJWT = SignedJWT.parse(actualJWT.encode());
        assertTrue("signature pass verification", expectedInternalJWT.verify(verifier));
    }

    @Test
    public void signedAndUnsignedVersionContainSameClaimsSet() throws Exception {
        JWS someSignedJWT = new JWTBuilderNimbusImpl().withPayload("Some Payload").buildAndSign("hmacSecret");
        JWT someUnsignedJWT = new JWTBuilderNimbusImpl().withPayload("Some Payload").build();

        assertEquals("Payload claims are same", someSignedJWT.getPayloadClaims(), someUnsignedJWT.getPayloadClaims());


        Map<String, Object> allClaimsFromSignedJWT = someSignedJWT.getAllClaims();
        Map<String, Object> allClaimsFromUnsignedJWT = someUnsignedJWT.getAllClaims();

        assertEquals("All claims sets contain same number of claims", allClaimsFromSignedJWT.size(), allClaimsFromUnsignedJWT.size());

        allClaimsFromSignedJWT.forEach( (k, v) -> {
            if ( !k.equals("iat") && !k.equals("exp") ) {
                assertEquals("All claims are same except time claims", v, allClaimsFromUnsignedJWT.get(k));
            }
        });

    }

}
