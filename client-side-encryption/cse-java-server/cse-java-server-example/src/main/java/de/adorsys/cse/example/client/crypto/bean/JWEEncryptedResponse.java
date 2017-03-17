package de.adorsys.cse.example.client.crypto.bean;

public class JWEEncryptedResponse {
    private String encryptedJWK;

    public void setEncryptedJWK(String encryptedJWK) {
        this.encryptedJWK = encryptedJWK;
    }

    public String getEncryptedJWK() {
        return encryptedJWK;
    }
}
