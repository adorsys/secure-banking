package de.adorsys.cse.jwt;

import com.nimbusds.jwt.JWTClaimsSet;
import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static de.adorsys.cse.Base64StringGenerator.generateRandomBase64String;
import static org.junit.Assert.*;

public class JWTNimbusImplTest {
    private JWTNimbusImpl jwt;

    @Test(expected = IllegalArgumentException.class)
    public void createWithNullThrowsException() throws Exception {
        //noinspection ConstantConditions
        new JWTNimbusImpl((String)null);
        fail("Creation of JWT with null-String causes IllegalArgumentException");
    }

    @Test(expected = ParseException.class)
    public void createWithAStringThatNotATokenThrowsParseException() throws Exception {
        new JWTNimbusImpl("bla-bla-bla");
        fail("Creation of JWT with invalid token-String causes ParseException");
    }

    @Test
    public void serializationReturnsSameString() throws Exception {
        final String base64encodedToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWV9.TJVA95OrM7E2cBab30RMHrHDcEfxjoYZgeFONFh7HgQ";

        jwt = new JWTNimbusImpl(base64encodedToken);
        assertEquals("Serializaion/Deserializaion returns same string", base64encodedToken, jwt.toString());
    }

    @Test
    public void serializationReturnsBase64EncodedJWT() throws Exception {
        final String expectedEncodedJWTToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWV9.TJVA95OrM7E2cBab30RMHrHDcEfxjoYZgeFONFh7HgQ";

        jwt = new JWTNimbusImpl(expectedEncodedJWTToken);
        assertEquals("Serializaion/Deserializaion returns same string", expectedEncodedJWTToken, jwt.encode());
    }

    @Test
    public void getClaimReturnsEmptyIfNoSuchClaimProvided() {
        final String claimName = generateRandomBase64String(2048);

        Optional<String> emptyClaim = jwt.getClaim(claimName);
        assertFalse("call of claim that is not in the token returns empty Optional", emptyClaim.isPresent());
    }

    @Before
    public void setUp() throws Exception {
        final String base64encodedToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWV9.TJVA95OrM7E2cBab30RMHrHDcEfxjoYZgeFONFh7HgQ";
        jwt = new JWTNimbusImpl(base64encodedToken);
    }

    @Test
    public void addAndGetClaim() {
        for (int i = 0; i < 50; i++) {
            final String claimName = generateRandomBase64String(2048);
            final String providedClaimValue = generateRandomBase64String(2048);

            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder().claim(claimName, providedClaimValue).build();

            jwt = new JWTNimbusImpl(claimsSet);
            Optional<String> actualReturnedClaim = jwt.getClaim(claimName);
            assertTrue("getClaim retuns claim value", actualReturnedClaim.isPresent());
            assertEquals("getClaim returns the claim that was set", providedClaimValue, actualReturnedClaim.get());
        }
    }


    @Test
    public void addAndGetClaims() {
        for (int testNo = 0; testNo < 50; testNo++) {
            JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder();

            final int claimsCount = 10;

            Map<String, String> expectedClaimsMap = new HashMap<>(10);

            for (int i = 0; i < claimsCount; i++) {
                String claimName = generateRandomBase64String(20);
                String providedClaimValue = generateRandomBase64String(2048);

                expectedClaimsMap.put(claimName, providedClaimValue);
                builder = builder.claim(claimName, providedClaimValue);
            }

            JWTClaimsSet claimsSet = builder.build();
            jwt = new JWTNimbusImpl(claimsSet);

            Map<String, Object> allActualClaims = jwt.getAllClaims();
            assertEquals("claims count corresponds to set", claimsCount, allActualClaims.size());

            allActualClaims.forEach( (actualClaimName, actualClaimValue) -> {
                assertTrue("claim is in the list of stored claims", expectedClaimsMap.containsKey(actualClaimName));
                assertEquals("claim returns the claim that was set", expectedClaimsMap.get(actualClaimName), actualClaimValue);
            }
            );
        }
    }

    @Test
    public void addAndGetOnePayloadClaim() {
        for (int testNo = 0; testNo < 50; testNo++) {

            String providedClaimValue = generateRandomBase64String(2048);

            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder().claim("pay", providedClaimValue).build();
            jwt = new JWTNimbusImpl(claimsSet);

            Map<String, Object> payloadClaims = jwt.getPayloadClaims();
            assertEquals("Payload claims count corresponds to set", 1, payloadClaims.size());

            payloadClaims.forEach( (actualClaimName, actualClaimValue) -> {
                        assertTrue("claim is in the list of stored claims", actualClaimName.equals("0"));
                        assertEquals("claim returns the claim that was set", providedClaimValue, actualClaimValue);
                    }
            );
        }
    }

    @Test
    public void getPayloadClaimsWithoutPayloadReturnsEmptyMap() {
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder().build();
        jwt = new JWTNimbusImpl(claimsSet);

        Map<String, Object> payloadClaims = jwt.getPayloadClaims();
        assertEquals("Payload claims count corresponds to set", 0, payloadClaims.size());
    }

    @Test
    public void addAndGetManyPayloadClaims() {
        for (int testNo = 0; testNo < 50; testNo++) {

            final int claimsCount = 10;

            Map<String, String> expectedPayloadClaimsMap = new HashMap<>(10);

            for (int i = 0; i < claimsCount; i++) {
                String claimName = generateRandomBase64String(20);
                String providedClaimValue = generateRandomBase64String(2048);

                expectedPayloadClaimsMap.put(claimName, providedClaimValue);
            }

            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder().claim("pay", expectedPayloadClaimsMap).build();
            jwt = new JWTNimbusImpl(claimsSet);

            Map<String, Object> payloadClaims = jwt.getPayloadClaims();
            assertEquals("claims count corresponds to set", claimsCount, payloadClaims.size());

            payloadClaims.forEach( (actualClaimName, actualClaimValue) -> {
                        assertTrue("claim is in the list of stored claims", expectedPayloadClaimsMap.containsKey(actualClaimName));
                        assertEquals("claim returns the claim that was set", expectedPayloadClaimsMap.get(actualClaimName), actualClaimValue);
                    }
            );
        }
    }
}

