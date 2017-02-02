package de.adorsys.cse.jwk;

public interface JWKBuilder {
    JWK build(String pemEncodedKey);
}
