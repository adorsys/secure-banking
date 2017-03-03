package de.adorsys.cse;

import de.adorsys.cse.client.oauth.AccessTokenExtractor;
import de.adorsys.cse.client.oauth.AccessTokenExtractorImpl;
import de.adorsys.cse.client.oauth.PubicKeyExtractorImpl;
import de.adorsys.cse.client.oauth.PublicKeyExtractor;
import de.adorsys.cse.jwt.JWTBuilder;
import de.adorsys.cse.jwt.JWTBuilderNimbusImpl;
import de.adorsys.cse.jwt.JWTEncryptor;
import de.adorsys.cse.jwt.JWTSigner;

public class NimbusClientFactory implements CseFactory {
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

    @Override
    public JWTSigner jwtHMacSigner() {
        return null;
    }

    @Override
    public JWTEncryptor jwtEncryptor() {
        return null;
    }
}
