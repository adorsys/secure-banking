package de.adorsys.cse.client.oauth;

import de.adorsys.cse.jwt.JWT;
import de.adorsys.cse.jwt.JWTNimbusImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.Optional;

public class AccessTokenExtractorImpl implements AccessTokenExtractor {
    private static final Logger log = LoggerFactory.getLogger(AccessTokenExtractorImpl.class);
    private static final String CLAIM_ACCESS_TOKEN = "access_token";

    @Override
    public Optional<JWT> extractAccessToken(JWT oAuthToken) {
        if (oAuthToken == null) {
            log.error("Passed oAuthToken is null");
            throw new IllegalArgumentException("oAuthToken cannot be null");
        }

        Optional<String> accessToken = oAuthToken.getClaim(CLAIM_ACCESS_TOKEN);
        if (accessToken.isPresent()) {
            try {
                return Optional.of(new JWTNimbusImpl(accessToken.get()));
            } catch (ParseException e) {
                log.error("Returned claim \"{}\" is not a valid JWT token", accessToken.get(), e);
            }
        }
        else {
            log.warn("Provided token {} doesn't contain claim \"{}\"", oAuthToken, CLAIM_ACCESS_TOKEN);
        }
        return Optional.empty();
    }
}
