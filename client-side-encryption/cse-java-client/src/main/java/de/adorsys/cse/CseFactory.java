package de.adorsys.cse;

import de.adorsys.cse.client.oauth.AccessTokenExtractor;
import de.adorsys.cse.client.oauth.PublicKeyExtractor;
import de.adorsys.cse.jwk.JWK;
import de.adorsys.cse.jwt.JWT;
import de.adorsys.cse.jwt.JWTBuilder;
import de.adorsys.cse.crypt.JWTEncryptor;
import de.adorsys.cse.jwt.JWTSigner;

import java.text.ParseException;

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

    JWTEncryptor jwtEncryptor(JWK publicKey);

    JWT parseToken(String base64encodedToken) throws ParseException;
}
