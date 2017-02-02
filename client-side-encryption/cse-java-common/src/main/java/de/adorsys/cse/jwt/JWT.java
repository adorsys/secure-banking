package de.adorsys.cse.jwt;

public interface JWT {
    String PUBLIC_KEY_CLAIM = "res_pub_key";

    Base64EncodedJWT encode();

    String getResourcePublicKey();

    static JWTBuilder create() {
        return new JWTBuilderImpl();
    }
}
