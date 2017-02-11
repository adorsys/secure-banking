package de.adorsys.cse.jwk;

import com.nimbusds.jose.jwk.RSAKey;
import org.apache.commons.codec.binary.Base64;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.DSAPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.IllegalFormatException;

public class JWKBuilderNimbusImpl implements JWKBuilder {

    private static final String PUBLIC_KEY_PREFIX = "-----BEGIN PUBLIC KEY-----";
    private static final String PUBLIC_KEY_SUFFIX = "-----END PUBLIC KEY-----";

    @Override
    public JWK build(String pemEncodedPublicKey) throws IllegalFormatException {
        if (pemEncodedPublicKey == null) {
            throw new IllegalArgumentException("pemEncodedPublicKey cannot be null");
        }

        String strippedKey = removePublicKeySignature(pemEncodedPublicKey);
        X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(Base64.decodeBase64(strippedKey));

        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            RSAPublicKey publicKey = (RSAPublicKey) keyFactory.generatePublic(pubKeySpec);
            return build(publicKey);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            //we failed with RSA, trying EC
            try {
                KeyFactory keyFactory = KeyFactory.getInstance("DSA");
                DSAPublicKey publicKey = (DSAPublicKey) keyFactory.generatePublic(pubKeySpec);
                return build(publicKey);
            }
            catch (NoSuchAlgorithmException | InvalidKeySpecException e1) {
                throw new IllegalArgumentException("Provided pemEncodedPublicKey is not a correct RSA/DSA public key: " + e1.getMessage());
            }
        }
    }

    @Override
    public JWK build(PublicKey publicKey) {
        if (publicKey == null) {
            throw new IllegalArgumentException("publicKey cannot be null");
        }
        if (JWK.Algorithm.RSA.name().equals(publicKey.getAlgorithm())) {
            return build((RSAPublicKey) publicKey);
        }

        throw new IllegalArgumentException("Unknown public key algorithm: " + publicKey.getAlgorithm());
    }

    private JWK build(RSAPublicKey rsaPublicKey) {
        RSAKey rsaKey = new RSAKey.Builder(rsaPublicKey).build();
        return new JWKNimbusImpl(rsaKey);
    }

    private String removePublicKeySignature(String sourceKeyString) {
        String sourceString = sourceKeyString.trim();

        if (sourceString.contains(PUBLIC_KEY_PREFIX)) {
            sourceString = sourceString.substring(sourceString.indexOf(PUBLIC_KEY_PREFIX) + PUBLIC_KEY_PREFIX.length());
        }

        if (sourceString.contains(PUBLIC_KEY_SUFFIX)) {
            sourceString = sourceString.substring(0, sourceString.indexOf(PUBLIC_KEY_SUFFIX));
        }
        return sourceString;
    }
}
