package de.adorsys.cse.jwt;

import java.util.Optional;

public interface JWT {

    /**
     * Encodes data as Base64 String according to RFC 7519
     * @return String that contains Base64 encoded token
     */
    String encode();

    /**
     * Returns the String value of a claim in the token's payload
     * @param claimName - name of a claim to be returned
     * @return String with a value of claim. Empty if a claim is not found
     */
    Optional<String> getClaim(String claimName);
}
