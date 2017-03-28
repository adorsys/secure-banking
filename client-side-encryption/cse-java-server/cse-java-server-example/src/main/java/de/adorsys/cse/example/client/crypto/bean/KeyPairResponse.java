package de.adorsys.cse.example.client.crypto.bean;

public class KeyPairResponse {
    private String privateKey;
    private String publicKey;
    private String publicKeyJWK;

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKeyJWK(String publicKeyJWK) {
        this.publicKeyJWK = publicKeyJWK;
    }

    public String getPublicKeyJWK() {
        return publicKeyJWK;
    }
}
