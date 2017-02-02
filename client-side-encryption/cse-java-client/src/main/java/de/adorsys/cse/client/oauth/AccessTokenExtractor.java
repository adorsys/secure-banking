package de.adorsys.cse.client.oauth;


import de.adorsys.cse.jwt.Base64EncodedJWT;
import de.adorsys.cse.jwt.JWT;

public interface AccessTokenExtractor {
    JWT extractAccessToken(Base64EncodedJWT oAuthToken) throws AccessTokenExtractorException;
}
