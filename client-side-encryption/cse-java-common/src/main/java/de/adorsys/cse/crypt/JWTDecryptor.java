package de.adorsys.cse.crypt;

import de.adorsys.cse.jwt.JWE;
import de.adorsys.cse.jwt.JWS;
import de.adorsys.cse.jwt.JWT;

public interface JWTDecryptor {
    JWS decryptSigned(JWE encrypted);

    JWT decryptUnsigned(JWE encrypted);
}
