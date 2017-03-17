package de.adorsys.cse.example.client.crypto.bean;

public class JWEEncryptRequest {
    private String publicKeyJWK;
    private String publicKey;
    private String secretToEncrypt;

    public String getPublicKeyJWK() {
        return publicKeyJWK;
    }

    public void setPublicKeyJWK(String publicKeyJWK) {
        this.publicKeyJWK = publicKeyJWK;
    }


    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getSecretToEncrypt() {
        return secretToEncrypt;
    }

    public void setSecretToEncrypt(String secretToEncrypt) {
        this.secretToEncrypt = secretToEncrypt;
    }
}
