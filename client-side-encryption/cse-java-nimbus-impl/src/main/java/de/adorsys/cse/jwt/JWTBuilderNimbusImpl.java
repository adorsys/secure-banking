package de.adorsys.cse.jwt;

import com.nimbusds.jwt.JWTClaimsSet;
import de.adorsys.cse.crypt.SecretCredentialEncryptor;
import de.adorsys.cse.jwk.JWK;
import de.adorsys.cse.nonce.NonceGenerator;
import de.adorsys.cse.timestamp.TimestampGenerator;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static de.adorsys.cse.jwt.JWT.Claims.CLAIM_ACCESS_TOKEN;
import static de.adorsys.cse.jwt.JWT.Claims.CLAIM_SERVER_PUBLIC_KEY;

public class JWTBuilderNimbusImpl implements JWTBuilder {

    private NonceGenerator nonceGenerator;
    private TimestampGenerator timestampGenerator;
    private long expirationTimeInMs;
    private SecretCredentialEncryptor encryptor;
    private JWK serverPublicKey;
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
    public JWTBuilder withTimestampGenerator(TimestampGenerator timestampGenerator) {
        if (timestampGenerator == null) {
            throw new IllegalArgumentException("timestampGenerator cannot be null");
        }
        this.timestampGenerator = timestampGenerator;
        return this;
    }

    @Override
    public JWTBuilder withExpirationTimeInMs(long expirationTimeInMs) {
        this.expirationTimeInMs = expirationTimeInMs;
        return this;
    }

    @Override
    public JWTBuilder withEncryptedServerPublicKey(SecretCredentialEncryptor encryptor, JWK serverPublicKey) {
        if (encryptor == null) {
            throw new IllegalArgumentException("encryptor cannot be null");
        }
        if (serverPublicKey == null) {
            throw new IllegalArgumentException("serverPublicKey cannot be null");
        }
        this.encryptor = encryptor;
        this.serverPublicKey = serverPublicKey;
        return this;
    }

    @Override
    public JWT build(String hMacKey) {
        if (hMacKey == null || hMacKey.length() == 0) {
            throw new IllegalArgumentException("hMacKey cannot be null or empty");
        }

        JWTClaimsSet.Builder claimsSetBuilder = new JWTClaimsSet.Builder();

        if (accessToken != null) {
            claimsSetBuilder.claim(CLAIM_ACCESS_TOKEN, accessToken.encode());
        }

        if (encryptor != null) {
            //TODO Dummy. Encrypt all "secret" claims instead
            claimsSetBuilder.claim(CLAIM_SERVER_PUBLIC_KEY, encryptor.encrypt(serverPublicKey.toBase64JSONString()));
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

    TimestampGenerator getTimestampGenerator() {
        return timestampGenerator;
    }

    long getExpirationTimeInMs() {
        return expirationTimeInMs;
    }

    SecretCredentialEncryptor getEncryptor() {
        return encryptor;
    }

    JWK getServerPublicKey() {
        return serverPublicKey;
    }

    JWT getAccessToken() {
        return accessToken;
    }
}
