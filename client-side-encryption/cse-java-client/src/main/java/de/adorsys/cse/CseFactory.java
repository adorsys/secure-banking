package de.adorsys.cse;

import de.adorsys.cse.client.oauth.AccessTokenExtractor;
import de.adorsys.cse.client.oauth.PublicKeyExtractor;
import de.adorsys.cse.crypt.JWTDecryptor;
import de.adorsys.cse.jwk.JWK;
import de.adorsys.cse.jwk.JWKPublicKeyBuilder;
import de.adorsys.cse.jwt.JWT;
import de.adorsys.cse.jwt.JWTBuilder;
import de.adorsys.cse.crypt.JWTEncryptor;
import de.adorsys.cse.jwt.JWTSigner;

import java.security.PrivateKey;
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

    JWKPublicKeyBuilder jwkPublicKeyBuilder();

    JWTBuilder jwtBuilder();

    PublicKeyExtractor publicKeyExtractor();

    AccessTokenExtractor accessTokenExtractor();

    JWTSigner jwtHMacSigner();

    JWTEncryptor jwtEncryptor(JWK publicKey);

    JWTDecryptor jwtDecryptor(PrivateKey privateKey);

    JWT parseToken(String base64encodedToken) throws ParseException;
}
