package de.adorsys.cse.jwk;

import java.security.PublicKey;
import java.text.ParseException;
import java.util.IllegalFormatException;

public interface JWKPublicKeyBuilder {
    JWK build(String pemEncodedPublicKey) throws IllegalFormatException;

    JWK build(PublicKey publicKey);

    JWK buildFromBase64EncodedJWK(String base64encodedJWK) throws ParseException;
}
