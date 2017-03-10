package de.adorsys.cse.crypt;

import com.nimbusds.jose.util.Base64;

import java.util.Random;

class CryptoPair {
    private byte[] data;
    private boolean dataEncrypted = false;

    private byte[] symmetricKey;
    private boolean keyEncrypted = false;

    CryptoPair(byte[] data, int keyLength) {
        this.data = data;
        this.symmetricKey = generateSymmetricKey(keyLength);
    }


    CryptoPair(String base64EncodedString) {
        String[] parts = base64EncodedString.split("\\.");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Provided encoded string doesn't contain 2 parts splitted by .");
        }
        Base64 encodedKey = new Base64(parts[0]);
        Base64 encodedData = new Base64(parts[1]);
        this.data = encodedData.decode();
        this.symmetricKey = encodedKey.decode();
    }

    CryptoPair(CryptoPair input, byte[] encryptedData) {
        this.symmetricKey = input.symmetricKey;
        this.keyEncrypted = input.keyEncrypted;
        this.data = encryptedData;
        this.dataEncrypted = true;
    }

    byte[] getData() {
        return data;
    }

    public boolean isDataEncrypted() {
        return dataEncrypted;
    }

    public boolean isKeyEncrypted() {
        return keyEncrypted;
    }

    byte[] getSymmetricKey() {
        return symmetricKey;
    }

    void setEncryptedData(byte[] encryptedData) {
        this.data = encryptedData;
        this.dataEncrypted = true;
    }

    void setEncryptedKey(byte[] encryptedKey) {
        this.symmetricKey = encryptedKey;
        this.keyEncrypted = true;
    }

    String encodeToBase64() {
        return Base64.encode(symmetricKey) + "." + Base64.encode(data);
    }

    private static byte[] generateSymmetricKey(int keyLength) {
        Random randomno = new Random();
        byte[] nbyte = new byte[keyLength];
        randomno.nextBytes(nbyte);
        return nbyte;
    }
}
