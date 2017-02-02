package de.adorsys.cse.jwt;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import de.adorsys.cse.jwk.JWK;

import java.text.ParseException;

public class JWTNimbusImpl implements JWT {
    private String originalBase64encodedToken;
    private com.nimbusds.jwt.JWT container;

    private JWTClaimsSet claimsSet;

    private com.nimbusds.jwt.JWT resourcePublicKey;

    public JWTNimbusImpl(String base64encodedJWT) throws ParseException {
        if (base64encodedJWT == null) {
            throw new ParseException("encodedJWT must not be null", 0);
        }
        this.originalBase64encodedToken = base64encodedJWT;
        this.container = JWTParser.parse(base64encodedJWT);
        this.claimsSet = container.getJWTClaimsSet();
    }

    @Override
    public String encode() {
        return container.serialize();
    }

    @Override
    public JWK getResourcePublicKey() throws ParseException {
        //TODO convert to JWK
        claimsSet.getStringClaim(JWT.PUBLIC_KEY_CLAIM);
        return null;
    }

    @Override
    public String getClaim(String claimName) throws ParseException {
        return claimsSet.getStringClaim(claimName);
    }

    @Override
    public String toString() {
        return container.serialize();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JWTNimbusImpl jwtNimbus = (JWTNimbusImpl) o;

        return originalBase64encodedToken.equals(jwtNimbus.originalBase64encodedToken);
    }

    @Override
    public int hashCode() {
        return originalBase64encodedToken.hashCode();
    }
}
