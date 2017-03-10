package de.adorsys.cse.jwk;

import java.security.PublicKey;

public interface JWK {
    String toJSONString();

    String toBase64JSONString();

    String getKeyType();

    PublicKey toRSAPublicKey() throws IllegalStateException;

    enum Algorythm {
        RSA_512("RSA", 512),
        RSA_1024("RSA", 1024),
        RSA_2048("RSA", 2048),
        ;

        private final String algName;
        private int keyLength;

        Algorythm(String algName, int keyLength) {
            this.algName = algName;
            this.keyLength = keyLength;
        }

        public int getKeyLength() {
            return keyLength;
        }

        public String getAlgName() {
            return algName;
        }
    }
}
