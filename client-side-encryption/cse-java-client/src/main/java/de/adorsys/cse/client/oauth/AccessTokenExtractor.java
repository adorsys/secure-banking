package de.adorsys.cse.client.oauth;

import de.adorsys.cse.jwt.Base64EncodedJWT;

public interface AccessTokenExtractor {
    Base64EncodedJWT extractAccessToken(Base64EncodedJWT oAuthToken);
}
