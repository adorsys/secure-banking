package de.adorsys.cse.example.client.crypto.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("Request to encrypt some secret into JWT with RSA Public key")
public class JWEEncryptRequest {
    private String publicKeyJWK;
    private String publicKey;
    private String secretToEncrypt;

    @ApiModelProperty("A public key in base64-encoded JWK format")
    public String getPublicKeyJWK() {
        return publicKeyJWK;
    }

    public void setPublicKeyJWK(String publicKeyJWK) {
        this.publicKeyJWK = publicKeyJWK;
    }


    @ApiModelProperty("A public key in base64-encoded X.509 format")
    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    @ApiModelProperty("Some secret to encrypt")
    public String getSecretToEncrypt() {
        return secretToEncrypt;
    }

    public void setSecretToEncrypt(String secretToEncrypt) {
        this.secretToEncrypt = secretToEncrypt;
    }
}
