package de.adorsys.cse.example.server.token.bean;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TokenStatusResponse {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    private Boolean signatureValid;

    private boolean tokenExpired;

    private String tokenExpirationTime;

    private Map<String, Object> tokenClaims;


    public boolean isSignatureValid() {
        return signatureValid;
    }

    public void setSignatureValid(boolean signatureValid) {
        this.signatureValid = signatureValid;
    }

    public boolean isTokenExpired() {
        return tokenExpired;
    }

    public void setTokenExpired(boolean tokenExpired) {
        this.tokenExpired = tokenExpired;
    }

    public String getTokenExpirationTime() {
        return tokenExpirationTime;
    }

    public void setTokenExpirationTime(Date tokenExpirationTime) {
        this.tokenExpirationTime = dateFormat.format(tokenExpirationTime);
    }

    public Map<String, Object> getTokenClaims() {
        return tokenClaims;
    }

    public void setTokenClaims(Map<String, Object> tokenClaims) {
        this.tokenClaims = tokenClaims;
    }
}
