package de.adorsys.cse.crypt;

import de.adorsys.cse.jwt.JWE;
import de.adorsys.cse.jwt.JWS;
import de.adorsys.cse.jwt.JWT;

public interface JWTEncryptor {
    JWE encrypt(JWT jwt) throws JWTEncryptionException;

    JWE encrypt(JWS signedJwt) throws JWTEncryptionException;
}
