package de.adorsys.cse.jwt;

import java.text.ParseException;

public interface JWT {
    String PUBLIC_KEY_CLAIM = "res_pub_key";

    Base64EncodedJWT encode();

    String getResourcePublicKey() throws ParseException;

    String getClaim(String claimName) throws ParseException;
}
