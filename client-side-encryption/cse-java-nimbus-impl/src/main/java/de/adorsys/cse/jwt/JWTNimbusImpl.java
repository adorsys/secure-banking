package de.adorsys.cse.jwt;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;

import java.text.ParseException;
import java.util.Optional;

public class JWTNimbusImpl implements JWT {
    private String originalBase64encodedToken;
    private com.nimbusds.jwt.JWT container;

    private JWTClaimsSet claimsSet;

    public JWTNimbusImpl(String base64encodedJWT) throws ParseException {
        if (base64encodedJWT == null) {
            throw new IllegalArgumentException("base64encodedJWT must not be null");
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
    public Optional<String> getClaim(String claimName) {
        try {
            return Optional.ofNullable(claimsSet.getStringClaim(claimName));
        } catch (ParseException e) {
            return Optional.empty();
        }
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
