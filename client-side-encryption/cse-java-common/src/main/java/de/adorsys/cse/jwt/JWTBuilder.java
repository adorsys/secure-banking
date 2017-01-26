package de.adorsys.cse.jwt;

import de.adorsys.cse.crypt.SecretCredentialEncryptor;
import de.adorsys.cse.nonce.NonceGenerator;
import de.adorsys.cse.timestamp.TimestampGenerator;

public class JWTBuilder {

    private JWT jwt;

    JWTBuilder() {
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

    public JWTBuilder withAccessToken(Base64EncodedJWT accessToken) {
        return this;
    }

    public JWTBuilder withNonceGenerator(NonceGenerator nonceGenerator) {
        return this;
    }

    public JWTBuilder withTimestampGenerator(TimestampGenerator timestampGenerator) {
        return this;
    }

    public JWTBuilder withExpirationTimeInMs(long expirationTimeInMs) {
        return this;
    }

    public JWTBuilder withEncryptedServerPublicKey(SecretCredentialEncryptor encryptor, JWT serverPublicKey) {
        return this;
    }

    public Base64EncodedJWT build(JWT hMacKey) {
        return new Base64EncodedJWT() {
            @Override
            public JWT decode() {
                return null;
            }
        };
    }

}
