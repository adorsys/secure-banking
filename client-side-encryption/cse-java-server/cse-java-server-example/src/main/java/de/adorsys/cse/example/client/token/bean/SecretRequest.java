package de.adorsys.cse.example.client.token.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "A set of parameters, that can be used in token")
public class SecretRequest {
    private String secret;
    private String hmacSecret;
    private Long expirationTimeMs;

    @ApiModelProperty("some secret value to transfer")
    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    @ApiModelProperty("a secret used to sign a key with hmac")
    public String getHmacSecret() {
        return hmacSecret;
    }

    public void setHmacSecret(String hmacSecret) {
        this.hmacSecret = hmacSecret;
    }

    @ApiModelProperty("token expiration time in milliseconds")
    public Long getExpirationTimeMs() {
        return expirationTimeMs;
    }

    public void setExpirationTimeMs(Long expirationTimeMs) {
        this.expirationTimeMs = expirationTimeMs;
    }
}
