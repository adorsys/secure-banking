package de.adorsys.cse.jwt;

import de.adorsys.cse.crypt.SecretCredentialEncryptor;
import de.adorsys.cse.jwk.JWK;
import de.adorsys.cse.nonce.NonceGenerator;
import de.adorsys.cse.timestamp.TimestampGenerator;

public class JWTBuilderImpl implements JWTBuilder {

    private JWT jwt;

    JWTBuilderImpl() {
        this.jwt = new JWT() {
            @Override
            public Base64EncodedJWT encode() {
                return null;
            }

            @Override
            public String getResourcePublicKey() {
                return null;
            }
        };
    }

    @Override
    public JWTBuilder withAccessToken(Base64EncodedJWT accessToken) {
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
    public Base64EncodedJWT build(JWT hMacKey) {
        return null;
    }

}
