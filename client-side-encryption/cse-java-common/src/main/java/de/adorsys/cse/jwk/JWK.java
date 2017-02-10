package de.adorsys.cse.jwk;

public interface JWK {
    String toJSONString();

    String toBase64JSONString();

    enum Algorithm {
        RSA,
    }
}
