package de.adorsys.cse.jwt;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jose.crypto.RSADecrypter;
import com.nimbusds.jose.crypto.RSAEncrypter;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.junit.Test;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;

public class JWEDemo {

    @Test
    public void demoJWEfromNimbus() throws Exception {
        // Generate 256-bit AES key for HMAC
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256);
        SecretKey hmacKey = keyGen.generateKey();

        // Create HMAC signer
        JWSSigner signer = new MACSigner(hmacKey.getEncoded());

        // Prepare JWT with claims set
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject("alice")
                .issueTime(new Date())
                .issuer("https://c2id.com")
                .build();

        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);

        // Apply the HMAC
        signedJWT.sign(signer);

        // Create JWE object with signed JWT as payload
        JWEObject jweObject = new JWEObject(
                new JWEHeader.Builder(JWEAlgorithm.RSA_OAEP, EncryptionMethod.A128GCM)
                        .contentType("JWT") // required to signal nested JWT
                        .build(),
                new Payload(signedJWT));

        // Perform encryption
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(1024);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();


        RSAEncrypter rsaEncrypter = new RSAEncrypter((RSAPublicKey) keyPair.getPublic());
        jweObject.encrypt(rsaEncrypter);

        // Serialise to JWE compact form
        String jweString = jweObject.serialize();
        System.out.println("jweString = " + jweString);


        EncryptedJWT jwt = EncryptedJWT.parse(jweString);

        // Create a decrypter with the specified private RSA key
        RSADecrypter decrypter = new RSADecrypter(keyPair.getPrivate());

        // Decrypt
        jwt.decrypt(decrypter);
        SignedJWT signedJwt = jwt.getPayload().toSignedJWT();
        
        signedJwt.verify(new MACVerifier(hmacKey.getEncoded()));

        // Retrieve JWT claims
        System.out.println(signedJwt.getJWTClaimsSet().getIssuer());
        System.out.println(signedJwt.getJWTClaimsSet().getSubject());
        System.out.println(signedJwt.getJWTClaimsSet().getAudience().size());
        System.out.println(signedJwt.getJWTClaimsSet().getExpirationTime());
        System.out.println(signedJwt.getJWTClaimsSet().getNotBeforeTime());
        System.out.println(signedJwt.getJWTClaimsSet().getIssueTime());
        System.out.println(signedJwt.getJWTClaimsSet().getJWTID());
    }
}
