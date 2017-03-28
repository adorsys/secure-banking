package de.adorsys.cse.jwt;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.jwt.PlainJWT;
import com.nimbusds.jwt.SignedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.time.Instant;
import java.util.*;

public class JWTNimbusImpl implements JWT {
    private final static Logger log = LoggerFactory.getLogger(JWTNimbusImpl.class);

    String base64encodedToken;
    com.nimbusds.jwt.JWT container;

    private JWTClaimsSet claimsSet;

    JWTNimbusImpl(JWTClaimsSet claimsSet) {
        this.claimsSet = claimsSet;
        this.container = new PlainJWT(claimsSet);
        this.base64encodedToken = container.serialize();
    }

    JWTNimbusImpl(JWTClaimsSet claimsSet, String hmacSecret, JWSAlgorithm signAlgorithm) {
        this.claimsSet = claimsSet;

        try {
            JWSSigner signer = new MACSigner(hmacSecret);
            SignedJWT signedJWT = new SignedJWT(new JWSHeader(signAlgorithm), claimsSet);
            signedJWT.sign(signer);
            this.container = signedJWT;
            this.base64encodedToken = container.serialize();
        } catch (KeyLengthException e) {
            log.error("Provided hmacSecret's length is less then required key length. Actual length is {}", hmacSecret.length(), e);
        } catch (JOSEException e) {
            log.error("Error while singing JWT", e);
        }
    }

    JWTNimbusImpl(SignedJWT signedJWT) throws Exception {
        this.claimsSet = signedJWT.getJWTClaimsSet();
        this.container = signedJWT;
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
        Object claimValue = claimsSet.getClaim(claimName);
        if (claimValue == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(String.valueOf(claimValue));
    }

    @Override
    public boolean isSigned() {
        return false;
    }

    @Override
    public Map<String, Object> getPayloadClaims() {
        Object payload = claimsSet.getClaim(Claims.CLAIM_PAYLOAD);
        if (payload == null) {
            return Collections.emptyMap();
        }
        if (payload instanceof Map) {
            try {
                return ((Map<String, Object>) payload);
            }
            catch (ClassCastException e) {
                Collections.singletonMap("0", payload);
            }
        }
        return Collections.singletonMap("0", payload);
    }

    @Override
    public Map<String, Object> getAllClaims() {
        return claimsSet.getClaims();
    }

    @Override
    public boolean isExpired() {
        Instant now = Instant.now();
        try {
            Date expirationTime = claimsSet.getDateClaim(Claims.CLAIM_EXPIRATION_TIME);
            return expirationTime != null && now.isAfter(expirationTime.toInstant());
        }
        catch (ParseException e) {
            //invalid claim content => token is invalid
            return true;
        }
    }

    @Override
    public boolean isNotExpired() {
        return !isExpired();
    }

    Optional<Instant> getTokenIssueTime() {
        Date issueTime = claimsSet.getIssueTime();
        if (issueTime == null) {
            return Optional.empty();
        }
        return Optional.of(issueTime.toInstant());
    }

    Optional<Instant> getTokenExpirationTime() {
        Date expirationTime = claimsSet.getExpirationTime();
        if (expirationTime == null) {
            return Optional.empty();
        }
        return Optional.of(expirationTime.toInstant());
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

    JWTClaimsSet getClaimsSet() {
        return claimsSet;
    }
}
