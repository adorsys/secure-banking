package de.adorsys.cse.jwk;

import java.security.PublicKey;
import java.util.IllegalFormatException;

public interface JWKBuilder {
    JWK build(String pemEncodedPublicKey) throws IllegalFormatException;

    JWK build(PublicKey publicKey);
}
