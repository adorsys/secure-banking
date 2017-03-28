package de.adorsys.cse.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jwt.JWTClaimsSet;
import de.adorsys.cse.crypt.SecretCredentialEncryptor;
import de.adorsys.cse.nonce.NonceGenerator;
import net.minidev.json.JSONObject;

import java.io.InvalidObjectException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static de.adorsys.cse.jwt.JWT.Claims.CLAIM_ACCESS_TOKEN;
import static de.adorsys.cse.jwt.JWT.Claims.CLAIM_PUBLIC_KEY_ENCRYPTED_HMAC_SECRET;

public class JWTBuilderNimbusImpl implements JWTBuilder {

    private static final long ONE_HOUR_MS = 60 * 60 * 1000;

    private NonceGenerator nonceGenerator;
    private long expirationTimeInMs = ONE_HOUR_MS;
    private String hmacEncryptedSecret;
    private JWT accessToken;
    private Map<String, Object> payloadClaims = new HashMap<>();

    @Override
    public JWTBuilder withAccessToken(JWT accessToken) {
        if (accessToken == null) {
            throw new IllegalArgumentException("accessToken cannot be null");
        }
        this.accessToken = accessToken;
        return this;
    }

    @Override
    public JWTBuilder withNonceGenerator(NonceGenerator nonceGenerator) {
        if (nonceGenerator == null) {
            throw new IllegalArgumentException("nonceGenerator cannot be null");
        }
        this.nonceGenerator = nonceGenerator;
        return this;
    }

    @Override
    public JWTBuilder withExpirationTimeInMs(long expirationTimeInMs) {
        this.expirationTimeInMs = expirationTimeInMs;
        return this;
    }

    @Override
    public JWTBuilder withPayload(Object payload) throws InvalidObjectException {
        String payloadClaimIndex = String.valueOf(payloadClaims.size());
        return withPayload(payloadClaimIndex, payload);
    }

    @Override
    public JWTBuilder withPayload(String claim, Object payload) throws InvalidObjectException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new InvalidObjectException("Error by serializing payload object to JSON: " + e.getMessage());
        }
        payloadClaims.put(claim, payload);

        return this;
    }

    @Override
    public JWTBuilder withEncryptedHMacSecretKey(SecretCredentialEncryptor encryptor, String hmacSecret) {
        if (encryptor == null) {
            throw new IllegalArgumentException("encryptor cannot be null");
        }
        if (hmacSecret == null) {
            throw new IllegalArgumentException("hmacSecret cannot be null");
        }
        this.hmacEncryptedSecret = encryptor.encrypt(hmacSecret);
        return this;
    }

    @Override
    public JWS buildAndSign(String hmacSecret) {
        JWT jwt = build();
        JWTSigner jwtSigner = new JWTSignerNimbusImpl();
        return jwtSigner.sign(jwt, hmacSecret);
    }

    @Override
    public JWT build() {
        JWTClaimsSet.Builder claimsSetBuilder = buildClaimsSet();

        return new JWTNimbusImpl(claimsSetBuilder.build());
    }

    private JWTClaimsSet.Builder buildClaimsSet() {
        JWTClaimsSet.Builder claimsSetBuilder = new JWTClaimsSet.Builder();

        if (accessToken != null) {
            claimsSetBuilder.claim(CLAIM_ACCESS_TOKEN, accessToken.encode());
        }

        if (hmacEncryptedSecret != null) {
            claimsSetBuilder.claim(CLAIM_PUBLIC_KEY_ENCRYPTED_HMAC_SECRET, hmacEncryptedSecret);
        }

        if (nonceGenerator != null) {
            claimsSetBuilder.jwtID(nonceGenerator.generateNonce());
        }

        JSONObject payloadClaimsArray = new JSONObject(payloadClaims);
        claimsSetBuilder.claim(JWT.Claims.CLAIM_PAYLOAD, payloadClaimsArray);

        Instant currentTime = Instant.now();
        Instant expirationTime = currentTime.plus(expirationTimeInMs, ChronoUnit.MILLIS);
        claimsSetBuilder.issueTime(Date.from(currentTime));
        claimsSetBuilder.expirationTime(Date.from(expirationTime));
        return claimsSetBuilder;
    }

    NonceGenerator getNonceGenerator() {
        return nonceGenerator;
    }

    long getExpirationTimeInMs() {
        return expirationTimeInMs;
    }

    String getHmacEncryptedSecret() {
        return hmacEncryptedSecret;
    }

    JWT getAccessToken() {
        return accessToken;
    }

    Map<String, Object> getPayloadClaims() {
        return payloadClaims;
    }

}
