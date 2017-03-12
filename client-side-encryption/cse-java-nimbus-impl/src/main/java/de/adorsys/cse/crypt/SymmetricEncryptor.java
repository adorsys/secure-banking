package de.adorsys.cse.crypt;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

class SymmetricEncryptor {
    private static final String CIPHER_NAME = "AES";

    CryptoPair encrypt(byte[] secret) {
        try {
            final Cipher symmetricCipher = Cipher.getInstance(CIPHER_NAME);
            CryptoPair input = new CryptoPair(secret, 128);
            Key secretKey = new SecretKeySpec(input.getSymmetricKey(), CIPHER_NAME);
            symmetricCipher.init(Cipher.ENCRYPT_MODE, secretKey);
            final byte[] encryptedData = symmetricCipher.doFinal(input.getData());

            return new CryptoPair(input, encryptedData);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new IllegalStateException("JVM does not support the '" + CIPHER_NAME + "' cipher");
        } catch (InvalidKeyException e) {
            throw new IllegalStateException("All JMs are required to support DESede keys", e);
        } catch (BadPaddingException | IllegalBlockSizeException e) {
            throw new IllegalStateException("Failed to encrypt data with random symmetric key", e);
        }
    }

    byte[] decrypt(CryptoPair encryptedSecret) {
        try {
            final Cipher symmetricCipher = Cipher.getInstance(CIPHER_NAME);
            Key secretKey = new SecretKeySpec(encryptedSecret.getSymmetricKey(), CIPHER_NAME);
            symmetricCipher.init(Cipher.DECRYPT_MODE, secretKey);
            return symmetricCipher.doFinal(encryptedSecret.getData());
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | IllegalBlockSizeException e) {
            throw new IllegalStateException("JVM does not support the '" + CIPHER_NAME + "' cipher");
        } catch (BadPaddingException e) {
            throw new IllegalStateException("Failed to decrypt data", e);
        } catch (InvalidKeyException e) {
            throw new IllegalStateException("Failed to initialise cipher", e);
        }
    }
}
