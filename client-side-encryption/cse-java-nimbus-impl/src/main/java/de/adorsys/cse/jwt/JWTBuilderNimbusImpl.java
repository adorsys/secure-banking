package de.adorsys.cse.jwt;

import com.nimbusds.jwt.JWTClaimsSet;
import de.adorsys.cse.crypt.SecretCredentialEncryptor;
import de.adorsys.cse.jwk.JWK;
import de.adorsys.cse.nonce.NonceGenerator;
import de.adorsys.cse.timestamp.TimestampGenerator;

public class JWTBuilderNimbusImpl implements JWTBuilder {
    private static final String CLAIM_ACCESS_TOKEN = "access_token";

    private JWTClaimsSet.Builder claimsSetBuilder;

    JWTBuilderNimbusImpl() {
        claimsSetBuilder = new JWTClaimsSet.Builder();
    }

    @Override
    public JWTBuilder withAccessToken(JWT accessToken) {
        if (accessToken == null) {
            throw new IllegalArgumentException("accessToken cannot be null");
        }
        claimsSetBuilder.claim(CLAIM_ACCESS_TOKEN, accessToken.encode());
        return this;
    }

    @Override
    public JWTBuilder withNonceGenerator(NonceGenerator nonceGenerator) {
        return this;
    }

    @Override
    public JWTBuilder withTimestampGenerator(TimestampGenerator timestampGenerator) {
        return this;
    }

    @Override
    public JWTBuilder withExpirationTimeInMs(long expirationTimeInMs) {
        return this;
    }

    @Override
    public JWTBuilder withEncryptedServerPublicKey(SecretCredentialEncryptor encryptor, JWK serverPublicKey) {
        return this;
    }

    @Override
    public JWT build(String hMacKey) {
        return new JWTNimbusImpl(claimsSetBuilder.build());
    }

}
