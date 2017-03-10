package de.adorsys.cse.jwk;

import java.security.PublicKey;
import java.util.IllegalFormatException;

public interface JWKPublicKeyBuilder {
    JWK build(String pemEncodedPublicKey) throws IllegalFormatException;

    JWK build(PublicKey publicKey);
}
