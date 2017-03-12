package de.adorsys.cse.jwk;


import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.RSAKey;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.security.PublicKey;
import java.text.ParseException;

public class JWKNimbusImpl implements JWK {
    private static final Logger log = LoggerFactory.getLogger(JWKNimbusImpl.class);

    private String unencodedJWK;
    private com.nimbusds.jose.jwk.JWK container;

    JWKNimbusImpl(com.nimbusds.jose.jwk.JWK container) {
        this.container = container;
        this.unencodedJWK = container.toJSONString();
    }

    public JWKNimbusImpl(String JWKOrBase64encodedJWK) throws ParseException {
        if (JWKOrBase64encodedJWK == null) {
            log.error("Passed JWKOrBase64encodedJWK is null");
            throw new IllegalArgumentException("Passed JWKOrBase64encodedJWK is null");
        }
        try {
            this.container = com.nimbusds.jose.jwk.JWK.parse(JWKOrBase64encodedJWK);
            this.unencodedJWK = JWKOrBase64encodedJWK;
        } catch (ParseException e) {
            //Failed first time, maybe the input is Base64-encoded ?
            try {
                byte[] decoded = Base64.decodeBase64(JWKOrBase64encodedJWK);
                String decodedJWK = new String(decoded, "UTF-8");

                this.container = com.nimbusds.jose.jwk.JWK.parse(decodedJWK);
                //if we failed here, the given key is not a valid JWK, ParseException goes out
                this.unencodedJWK = decodedJWK;

            } catch (IllegalArgumentException | UnsupportedEncodingException ex) {
                //it's not a base64-encoded string
                throw new ParseException("Provided string is neither correct JSON JWK object not base64-encoded", 0);
            }
        }
    }

    @Override
    public String toJSONString() {
        return unencodedJWK;
    }

    @Override
    public String toBase64JSONString() {
        return Base64.encodeBase64URLSafeString(unencodedJWK.getBytes());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JWKNimbusImpl jwkNimbus = (JWKNimbusImpl) o;

        return unencodedJWK.equals(jwkNimbus.unencodedJWK);
    }

    @Override
    public int hashCode() {
        return unencodedJWK.hashCode();
    }

    @Override
    public String getKeyType() {
        return container.getKeyType().getValue().toUpperCase();
    }

    @Override
    public PublicKey toRSAPublicKey() throws IllegalStateException {
        if (container instanceof RSAKey) {
            try {
                return ((RSAKey)container).toPublicKey();
            }
            catch (JOSEException e) {
                throw new IllegalStateException("This JWK is not a RSA-key");
            }
        }
        throw new IllegalStateException("This JWK is not a RSA-key");
    }
}
