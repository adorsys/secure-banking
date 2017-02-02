package de.adorsys.cse.jwt;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import de.adorsys.cse.jwk.JWK;

import java.text.ParseException;

public class JWTNimbusImpl implements JWT {
    private com.nimbusds.jwt.JWT container;

    private JWTClaimsSet claimsSet;

    private com.nimbusds.jwt.JWT resourcePublicKey;

    public JWTNimbusImpl(String jwt) throws ParseException {
        this.container = JWTParser.parse(jwt);
        this.claimsSet = container.getJWTClaimsSet();
    }

    @Override
    public Base64EncodedJWT encode() {
        return new Base64EncodedJWTNimbusImpl(container.serialize());
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
}
