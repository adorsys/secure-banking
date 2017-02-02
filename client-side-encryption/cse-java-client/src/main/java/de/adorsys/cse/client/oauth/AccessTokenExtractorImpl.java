package de.adorsys.cse.client.oauth;

import de.adorsys.cse.jwt.JWT;
import de.adorsys.cse.jwt.JWTNimbusImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;

public class AccessTokenExtractorImpl implements AccessTokenExtractor {
    private static final Logger log = LoggerFactory.getLogger(AccessTokenExtractorImpl.class);
    private static final String CLAIM_ACCESS_TOKEN = "access_token";

    @Override
    public JWT extractAccessToken(JWT oAuthToken) throws AccessTokenExtractorException {
        if (oAuthToken == null) {
            log.error("Passed oAuthToken is null");
            throw new IllegalArgumentException("oAuthToken cannot be null");
        }

        try {
            String accessToken = oAuthToken.getClaim(CLAIM_ACCESS_TOKEN);
            return new JWTNimbusImpl(accessToken);
        }
        catch (ParseException e) {
            log.error("Provided token {} doesn't contain claim {}", oAuthToken, CLAIM_ACCESS_TOKEN);
            throw new AccessTokenExtractorException("Provided token doesn't contain access_token");
        }
    }
}
