package de.adorsys.cse.example.server.token.bean;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Date;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TokenStatusResponse {

    private Boolean signatureValid;

    private boolean tokenExpired;

    private Date tokenExpirationTime;

    private Map<String, String> tokenClaims;


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

    public Date getTokenExpirationTime() {
        return tokenExpirationTime;
    }

    public void setTokenExpirationTime(Date tokenExpirationTime) {
        this.tokenExpirationTime = tokenExpirationTime;
    }

    public Map<String, String> getTokenClaims() {
        return tokenClaims;
    }

    public void setTokenClaims(Map<String, String> tokenClaims) {
        this.tokenClaims = tokenClaims;
    }
}
