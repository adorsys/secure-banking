package de.adorsys.cse;

import de.adorsys.cse.client.oauth.AccessTokenExtractor;
import de.adorsys.cse.client.oauth.PublicKeyExtractor;
import de.adorsys.cse.jwt.JWTBuilder;

public interface CseJwtClient {
    static CseJwtClient init() throws ClassNotFoundException {
        CseJwtClient factory;
        try {
            Class c = Class.forName("de.adorsys.cse.NimbusClientFactory");
            factory = (CseJwtClient) c.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new ClassNotFoundException("Can't instantiate of any known implementations", e);
        }
        return factory;
    }

    JWTBuilder jwtBuilder();

    PublicKeyExtractor publicKeyExtractor();

    AccessTokenExtractor accessTokenExtractor();
}
