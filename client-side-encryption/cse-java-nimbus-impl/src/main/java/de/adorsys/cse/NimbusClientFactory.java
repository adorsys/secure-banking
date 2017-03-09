package de.adorsys.cse;

import de.adorsys.cse.client.oauth.AccessTokenExtractor;
import de.adorsys.cse.client.oauth.AccessTokenExtractorImpl;
import de.adorsys.cse.client.oauth.PubicKeyExtractorImpl;
import de.adorsys.cse.client.oauth.PublicKeyExtractor;
import de.adorsys.cse.jwt.*;

import java.text.ParseException;

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
        return new JWTSignerNimbusImpl();
    }

    @Override
    public JWTEncryptor jwtEncryptor() {
        return null;
    }

    @Override
    public JWT parseToken(String base64encodedToken) throws ParseException {
        if (base64encodedToken == null || base64encodedToken.length() == 0) {
            throw new IllegalArgumentException("base64encodedToken cannot be null or empty");
        }

        if (base64encodedToken.split("\\.").length == 3) {
            return new JWSNimbusImpl(base64encodedToken);
        }
        return new JWTNimbusImpl(base64encodedToken);
    }
}
