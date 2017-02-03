package de.adorsys.cse.client.oauth;

import de.adorsys.cse.jwk.JWK;
import de.adorsys.cse.jwt.JWT;

import java.util.Optional;

public interface PublicKeyExtractor {
    Optional<JWK> extractPublicKey(JWT token);
}
