package de.adorsys.cse.client.oauth;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.PlainJWT;
import de.adorsys.cse.jwt.JWT;
import de.adorsys.cse.jwt.JWTNimbusImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Optional;

public class AccessTokenExtractorImplTest {
    private AccessTokenExtractor accessTokenExtractor;

    @Before
    public void setUp() {
        accessTokenExtractor = new AccessTokenExtractorImpl();
    }

    @Test(expected = IllegalArgumentException.class)
    public void passedNullThrowsIllegalArgumentException() throws Exception {
        accessTokenExtractor.extractAccessToken(null);
    }

    @Test
    public void withoutAccessTokenPassedReturnsNone() throws Exception {

        final String inputBase64EncodedJWT = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpZCI6IjYzMjIwNzg0YzUzODA3ZjVmZTc2Yjg4ZjZkNjdlMmExZTIxODlhZTEiLCJjbGllbnRfaWQiOiJUZXN0IENsaWVudCBJRCIsInVzZXJfaWQiOm51bGwsImV4cGlyZXMiOjEzODAwNDQ1NDIsInRva2VuX3R5cGUiOiJiZWFyZXIiLCJzY29wZSI6bnVsbH0.PcC4k8Q_etpU-J4yGFEuBUdeyMJhtpZFkVQ__sXpe78eSi7xTniqOOtgfWa62Y4sj5Npta8xPuDglH8Fueh_APZX4wGCiRE1P4nT4APQCOTbgcuCNXwjmP8znk9F76ID2WxThaMbmpsTTEkuyyUYQKCCdxlIcSbVvcLZUGKZ6-g";
        JWT oAuthTokenToInput = new JWTNimbusImpl(inputBase64EncodedJWT);

        Optional<JWT> actualAccessToken = accessTokenExtractor.extractAccessToken(oAuthTokenToInput);

        Assert.assertFalse("Token without access_token claim returns nothing", actualAccessToken.isPresent());
    }

    @Test
    public void extractAccessToken() throws Exception {

        JWT expectedAccesToken = new JWTNimbusImpl("eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpZCI6IjYzMjIwNzg0YzUzODA3ZjVmZTc2Yjg4ZjZkNjdlMmExZTIxODlhZTEiLCJjbGllbnRfaWQiOiJUZXN0IENsaWVudCBJRCIsInVzZXJfaWQiOm51bGwsImV4cGlyZXMiOjEzODAwNDQ1NDIsInRva2VuX3R5cGUiOiJiZWFyZXIiLCJzY29wZSI6bnVsbH0.PcC4k8Q_etpU-J4yGFEuBUdeyMJhtpZFkVQ__sXpe78eSi7xTniqOOtgfWa62Y4sj5Npta8xPuDglH8Fueh_APZX4wGCiRE1P4nT4APQCOTbgcuCNXwjmP8znk9F76ID2WxThaMbmpsTTEkuyyUYQKCCdxlIcSbVvcLZUGKZ6-g");

        final String inputBase64EncodedJWT = "eyJhbGciOiJub25lIn0.eyJhY2Nlc3NfdG9rZW4iOiJleUowZVhBaU9pSktWMVFpTENKaGJHY2lPaUpTVXpJMU5pSjkuZXlKcFpDSTZJall6TWpJd056ZzBZelV6T0RBM1pqVm1aVGMyWWpnNFpqWmtOamRsTW1FeFpUSXhPRGxoWlRFaUxDSmpiR2xsYm5SZmFXUWlPaUpVWlhOMElFTnNhV1Z1ZENCSlJDSXNJblZ6WlhKZmFXUWlPbTUxYkd3c0ltVjRjR2x5WlhNaU9qRXpPREF3TkRRMU5ESXNJblJ2YTJWdVgzUjVjR1VpT2lKaVpXRnlaWElpTENKelkyOXdaU0k2Ym5Wc2JIMC5QY0M0azhRX2V0cFUtSjR5R0ZFdUJVZGV5TUpodHBaRmtWUV9fc1hwZTc4ZVNpN3hUbmlxT090Z2ZXYTYyWTRzajVOcHRhOHhQdURnbEg4RnVlaF9BUFpYNHdHQ2lSRTFQNG5UNEFQUUNPVGJnY3VDTlh3am1QOHpuazlGNzZJRDJXeFRoYU1ibXBzVFRFa3V5eVVZUUtDQ2R4bEljU2JWdmNMWlVHS1o2LWciLCJleHBpcmVzIjoiMTM4MjYzMDQ3MyIsImNsaWVudF9pZCI6Ik1ZX0NMSUVOVF9JRCJ9.";
        JWT oAuthTokenToInput = new JWTNimbusImpl(inputBase64EncodedJWT);

        Optional<JWT> actualAccessToken = accessTokenExtractor.extractAccessToken(oAuthTokenToInput);

        Assert.assertTrue("Access token is present", actualAccessToken.isPresent());
        Assert.assertEquals("Access Token is extracted", expectedAccesToken, actualAccessToken.get());

    }


    @Test
    @Ignore("It's not a test, but utility to generate string")
    public void generateAccessToken() {
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .claim("access_token", "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpZCI6IjYzMjIwNzg0YzUzODA3ZjVmZTc2Yjg4ZjZkNjdlMmExZTIxODlhZTEiLCJjbGllbnRfaWQiOiJUZXN0IENsaWVudCBJRCIsInVzZXJfaWQiOm51bGwsImV4cGlyZXMiOjEzODAwNDQ1NDIsInRva2VuX3R5cGUiOiJiZWFyZXIiLCJzY29wZSI6bnVsbH0.PcC4k8Q_etpU-J4yGFEuBUdeyMJhtpZFkVQ__sXpe78eSi7xTniqOOtgfWa62Y4sj5Npta8xPuDglH8Fueh_APZX4wGCiRE1P4nT4APQCOTbgcuCNXwjmP8znk9F76ID2WxThaMbmpsTTEkuyyUYQKCCdxlIcSbVvcLZUGKZ6-g")
                .claim("client_id", "MY_CLIENT_ID")
                .claim("expires", "1382630473")
                .build();
        com.nimbusds.jwt.JWT jwt = new PlainJWT(claimsSet);
        System.out.println(jwt.serialize());
    }
}
