package de.adorsys.cse.jwt;

import de.adorsys.cse.crypt.SecretCredentialEncryptor;
import de.adorsys.cse.nonce.NonceGenerator;

import java.io.InvalidObjectException;

public interface JWTBuilder {
    /**
     * Checks if the provided token is valid and if yes, includes the claim "access_token" to the resulting token's payload
     *
     * @param accessToken - access token to be put into resulting token
     * @return an instance of builder with included access token
     */
    JWTBuilder withAccessToken(JWT accessToken);

    /**
     * Stores the generator which will be used for JWT ID claim on the token buildAndSign invokation
     *
     * @param nonceGenerator - a generator to be used to generate id
     * @return an instance of builder with stored nonce generator
     */
    JWTBuilder withNonceGenerator(NonceGenerator nonceGenerator);

    /**
     * Sets the expiration time of token from a moment of token building (by invoking any buildAndSign method)
     *
     * @param expirationTimeInMs - expiration time in milliseconds
     * @return an instance of builder with stored token expiration time
     */
    JWTBuilder withExpirationTimeInMs(long expirationTimeInMs);

    /**
     * Stores encrypted hmacSecret in the token
     *
     * @param encryptor - an encryptor instance that will encrypt the secret
     * @param hmacSecret - a secret to transfer
     * @return an instance of builder with stored encrypted secret
     */
    JWTBuilder withEncryptedHMacSecretKey(SecretCredentialEncryptor encryptor, String hmacSecret);

    /**
     * Stores in payload a JSON-serialized object
     *
     * @param payload - a payload object to store
     * @return an instance of builder with stored JSON-serialized payload
     * @throws java.io.InvalidObjectException when object' serializaion to JSON failed
     */
    JWTBuilder withPayload(Object payload) throws InvalidObjectException;

    /**
     * Stores in payload a JSON-serialized object in defined claim
     *
     * @param claim name of claim to store the payload
     * @param payload a payload object to store
     * @return an instance of builder with stored JSON-serialized payload
     * @throws java.io.InvalidObjectException when object' serializaion to JSON failed
     */
    JWTBuilder withPayload(String claim, Object payload) throws InvalidObjectException;

    /**
     * Builds JWT and signs it with HMAC Alghorythm.
     * Secret HMAC Key must be shared with recipient before.
     * To do it include and encrypt secret to the first token you send to server with {@link #withEncryptedHMacSecretKey(SecretCredentialEncryptor, String) withEncryptedHMacSecretKey}
     *
     * @param hmacSecret - a secret used to sign token
     * @return builded JWT with HMAC signature
     */
    JWT buildAndSign(String hmacSecret);

    /**
     * Builds unsigned JWT
     *
     * @return builded JWT
     */
    JWT build();
}
