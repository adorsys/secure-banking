package de.adorsys.cse.jwk;


import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;

public class JWKNimbusImpl implements JWK {
    private static final Logger log = LoggerFactory.getLogger(JWKNimbusImpl.class);

    private String originalUnencodedJWK;
    private com.nimbusds.jose.jwk.JWK container;

    public JWKNimbusImpl(String JWKOrBase64encodedJWK) throws ParseException {
        if (JWKOrBase64encodedJWK == null) {
            log.error("Passed JWKOrBase64encodedJWK is null");
            throw new IllegalArgumentException("Passed JWKOrBase64encodedJWK is null");
        }
        try {
            this.container = com.nimbusds.jose.jwk.JWK.parse(JWKOrBase64encodedJWK);
            this.originalUnencodedJWK = JWKOrBase64encodedJWK;
        } catch (ParseException e) {
            //Failed first time, maybe the input is Base64-encoded ?
            try {
                byte[] decoded = Base64.decodeBase64(JWKOrBase64encodedJWK);
                String decodedJWK = new String(decoded, "UTF-8");

                this.container = com.nimbusds.jose.jwk.JWK.parse(decodedJWK);
                //if we failed here, the given key is not a valid JWK, ParseException goes out
                this.originalUnencodedJWK = decodedJWK;

            } catch (IllegalArgumentException | UnsupportedEncodingException ex) {
                //it's not a base64-encoded string
                throw new ParseException("Provided string is neither correct JSON JWK object not base64-encoded", 0);
            }
        }
    }

    @Override
    public String toJSONString() {
        return originalUnencodedJWK;
    }

    @Override
    public String toBase64JSONString() {
        return Base64.encodeBase64String(originalUnencodedJWK.getBytes());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JWKNimbusImpl jwkNimbus = (JWKNimbusImpl) o;

        return originalUnencodedJWK.equals(jwkNimbus.originalUnencodedJWK);
    }

    @Override
    public int hashCode() {
        return originalUnencodedJWK.hashCode();
    }
}
