package de.adorsys.cse.jwt;

import com.nimbusds.jwt.JWTClaimsSet;
import de.adorsys.cse.crypt.SecretCredentialEncryptor;
import de.adorsys.cse.nonce.NonceGenerator;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static de.adorsys.cse.jwt.JWT.Claims.CLAIM_ACCESS_TOKEN;
import static de.adorsys.cse.jwt.JWT.Claims.CLAIM_PUBLIC_KEY_ENCRYPTED_HMAC_SECRET;

public class JWTBuilderNimbusImpl implements JWTBuilder {

    private NonceGenerator nonceGenerator;
    private long expirationTimeInMs;
    private String hmacEncryptedSecret;
    private JWT accessToken;

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
    public JWT buildAndSign(String hmacSecret) {
        if (hmacSecret == null || hmacSecret.length() == 0) {
            throw new IllegalArgumentException("hmacSecret cannot be null or empty");
        }

        return build();
    }

    @Override
    public JWT build() {
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

        Instant currentTime = Instant.now();
        Instant expirationTime = currentTime.plus(expirationTimeInMs, ChronoUnit.MILLIS);
        claimsSetBuilder.issueTime(Date.from(currentTime));
        claimsSetBuilder.expirationTime(Date.from(expirationTime));

        return new JWTNimbusImpl(claimsSetBuilder.build());
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
}
