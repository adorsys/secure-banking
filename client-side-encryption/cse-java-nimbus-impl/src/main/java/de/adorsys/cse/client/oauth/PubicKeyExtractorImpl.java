package de.adorsys.cse.client.oauth;

import de.adorsys.cse.jwk.JWK;
import de.adorsys.cse.jwk.JWKNimbusImpl;
import de.adorsys.cse.jwt.JWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.Optional;

import static de.adorsys.cse.jwt.JWT.Claims.CLAIM_SERVER_PUBLIC_KEY;

public class PubicKeyExtractorImpl implements PublicKeyExtractor {
    private static final Logger log = LoggerFactory.getLogger(PubicKeyExtractorImpl.class);

    @Override
    public Optional<JWK> extractPublicKey(JWT token) {
        if (token == null) {
            log.error("Passed token is null");
            throw new IllegalArgumentException("Passed token is null");
        }
        Optional<String> claim = token.getClaim(CLAIM_SERVER_PUBLIC_KEY);
        if (claim.isPresent()) {
            try {
                return Optional.of(new JWKNimbusImpl(claim.get()));
            }
            catch (ParseException e) {
                log.warn("Storred in token claim is invalid or corrupted: {}", e.getMessage());
            }
        }
        else {
            log.warn("No claim or empty claim \"{}\" found in token", CLAIM_SERVER_PUBLIC_KEY);
        }
        return Optional.empty();
    }
}
