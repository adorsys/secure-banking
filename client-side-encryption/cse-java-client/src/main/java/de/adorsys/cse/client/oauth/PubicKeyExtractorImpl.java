package de.adorsys.cse.client.oauth;

import de.adorsys.cse.jwk.JWK;
import de.adorsys.cse.jwk.JWKNimbusImpl;
import de.adorsys.cse.jwt.JWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.Optional;

public class PubicKeyExtractorImpl implements PublicKeyExtractor {
    private static final Logger log = LoggerFactory.getLogger(PubicKeyExtractorImpl.class);
    private static final String PUBLIC_KEY_CLAIM = "res_pub_key";

    @Override
    public Optional<JWK> extractPublicKey(JWT token) {
        if (token == null) {
            log.error("Passed token is null");
            throw new IllegalArgumentException("Passed token is null");
        }
        Optional<String> claim = token.getClaim(PUBLIC_KEY_CLAIM);
        if (claim.isPresent()) {
            try {
                return Optional.of(new JWKNimbusImpl(claim.get()));
            }
            catch (ParseException e) {
                log.warn("Storred in token claim is invalid or corrupted: {}", e.getMessage());
            }
        }
        else {
            log.warn("No claim or empty claim \"{}\" found in token", PUBLIC_KEY_CLAIM);
        }
        return Optional.empty();
    }
}
