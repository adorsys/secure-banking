package de.adorsys.cse;

import de.adorsys.cse.client.oauth.AccessTokenExtractor;
import de.adorsys.cse.client.oauth.AccessTokenExtractorImpl;
import de.adorsys.cse.client.oauth.PubicKeyExtractorImpl;
import de.adorsys.cse.client.oauth.PublicKeyExtractor;
import de.adorsys.cse.jwt.JWTBuilder;
import de.adorsys.cse.jwt.JWTBuilderNimbusImpl;

public class NimbusClientFactory implements CseJwtClient {
    @Override
    public JWTBuilder jwtBuilder() {
        return new JWTBuilderNimbusImpl();
    }

    @Override
    public PublicKeyExtractor publicKeyExtractor() {
        return new PubicKeyExtractorImpl();
    }

    @Override
    public AccessTokenExtractor accessTokenExtractor() {
        return new AccessTokenExtractorImpl();
    }
}
