package de.adorsys.cse.example.util;

import org.apache.commons.codec.binary.Base64;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class KeysSerializer {
    private KeysSerializer() {
    }

    public static String publicKeyToBase64String(PublicKey publicKey) {
        byte[] keyInBytes = publicKey.getEncoded();
        return Base64.encodeBase64URLSafeString(keyInBytes);
    }

    public static String privateKeyToBase64String(PrivateKey privateKey) {
        byte[] keyInBytes = privateKey.getEncoded();
        return Base64.encodeBase64URLSafeString(keyInBytes);
    }

    public static PublicKey base64StringToPublicKey(String base64) throws Exception {
        byte[] keyInBytes = Base64.decodeBase64(base64);

        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyInBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(spec);
    }

    public static PrivateKey base64StringToPrivateKey(String base64) throws Exception {
        byte[] keyInBytes = Base64.decodeBase64(base64);

        PKCS8EncodedKeySpec specPriv = new PKCS8EncodedKeySpec(keyInBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(specPriv);
    }
}
