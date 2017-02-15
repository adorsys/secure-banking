package de.adorsys.cse.client.oauth;

import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.PlainJWT;
import de.adorsys.cse.jwk.JWK;
import de.adorsys.cse.jwt.JWT;
import de.adorsys.cse.jwt.JWTNimbusImpl;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

public class PubicKeyExtractorImplTest {
    private PublicKeyExtractor publicKeyExtractor;

    @Before
    public void setUp() {
        publicKeyExtractor = new PubicKeyExtractorImpl();
    }

    @Test(expected = IllegalArgumentException.class)
    public void extractFromNullReturnsIllegalArgumentException() throws Exception {
        publicKeyExtractor.extractPublicKey(null);
        Assert.fail("Extract from null object returns IllegalArgumentException");
    }

    @Test
    public void returnsNoValueIfNoPKInToken() throws Exception {
        //There is no "res_pub_key" claim in this token
        JWT inputTokenWithoutPK = new JWTNimbusImpl("eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpZCI6IjYzMjIwNzg0YzUzODA3ZjVmZTc2Yjg4ZjZkNjdlMmExZTIxODlhZTEiLCJjbGllbnRfaWQiOiJUZXN0IENsaWVudCBJRCIsInVzZXJfaWQiOm51bGwsImV4cGlyZXMiOjEzODAwNDQ1NDIsInRva2VuX3R5cGUiOiJiZWFyZXIiLCJzY29wZSI6bnVsbH0.PcC4k8Q_etpU-J4yGFEuBUdeyMJhtpZFkVQ__sXpe78eSi7xTniqOOtgfWa62Y4sj5Npta8xPuDglH8Fueh_APZX4wGCiRE1P4nT4APQCOTbgcuCNXwjmP8znk9F76ID2WxThaMbmpsTTEkuyyUYQKCCdxlIcSbVvcLZUGKZ6-g");
        Optional<JWK> actualJWK = publicKeyExtractor.extractPublicKey(inputTokenWithoutPK);
        Assert.assertFalse("Extract returns no value if there is no such claim in the token", actualJWK.isPresent());
    }

    @Test
    public void extractInvalidFormatPublicKeyFromJWTTokenReturnsParseException() throws Exception {
        //There is a "res_pub_key" claim in this token with "Bla-bla-bla encoded"
        JWT inputTokenWithoutPK = new JWTNimbusImpl("eyJhbGciOiJub25lIn0.eyJhY2Nlc3NfdG9rZW4iOiJleUowZVhBaU9pSktWMVFpTENKaGJHY2lPaUpTVXpJMU5pSjkuZXlKcFpDSTZJall6TWpJd056ZzBZelV6T0RBM1pqVm1aVGMyWWpnNFpqWmtOamRsTW1FeFpUSXhPRGxoWlRFaUxDSmpiR2xsYm5SZmFXUWlPaUpVWlhOMElFTnNhV1Z1ZENCSlJDSXNJblZ6WlhKZmFXUWlPbTUxYkd3c0ltVjRjR2x5WlhNaU9qRXpPREF3TkRRMU5ESXNJblJ2YTJWdVgzUjVjR1VpT2lKaVpXRnlaWElpTENKelkyOXdaU0k2Ym5Wc2JIMC5QY0M0azhRX2V0cFUtSjR5R0ZFdUJVZGV5TUpodHBaRmtWUV9fc1hwZTc4ZVNpN3hUbmlxT090Z2ZXYTYyWTRzajVOcHRhOHhQdURnbEg4RnVlaF9BUFpYNHdHQ2lSRTFQNG5UNEFQUUNPVGJnY3VDTlh3am1QOHpuazlGNzZJRDJXeFRoYU1ibXBzVFRFa3V5eVVZUUtDQ2R4bEljU2JWdmNMWlVHS1o2LWciLCJleHBpcmVzIjoiMTM4MjYzMDQ3MyIsImNsaWVudF9pZCI6Ik1ZX0NMSUVOVF9JRCIsInJlc19wdWJfa2V5IjoiQmxhLWJsYS1ibGEifQ.");
        Optional<JWK> actual = publicKeyExtractor.extractPublicKey(inputTokenWithoutPK);
        Assert.assertFalse("Invalid public key format (not a JWK) returns empty result", actual.isPresent());
    }

    @Test
    public void extractOfValidPublicKeyFromJWTToken() throws Exception {
        //There is a valid "res_pub_key" claim in this token
        final String encodedBase64JWK = "eyJwIjoiLUt2NE9WNFR4U2VHUS1JakpwTEJmeEw2WW1GZTFXajQtcWpfMHRqdEo4R0t6WWpGcmtoM29xWUNOR0pQdnlqQnd5N3doREVFb2lsRVZQcWpFYTNSN0thc0J5Nk5iY1BjNU44Z0wwdTh3VktnRnR3bW94d25pb3ZMNmxleUo1WlZXUlFYdnAxTXdacmc3RjBaVzc5RGlnTzl0VFNoX0U3STJDWi14Z2NhLXljIiwia3R5IjoiUlNBIiwicSI6InIyU2x5YzQ4VlpRS2FYVTNvMTZCTFYyT3FOcHVtaEx6RFVyRlFnQjF3Yk9YV0Z0bDJXNnZldndaR3VVWmtjNmpteWVhcW5fOWZNQURPbVYxTGNRY0FDdDdMXzhRS1lwTC1JRTFXT3BmMWp0N0g3WkxEZFVBbUxRYURHNG0xOHFBLTRUUW1DMkFiazVURmprZ3d4N0lNb25aSzlETG9INk05N2VSdlBTWWhLVSIsImQiOiJiR1dqMkc3Ylh5Y1B6eE9wd1A0RXZwOUJFSDlORzJsLXNSUzJuT3Q2ZzZ0OWZlRlRMSnBVdUVzVDhwTXR2M1d4QnBaR01TY1JhZjB3X0xjT2FNMExTaXItWTAwQWZyN3VZUzlSZUluaXRiano1OUY4OG11aWl1NVVaMDBDRnQzWjVKTjBZNVQ2SWttbjFVd0xBT0ZmOHZ3S0YxWTJJdmdITTU2WHdTSVcwYkIyQzRlR1M0c1Z3R29CdDdUWVcwNjBiUU1vNWVrdmVSQmcybHVKbjY5UHB4ZllCdWJFTm4yaXBsa0UyWUFRSnh6Ul9lMVphWmNvdFQyczlVMzIxWVJwV1hiMWRmOGtYd0ZUWHZGSHFYSE1UWWlNc3FSeHVucU0xYU5hNVYyd3BBNXJpTUhSYlZVNWI5WFVwVkc1ZkdqRkZIc0pteHBVS052VG81ajlnQnY1V1EiLCJlIjoiQVFBQiIsImtpZCI6IjFiMTM3MWJjLWIyZDYtNDBlYi1hYWVkLWJmNjY1NzY1MzY3YyIsInFpIjoiSU9kVi04Nlltb0ZieEJmRDBoRzFwWUx3anhXV2t1ckRBdlJkR0pxVFRBTFhxdVhyU3NBb1F0VEd4WVFyakRCaU13T0JMd1lTazQ2bS1iSnRSUElMRl9vZG1RT1hhSHdBTXRyaHg1QkFmdmlKeTlwQkxGMXpUSzgyb1daMVE5SGlJZEpHYWJ2THdPUF9BWmRrQVV1NnY5UXVRX0NRUFpybmNpeUNNc2dtVjQwIiwiZHAiOiI4dm4xblBoVGtQekJzR3F4dGVzSHJrYTQ2SkFEdmJySUxRUWNrOFRxVlVKRGdmajJUd2ozX0xFQXpuYVJQVk54b0dtcEVVRmtLWjd3NmFmTTFtVmxEVHItTkRaRWN0QkNjay1pZnhRZDV6ZjR2TjEzMDBDYlVrQ3VpdGdOZDZqbzFCVlI3aXA4NlYtUUR1UVk5eEMzeVk5R09seW5Pb1V3c2lOTEUtdTFiVmsiLCJkcSI6ImJFYzdYbkdXcDlKajl5VHBPMDhkZW04Mm9pRnZoR2pNYWRsT0ctZ0N5cGxmaHFtdUgtU0lLdW0xYWZnanBfTWs1NlUwZzZ2T1J4U1hLWmIzRjFEcFpHVnpnSGI3ZUZtcmV2Znhnam4yX2xfc0tfQ3pOd19YZFc1Q1lvZ2lMTGdVbHJJd3dMSGVocnFONnJ0TDhZbHdXRnJubW5nbExhalN5QjhvdHJsZDZ3MCIsIm4iOiJxbDlTNXd3UUgxbmg2c3B6TWFMX0xOTWZ6ckNGZHlwMG13SFltMFR2ODhIX2RMTDJKVWEwYnE2bGhRVVVVZE1BRHBWT1RtS09JdmwzcHhtUTFqZVdmbFFtM1hMNVJwSUVhSzFZOTVFRWFYTlVLLXNoV3dLRVdPR1dPZFNzX0pSMVlNOXNmMzFXTm9GbHdadjlLQ3hiYUZRM1pqZ0oxSnRYN0F1V2JFYWFzOXA4RXpyamV6TzZ0UlNQcVRYWGJwR1NNUDBYdllHNEVTOXk0RzAweTJUY09TUVZLX29LUXdqdzJmUzVWSXNXYzV4Y1pYdTJmY2lXeTA4RzVrM3JveU9sS05oaUxkNzZzdlowazMteUtUMXJKTTVxU3VuT3BwWjNpLWduSlAxSjB4ell1aXJtR0l6bUVqa1hKN2xSYzZmT2NHY2Jtdi1fU2ZyWWVraFd5UXY4SXcifQ==";

        JWT inputTokenWithoutPK = new JWTNimbusImpl("eyJhbGciOiJub25lIn0.eyJhY2Nlc3NfdG9rZW4iOiJleUowZVhBaU9pSktWMVFpTENKaGJHY2lPaUpTVXpJMU5pSjkuZXlKcFpDSTZJall6TWpJd056ZzBZelV6T0RBM1pqVm1aVGMyWWpnNFpqWmtOamRsTW1FeFpUSXhPRGxoWlRFaUxDSmpiR2xsYm5SZmFXUWlPaUpVWlhOMElFTnNhV1Z1ZENCSlJDSXNJblZ6WlhKZmFXUWlPbTUxYkd3c0ltVjRjR2x5WlhNaU9qRXpPREF3TkRRMU5ESXNJblJ2YTJWdVgzUjVjR1VpT2lKaVpXRnlaWElpTENKelkyOXdaU0k2Ym5Wc2JIMC5QY0M0azhRX2V0cFUtSjR5R0ZFdUJVZGV5TUpodHBaRmtWUV9fc1hwZTc4ZVNpN3hUbmlxT090Z2ZXYTYyWTRzajVOcHRhOHhQdURnbEg4RnVlaF9BUFpYNHdHQ2lSRTFQNG5UNEFQUUNPVGJnY3VDTlh3am1QOHpuazlGNzZJRDJXeFRoYU1ibXBzVFRFa3V5eVVZUUtDQ2R4bEljU2JWdmNMWlVHS1o2LWciLCJleHBpcmVzIjoiMTM4MjYzMDQ3MyIsImNsaWVudF9pZCI6Ik1ZX0NMSUVOVF9JRCIsInJlc19wdWJfa2V5IjoiZXlKd0lqb2lMVXQyTkU5V05GUjRVMlZIVVMxSmFrcHdURUptZUV3MldXMUdaVEZYYWpRdGNXcGZNSFJxZEVvNFIwdDZXV3BHY210b00yOXhXVU5PUjBwUWRubHFRbmQ1TjNkb1JFVkZiMmxzUlZaUWNXcEZZVE5TTjB0aGMwSjVOazVpWTFCak5VNDRaMHd3ZFRoM1ZrdG5SblIzYlc5NGQyNXBiM1pNTm14bGVVbzFXbFpYVWxGWWRuQXhUWGRhY21jM1JqQmFWemM1UkdsblR6bDBWRk5vWDBVM1NUSkRXaTE0WjJOaExYbGpJaXdpYTNSNUlqb2lVbE5CSWl3aWNTSTZJbkl5VTJ4NVl6UTRWbHBSUzJGWVZUTnZNVFpDVEZZeVQzRk9jSFZ0YUV4NlJGVnlSbEZuUWpGM1lrOVlWMFowYkRKWE5uWmxkbmRhUjNWVldtdGpObXB0ZVdWaGNXNWZPV1pOUVVSUGJWWXhUR05SWTBGRGREZE1YemhSUzFsd1RDMUpSVEZYVDNCbU1XcDBOMGczV2t4RVpGVkJiVXhSWVVSSE5HMHhPSEZCTFRSVVVXMURNa0ZpYXpWVVJtcHJaM2Q0TjBsTmIyNWFTemxFVEc5SU5rMDVOMlZTZGxCVFdXaExWU0lzSW1RaU9pSmlSMWRxTWtjM1lsaDVZMUI2ZUU5d2QxQTBSWFp3T1VKRlNEbE9SekpzTFhOU1V6SnVUM1EyWnpaME9XWmxSbFJNU25CVmRVVnpWRGh3VFhSMk0xZDRRbkJhUjAxVFkxSmhaakIzWDB4alQyRk5NRXhUYVhJdFdUQXdRV1p5TjNWWlV6bFNaVWx1YVhSaWFubzFPVVk0T0cxMWFXbDFOVlZhTURCRFJuUXpXalZLVGpCWk5WUTJTV3R0YmpGVmQweEJUMFptT0haM1MwWXhXVEpKZG1kSVRUVTJXSGRUU1Zjd1lrSXlRelJsUjFNMGMxWjNSMjlDZERkVVdWY3dOakJpVVUxdk5XVnJkbVZTUW1jeWJIVktialk1VUhCNFpsbENkV0pGVG00eWFYQnNhMFV5V1VGUlNuaDZVbDlsTVZwaFdtTnZkRlF5Y3psVk16SXhXVkp3VjFoaU1XUm1PR3RZZDBaVVdIWkdTSEZZU0UxVVdXbE5jM0ZTZUhWdWNVMHhZVTVoTlZZeWQzQkJOWEpwVFVoU1lsWlZOV0k1V0ZWd1ZrYzFaa2RxUmtaSWMwcHRlSEJWUzA1MlZHODFhamxuUW5ZMVYxRWlMQ0psSWpvaVFWRkJRaUlzSW10cFpDSTZJakZpTVRNM01XSmpMV0l5WkRZdE5EQmxZaTFoWVdWa0xXSm1OalkxTnpZMU16WTNZeUlzSW5GcElqb2lTVTlrVmkwNE5sbHRiMFppZUVKbVJEQm9SekZ3V1V4M2FuaFhWMnQxY2tSQmRsSmtSMHB4VkZSQlRGaHhkVmh5VTNOQmIxRjBWRWQ0V1ZGeWFrUkNhVTEzVDBKTWQxbFRhelEyYlMxaVNuUlNVRWxNUmw5dlpHMVJUMWhoU0hkQlRYUnlhSGcxUWtGbWRtbEtlVGx3UWt4R01YcFVTemd5YjFkYU1WRTVTR2xKWkVwSFlXSjJUSGRQVUY5QldtUnJRVlYxTm5ZNVVYVlJYME5SVUZweWJtTnBlVU5OYzJkdFZqUXdJaXdpWkhBaU9pSTRkbTR4YmxCb1ZHdFFla0p6UjNGNGRHVnpTSEpyWVRRMlNrRkVkbUp5U1V4UlVXTnJPRlJ4VmxWS1JHZG1hakpVZDJvelgweEZRWHB1WVZKUVZrNTRiMGR0Y0VWVlJtdExXamQzTm1GbVRURnRWbXhFVkhJdFRrUmFSV04wUWtOamF5MXBabmhSWkRWNlpqUjJUakV6TURCRFlsVnJRM1ZwZEdkT1pEWnFiekZDVmxJM2FYQTRObFl0VVVSMVVWazVlRU16ZVZrNVIwOXNlVzVQYjFWM2MybE9URVV0ZFRGaVZtc2lMQ0prY1NJNkltSkZZemRZYmtkWGNEbEthamw1VkhCUE1EaGtaVzA0TW05cFJuWm9SMnBOWVdSc1QwY3RaME41Y0d4bWFIRnRkVWd0VTBsTGRXMHhZV1puYW5CZlRXczFObFV3WnpaMlQxSjRVMWhMV21JelJqRkVjRnBIVm5wblNHSTNaVVp0Y21WMlpuaG5hbTR5WDJ4ZmMwdGZRM3BPZDE5WVpGYzFRMWx2WjJsTVRHZFZiSEpKZDNkTVNHVm9jbkZPTm5KMFREaFpiSGRYUm5KdWJXNW5iRXhoYWxONVFqaHZkSEpzWkRaM01DSXNJbTRpT2lKeGJEbFROWGQzVVVneGJtZzJjM0I2VFdGTVgweE9UV1o2Y2tOR1pIbHdNRzEzU0ZsdE1GUjJPRGhJWDJSTVRESktWV0V3WW5FMmJHaFJWVlZWWkUxQlJIQldUMVJ0UzA5SmRtd3pjSGh0VVRGcVpWZG1iRkZ0TTFoTU5WSndTVVZoU3pGWk9UVkZSV0ZZVGxWTExYTm9WM2RMUlZkUFIxZFBaRk56WDBwU01WbE5PWE5tTXpGWFRtOUdiSGRhZGpsTFEzaGlZVVpSTTFwcVowb3hTblJZTjBGMVYySkZZV0Z6T1hBNFJYcHlhbVY2VHpaMFVsTlFjVlJZV0dKd1IxTk5VREJZZGxsSE5FVlRPWGswUnpBd2VUSlVZMDlUVVZaTFgyOUxVWGRxZHpKbVV6VldTWE5YWXpWNFkxcFlkVEptWTJsWGVUQTRSelZyTTNKdmVVOXNTMDVvYVV4a056WnpkbG93YXpNdGVVdFVNWEpLVFRWeFUzVnVUM0J3V2pOcExXZHVTbEF4U2pCNGVsbDFhWEp0UjBsNmJVVnFhMWhLTjJ4U1l6Wm1UMk5IWTJKdGRpMWZVMlp5V1dWcmFGZDVVWFk0U1hjaWZRPT0ifQ.");
        Optional<JWK> actualJWK = publicKeyExtractor.extractPublicKey(inputTokenWithoutPK);
        TestCase.assertTrue("Extract returns no value if there is no such claim in the token", actualJWK.isPresent());
        //noinspection OptionalGetWithoutIsPresent
        Assert.assertEquals("Extracted public key in JWK format corresponds to given one", encodedBase64JWK, actualJWK.get().toBase64JSONString());
    }

    @Test
    @Ignore("It's not a test, but utility to generate string")
    public void generateTokenWithPubKey() throws Exception {
        // Generate the RSA key pair
        KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
        gen.initialize(2048); // Set the desired key length
        KeyPair keyPair = gen.generateKeyPair();

        // Convert to JWK format
        com.nimbusds.jose.jwk.JWK jwk = new RSAKey.Builder((RSAPublicKey)keyPair.getPublic())
                .privateKey((RSAPrivateKey)keyPair.getPrivate())
                .keyID(UUID.randomUUID().toString()) // Give the key some ID (optional)
                .build();

        // Output
        final String base64encodedJWK = Base64.getEncoder().encodeToString(jwk.toJSONString().getBytes());
        final String base64encodedJWKApache = org.apache.commons.codec.binary.Base64.encodeBase64String(jwk.toJSONString().getBytes());
        System.out.println("JWK: " + jwk.toJSONString());
        System.out.println("JDK Base64-encoded JWK   : " + base64encodedJWK);
        System.out.println("Apache Base64-encoded JWK: " + base64encodedJWKApache);
        Assert.assertEquals(base64encodedJWK, base64encodedJWKApache);



        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .claim("access_token", "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpZCI6IjYzMjIwNzg0YzUzODA3ZjVmZTc2Yjg4ZjZkNjdlMmExZTIxODlhZTEiLCJjbGllbnRfaWQiOiJUZXN0IENsaWVudCBJRCIsInVzZXJfaWQiOm51bGwsImV4cGlyZXMiOjEzODAwNDQ1NDIsInRva2VuX3R5cGUiOiJiZWFyZXIiLCJzY29wZSI6bnVsbH0.PcC4k8Q_etpU-J4yGFEuBUdeyMJhtpZFkVQ__sXpe78eSi7xTniqOOtgfWa62Y4sj5Npta8xPuDglH8Fueh_APZX4wGCiRE1P4nT4APQCOTbgcuCNXwjmP8znk9F76ID2WxThaMbmpsTTEkuyyUYQKCCdxlIcSbVvcLZUGKZ6-g")
                .claim("res_pub_key", base64encodedJWK)
                .claim("client_id", "MY_CLIENT_ID")
                .claim("expires", "1382630473")
                .build();
        com.nimbusds.jwt.JWT jwt = new PlainJWT(claimsSet);
        System.out.println("JWT: " + jwt.serialize());
    }

}
