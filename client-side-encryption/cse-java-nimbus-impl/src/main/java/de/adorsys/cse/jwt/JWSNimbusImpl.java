package de.adorsys.cse.jwt;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class JWSNimbusImpl extends JWTNimbusImpl implements JWS {
    private static final Logger log = LoggerFactory.getLogger(JWSNimbusImpl.class);

    JWSNimbusImpl(JWTClaimsSet claimsSet, String hmacSecret, JWSAlgorithm signAlgorithm) {
        super(claimsSet);

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

    @Override
    public boolean isSigned() {
        return true;
    }

    SignedJWT getContainer() {
        return (SignedJWT) container;
    }
}
