package de.adorsys.cse.client.oauth;


import de.adorsys.cse.jwt.JWT;

import java.util.Optional;

public interface AccessTokenExtractor {
    Optional<JWT> extractAccessToken(JWT oAuthToken);
}
