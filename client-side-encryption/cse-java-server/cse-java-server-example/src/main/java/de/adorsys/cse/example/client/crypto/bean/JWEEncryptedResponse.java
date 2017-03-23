package de.adorsys.cse.example.client.crypto.bean;

public class JWEEncryptedResponse {
    private String encryptedJWT;

    public void setEncryptedJWT(String encryptedJWK) {
        this.encryptedJWT = encryptedJWK;
    }

    public String getEncryptedJWT() {
        return encryptedJWT;
    }
}
