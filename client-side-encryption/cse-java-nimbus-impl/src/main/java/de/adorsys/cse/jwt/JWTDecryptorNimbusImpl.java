package de.adorsys.cse.jwt;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.RSADecrypter;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWTClaimsSet;
import de.adorsys.cse.crypt.JWTDecryptor;
import de.adorsys.cse.crypt.JWTEncryptionException;

import java.security.PrivateKey;
import java.text.ParseException;

public class JWTDecryptorNimbusImpl implements JWTDecryptor {

    private final RSADecrypter decrypter;

    public JWTDecryptorNimbusImpl(PrivateKey privateKey) {
        if (privateKey == null) {
            throw new IllegalArgumentException("privateKey cannot be null");
        }
        if (!"RSA".equals(privateKey.getAlgorithm())) {
            throw new IllegalArgumentException("currently only RSA-keys are supported");
        }

        decrypter = new RSADecrypter(privateKey);
    }

    @Override
    public JWS decryptSigned(JWE encrypted) throws JWTEncryptionException {
        if (encrypted == null) {
            throw new IllegalArgumentException("encrypted cannot be null");
        }

        return decryptSigned(encrypted.encode());
    }

    @Override
    public JWT decryptUnsigned(JWE encrypted) throws JWTEncryptionException {
        if (encrypted == null) {
            throw new IllegalArgumentException("encrypted cannot be null");
        }
        return decryptUnsigned(encrypted.encode());
    }

    @Override
    public JWS decryptSigned(String encryptedBase64) throws JWTEncryptionException {
        if (encryptedBase64 == null) {
            throw new IllegalArgumentException("encryptedBase64 cannot be null");
        }
        Payload payload = decryptPayload(encryptedBase64);

        String serialize = payload.toSignedJWT().serialize();
        try {
            return new JWSNimbusImpl(serialize);
        } catch (ParseException e) {
            throw new JWTEncryptionException("Encrypted object is not a valid signed JWT object: " + e.getMessage());
        }
    }

    @Override
    public JWT decryptUnsigned(String encryptedBase64) throws JWTEncryptionException {
        if (encryptedBase64 == null) {
            throw new IllegalArgumentException("encryptedBase64 cannot be null");
        }

        Payload payload = decryptPayload(encryptedBase64);
        try {
            JWTClaimsSet claimsSet = JWTClaimsSet.parse(payload.toJSONObject());
            return new JWTNimbusImpl(claimsSet);
        } catch (ParseException e) {
            throw new JWTEncryptionException("Encrypted object is not a valid JWT object: " + e.getMessage());
        }

    }

    private Payload decryptPayload(String encryptedBase64) throws JWTEncryptionException {
        EncryptedJWT encryptedJWT;
        try {
            encryptedJWT = EncryptedJWT.parse(encryptedBase64);
        }
        catch (ParseException e) {
            throw new JWTEncryptionException("Provided JWE is incorrect: " + e.getMessage());
        }

        // Decrypt
        try {
            encryptedJWT.decrypt(decrypter);
        } catch (JOSEException e) {
            throw new JWTEncryptionException("Error decrypting provided JWE object: " + e.getMessage());
        }

        return encryptedJWT.getPayload();
    }
}
