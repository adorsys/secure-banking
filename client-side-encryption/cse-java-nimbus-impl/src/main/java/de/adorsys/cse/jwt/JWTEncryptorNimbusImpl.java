package de.adorsys.cse.jwt;

public class JWTEncryptorNimbusImpl implements JWTEncryptor {
    @Override
    public JWE encrypt(JWT jwt) {
        return null;
    }

    @Override
    public JWE encrypt(JWS signedJwt) {
        return encrypt((JWT)signedJwt);
    }

    @Override
    public JWS decryptSigned(JWE encrypted) {
        return null;
    }

    @Override
    public JWT decryptUnsigned(JWE encrypted) {
        return null;
    }
}
