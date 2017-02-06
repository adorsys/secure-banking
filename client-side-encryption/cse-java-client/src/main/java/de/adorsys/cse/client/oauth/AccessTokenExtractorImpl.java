package de.adorsys.cse.client.oauth;

import de.adorsys.cse.jwt.Base64EncodedJWT;
import de.adorsys.cse.jwt.Base64EncodedJWTNimbusImpl;
import de.adorsys.cse.jwt.JWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;

public class AccessTokenExtractorImpl implements AccessTokenExtractor {
    private static final Logger log = LoggerFactory.getLogger(AccessTokenExtractorImpl.class);
    private static final String CLAIM_ACCESS_TOKEN = "access_token";

    @Override
    public JWT extractAccessToken(Base64EncodedJWT oAuthToken) throws AccessTokenExtractorException {
        try {
            JWT oAuthDecodedToken = oAuthToken.decode();
            Base64EncodedJWT accessToken = new Base64EncodedJWTNimbusImpl(oAuthDecodedToken.getClaim(CLAIM_ACCESS_TOKEN));
            return accessToken.decode();
        }
        catch (ParseException e) {
            log.error("Provided token {} doesn't contain claim {}", oAuthToken, CLAIM_ACCESS_TOKEN);
            throw new AccessTokenExtractorException("Provided token doesn't contain access_token");
        }
    }
}
