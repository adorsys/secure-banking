package de.adorsys.cse.crypt;

import com.sun.crypto.provider.RSACipher;
import de.adorsys.cse.jwk.JWK;

import javax.crypto.Cipher;

class RSAEncryptor {
    CryptoPair encryptSecretKey(CryptoPair input, JWK publicKey) {
        try {
            // get an RSA cipher object and print the provider
            final Cipher cipher = Cipher.getInstance("RSA");
            new RSACipher();
            // encrypt the plain text using the public key
            cipher.init(Cipher.ENCRYPT_MODE, publicKey.toRSAPublicKey());
System.out.println("Length: " + input.getSymmetricKey().length);
            byte[] cipherText = cipher.doFinal(input.getSymmetricKey());
            input.setEncryptedKey(cipherText);
            return input;
        } catch (Exception e) {
            throw new IllegalStateException("Cannot encrypt with provided JWK public key, using RSA Algorythm", e);
        }

    }
}
