package de.adorsys.cse.jwk;

import java.security.PublicKey;

public interface JWK {
    String toJSONString();

    String toBase64JSONString();

    String getKeyType();

    PublicKey toRSAPublicKey() throws IllegalStateException;

    enum Algorythm {
        RSA(2048),
        ;

        private int keyLength;

        Algorythm(int keyLength) {
            this.keyLength = keyLength;
        }

        public int getKeyLength() {
            return keyLength;
        }
    }
}
