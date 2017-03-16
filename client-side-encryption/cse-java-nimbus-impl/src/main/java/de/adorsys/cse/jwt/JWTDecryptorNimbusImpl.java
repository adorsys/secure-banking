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

    private final PrivateKey privateKey;

    public JWTDecryptorNimbusImpl(PrivateKey privateKey) {
        if (privateKey == null) {
            throw new IllegalArgumentException("privateKey cannot be null");
        }
        if (!"RSA".equals(privateKey.getAlgorithm())) {
            throw new IllegalArgumentException("currently only RSA-keys are supported");
        }

        this.privateKey = privateKey;
    }

    @Override
    public JWS decryptSigned(JWE encrypted) throws JWTEncryptionException {
        if (encrypted == null) {
            throw new IllegalArgumentException("encrypted cannot be null");
        }
        EncryptedJWT encryptedJWT;
        try {
            encryptedJWT = EncryptedJWT.parse(encrypted.encode());
        }
        catch (ParseException e) {
            throw new JWTEncryptionException("Provided JWE is incorrect: " + e.getMessage());
        }

        // Create a decrypter with the specified private RSA key
        RSADecrypter decrypter = new RSADecrypter(privateKey);

        // Decrypt
        try {
            encryptedJWT.decrypt(decrypter);
        } catch (JOSEException e) {
            throw new JWTEncryptionException("Error decrypting provided JWE object: " + e.getMessage());
        }
        Payload payload = encryptedJWT.getPayload();

        String serialize = payload.toSignedJWT().serialize();
        try {
            return new JWSNimbusImpl(serialize);
        } catch (ParseException e) {
            throw new JWTEncryptionException("Encrypted object is not a valid signed JWT object: " + e.getMessage());
        }
    }

    @Override
    public JWT decryptUnsigned(JWE encrypted) throws JWTEncryptionException {
        if (encrypted == null) {
            throw new IllegalArgumentException("encrypted cannot be null");
        }
        EncryptedJWT encryptedJWT;
        try {
            encryptedJWT = EncryptedJWT.parse(encrypted.encode());
        }
        catch (ParseException e) {
            throw new JWTEncryptionException("Provided JWE is incorrect: " + e.getMessage());
        }

        // Create a decrypter with the specified private RSA key
        RSADecrypter decrypter = new RSADecrypter(privateKey);

        // Decrypt
        try {
            encryptedJWT.decrypt(decrypter);
        } catch (JOSEException e) {
            throw new JWTEncryptionException("Error decrypting provided JWE object: " + e.getMessage());
        }

        Payload payload = encryptedJWT.getPayload();
        try {
            JWTClaimsSet claimsSet = JWTClaimsSet.parse(payload.toJSONObject());
            return new JWTNimbusImpl(claimsSet);
        } catch (ParseException e) {
            throw new JWTEncryptionException("Encrypted object is not a valid JWT object: " + e.getMessage());
        }
    }
}
