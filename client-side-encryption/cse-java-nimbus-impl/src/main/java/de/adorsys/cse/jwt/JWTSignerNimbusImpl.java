package de.adorsys.cse.jwt;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.text.ParseException;
import java.util.Arrays;

public class JWTSignerNimbusImpl implements JWTSigner {
    private static final int MINIMAL_KEY_LENGTH_BYTES = 32; //minimal key length for HS256 - 256 bits

    private static final Logger log = LoggerFactory.getLogger(JWTSignerNimbusImpl.class);

    private SecureRandom secureRandom = new SecureRandom();

    @Override
    public byte[] generateHMacSecret() {
        byte[] randomBytes = new byte[MINIMAL_KEY_LENGTH_BYTES];
        secureRandom.nextBytes(randomBytes);
        return randomBytes;
    }

    @Override
    public JWS sign(JWT jwt, String hmacSecret) {
        if (hmacSecret == null || hmacSecret.length() == 0) {
            throw new IllegalArgumentException("hmacSecret cannot be null or empty");
        }

        JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder();
        jwt.getAllClaims().forEach(builder::claim);


        return new JWSNimbusImpl(builder.build(), normalizeHmacSecretLength(hmacSecret.getBytes()), JWSAlgorithm.HS256);
    }

    @Override
    public boolean verify(JWS signedJwt, String hmacSecret) {
        if (hmacSecret == null || hmacSecret.getBytes().length == 0) {
            throw new IllegalArgumentException("hmacSecret cannot be null or empty");
        }

        try {
            SignedJWT signedJWT = SignedJWT.parse(signedJwt.encode());
            MACVerifier verifier = new MACVerifier(normalizeHmacSecretLength(hmacSecret.getBytes()));
            return signedJWT.verify(verifier);
        } catch (JOSEException | ParseException e) {
            log.warn("Failed checking signature of jwt. Reason: {}", e.getMessage());
            return false;
        }
    }

    private byte[] normalizeHmacSecretLength(byte[] hmacSecret) {
        if (hmacSecret.length < MINIMAL_KEY_LENGTH_BYTES) {
            log.warn("provided hmacSecret is less then {} bytes and will be extended with 0-byte", MINIMAL_KEY_LENGTH_BYTES * 8);
            return Arrays.copyOf(hmacSecret, MINIMAL_KEY_LENGTH_BYTES);
        }
        else {
            return hmacSecret;
        }
    }
}
