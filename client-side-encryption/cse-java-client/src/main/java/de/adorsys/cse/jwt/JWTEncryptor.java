package de.adorsys.cse.jwt;

public interface JWTEncryptor {
    JWE encrypt(JWT jwt);

    JWE encrypt(JWS signedJwt);

    JWS decryptSigned(JWE encrypted);

    JWT decryptUnsigned(JWE encrypted);
}
