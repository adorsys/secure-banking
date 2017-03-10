package de.adorsys.cse.jwt;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSAEncrypter;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import de.adorsys.cse.crypt.JWTEncryptionException;
import de.adorsys.cse.crypt.JWTEncryptor;
import de.adorsys.cse.jwk.JWK;

import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.util.Map;

public class JWTEncryptorNimbusImpl implements JWTEncryptor {

    private final RSAEncrypter encrypter;

    public JWTEncryptorNimbusImpl(JWK publicKey) {
        if (publicKey == null) {
            throw new IllegalArgumentException("publicKey cannot be null");
        }
        if (!publicKey.getKeyType().equals("RSA")) {
            throw new IllegalArgumentException("only RSA public keys are supported");
        }
        RSAPublicKey rsaPublicKey = (RSAPublicKey) publicKey.toRSAPublicKey();
        encrypter = new RSAEncrypter(rsaPublicKey);

    }

    @Override
    public JWE encrypt(JWT jwt) throws JWTEncryptionException {
        JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder();

        Map<String, Object> allClaims = jwt.getAllClaims();
        allClaims.forEach(builder::claim);

        JWTClaimsSet claimsSet = builder.build();

        JWEObject jweObject = new JWEObject(
                new JWEHeader.Builder(JWEAlgorithm.RSA_OAEP, EncryptionMethod.A128GCM)
                        .contentType("JWT") // required to signal nested JWT
                        .build(),
                new Payload(claimsSet.toJSONObject()));

        try {
            jweObject.encrypt(encrypter);
            return new JWENimbusImpl(jweObject);
        } catch (JOSEException e) {
            throw new JWTEncryptionException("An error occured during JWT encryption: " + e.getMessage());
        }
    }

    @Override
    public JWE encrypt(JWS signedJwt) throws JWTEncryptionException {
        try {
            SignedJWT signedJWT = SignedJWT.parse(signedJwt.encode());

            JWEObject jweObject = new JWEObject(
                    new JWEHeader.Builder(JWEAlgorithm.RSA_OAEP, EncryptionMethod.A128GCM)
                            .contentType("JWT") // required to signal nested JWT
                            .build(),
                    new Payload(signedJWT));

            jweObject.encrypt(encrypter);
            return new JWENimbusImpl(jweObject);
        }
        catch (ParseException e) {
            throw new JWTEncryptionException("Invalid JWT prodivded" + signedJwt.encode());
        }
        catch (JOSEException e) {
            throw new JWTEncryptionException("An error occured during JWT encryption: " + e.getMessage());
        }
    }

}
