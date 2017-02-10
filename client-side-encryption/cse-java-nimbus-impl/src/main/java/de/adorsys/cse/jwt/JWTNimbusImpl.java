package de.adorsys.cse.jwt;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.jwt.PlainJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.Optional;

public class JWTNimbusImpl implements JWT {
    private final static Logger log = LoggerFactory.getLogger(JWTNimbusImpl.class);

    private String base64encodedToken;
    private com.nimbusds.jwt.JWT container;

    private JWTClaimsSet claimsSet;

    JWTNimbusImpl(JWTClaimsSet claimsSet) {
        this.claimsSet = claimsSet;
        this.container = new PlainJWT(claimsSet);
        this.base64encodedToken = container.serialize();
    }

    public JWTNimbusImpl(String base64encodedJWT) throws ParseException {
        if (base64encodedJWT == null) {
            throw new IllegalArgumentException("base64encodedJWT must not be null");
        }
        this.base64encodedToken = base64encodedJWT;
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

        return base64encodedToken.equals(jwtNimbus.base64encodedToken);
    }

    @Override
    public int hashCode() {
        return base64encodedToken.hashCode();
    }

}
