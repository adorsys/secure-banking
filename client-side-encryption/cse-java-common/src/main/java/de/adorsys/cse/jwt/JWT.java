package de.adorsys.cse.jwt;

import de.adorsys.cse.jwk.JWK;

import java.text.ParseException;

public interface JWT {
    String PUBLIC_KEY_CLAIM = "res_pub_key";

    Base64EncodedJWT encode();

    JWK getResourcePublicKey() throws ParseException;

    String getClaim(String claimName) throws ParseException;
}
