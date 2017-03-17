package de.adorsys.cse.example.client.crypto.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("Request to decrypt a secret transferred by RSA-encrypted JWT-token")
public class JWEDecryptRequest {
    private String encryptedJWT;
    private String privateKey;

    @ApiModelProperty("Encrypted JWT with some secrets")
    public String getEncryptedJWT() {
        return encryptedJWT;
    }

    public void setEncryptedJWT(String encryptedJWT) {
        this.encryptedJWT = encryptedJWT;
    }

    @ApiModelProperty("Private key in base64-encoded X.509 format")
    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }
}
