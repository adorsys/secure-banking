package de.adorsys.cse.client.oauth;


import de.adorsys.cse.jwt.JWT;

public interface AccessTokenExtractor {
    JWT extractAccessToken(JWT oAuthToken) throws AccessTokenExtractorException;
}
