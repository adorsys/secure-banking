package de.adorsys.cse.jwt;

/**
 * Represents encrypted token
 */
public interface JWE {

    /**
     * Encodes data as Base64 URL-safe String according to RFC 7519
     *
     * @return String that contains Base64 encoded token
     */
    String encode();

}
