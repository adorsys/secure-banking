package de.adorsys.cse;

import de.adorsys.cse.client.oauth.AccessTokenExtractor;
import de.adorsys.cse.client.oauth.PublicKeyExtractor;
import de.adorsys.cse.jwt.JWTBuilder;
import de.adorsys.cse.jwt.JWTEncryptor;
import de.adorsys.cse.jwt.JWTSigner;

public interface CseFactory {
    static CseFactory init() throws ClassNotFoundException {
        CseFactory factory;
        try {
            Class c = Class.forName("de.adorsys.cse.NimbusClientFactory");
            factory = (CseFactory) c.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new ClassNotFoundException("Can't instantiate of any known implementations", e);
        }
        return factory;
    }

    JWTBuilder jwtBuilder();

    PublicKeyExtractor publicKeyExtractor();

    AccessTokenExtractor accessTokenExtractor();

    JWTSigner jwtHMacSigner();

    JWTEncryptor jwtEncryptor();
}
