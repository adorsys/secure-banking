package de.adorsys.cse.jwt;

public interface JWTSigner {
    byte[] generateHMacSecret();

    JWS sign(JWT jwt, String hmacSecret);

    boolean verify(JWS jwt, String hmacSecret);
}
