package de.adorsys.cse.example.client.crypto;

import de.adorsys.cse.CseFactory;
import de.adorsys.cse.crypt.JWTDecryptor;
import de.adorsys.cse.crypt.JWTEncryptionException;
import de.adorsys.cse.crypt.JWTEncryptor;
import de.adorsys.cse.example.client.crypto.bean.*;
import de.adorsys.cse.example.util.KeysSerializer;
import de.adorsys.cse.jwk.JWK;
import de.adorsys.cse.jwt.JWE;
import de.adorsys.cse.jwt.JWT;
import de.adorsys.cse.jwt.JWTBuilder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InvalidObjectException;
import java.security.*;

@Api(value = "/v1/jwe")
@Path("/v1/jwe")
public class JWEGeneratorResource {
    private CseFactory cseFactory;

    public JWEGeneratorResource() throws ClassNotFoundException {
        cseFactory = CseFactory.init();
    }

    @GET
    @Path("/keypair")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Generate a RSA key pair", response = KeyPairResponse.class, notes = "Generates a RSA key pair and returns the keys in various formats. JWK can be used for future encryption")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ok", response = KeyPairResponse.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = KeyPairResponse.class)
    })
    public Response generateRSAKeyPair(@Context HttpServletRequest request) {

        KeyPairGenerator keyPairGenerator;
        try {
            keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            return Response.serverError().entity(e).build();
        }
        keyPairGenerator.initialize(1024);

        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        JWK publikKeyJWK = cseFactory.jwkPublicKeyBuilder().build(publicKey);

        KeyPairResponse keyPairResponse = new KeyPairResponse();
        keyPairResponse.setPublicKeyJWK(publikKeyJWK.toBase64JSONString());
        keyPairResponse.setPublicKey(KeysSerializer.publicKeyToBase64String(publicKey));
        keyPairResponse.setPrivateKey(KeysSerializer.privateKeyToBase64String(privateKey));

        return Response.ok(keyPairResponse).build();
    }

    @POST
    @Path("/encrypt")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Encrypt a secret into a JWE Token", response = JWEEncryptedResponse.class, notes = "Create a JWT with some secret and encrypt with RSA Public Key.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ok", response = JWEEncryptedResponse.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = String.class)
    })
    public Response encryptToken(@Context HttpServletRequest request, JWEEncryptRequest encryptRequest) {
        JWK publicKeyJWK;
        if (StringUtils.isNotEmpty(encryptRequest.getPublicKeyJWK())) {
            try {
                publicKeyJWK = getJWKFromBase64SJWK(encryptRequest.getPublicKeyJWK());
            } catch (Exception e) {
                return Response.serverError().entity("Cannot parse publicKeyJWK: " + e.getMessage()).build();
            }
        }
        else if (StringUtils.isNotEmpty(encryptRequest.getPublicKey())) {
            try {
                publicKeyJWK = getJWKFromBase64PublicKey(encryptRequest.getPublicKey());
            } catch (Exception e) {
                return Response.serverError().entity("Cannot parse publicKey: " + e.getMessage()).build();
            }
        }
        else {
            return Response.serverError().entity("Either publicKey or publicKeyJWK must be provided").build();
        }

        String secretToEncrypt = encryptRequest.getSecretToEncrypt();
        JWTBuilder builder = cseFactory.jwtBuilder();
        JWT tokenToEncrypt;
        try {
            tokenToEncrypt = builder.withPayload(secretToEncrypt).build();
        } catch (InvalidObjectException e) {
            return Response.serverError().entity("Cannot serialize String: " + secretToEncrypt).build();
        }

        JWTEncryptor jwtEncryptor = cseFactory.jwtEncryptor(publicKeyJWK);

        JWEEncryptedResponse response = new JWEEncryptedResponse();
        JWE jwe;
        try {
            jwe = jwtEncryptor.encrypt(tokenToEncrypt);
        } catch (JWTEncryptionException e) {
            return Response.status(Response.Status.EXPECTATION_FAILED).entity("Cannot encrypt token: " + tokenToEncrypt.encode()).build();
        }

        response.setEncryptedJWT(jwe.encode());
        return Response.ok(response).build();
    }

    private JWK getJWKFromBase64SJWK(String publicKeyJWK) throws Exception {
        return cseFactory.jwkPublicKeyBuilder().buildFromBase64EncodedJWK(publicKeyJWK);
    }

    private JWK getJWKFromBase64PublicKey(String publicKeyBase64) throws Exception {
        PublicKey publicKey = KeysSerializer.base64StringToPublicKey(publicKeyBase64);
        return cseFactory.jwkPublicKeyBuilder().build(publicKey);

    }

    @POST
    @Path("/decrypt")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Decrypt a secret from the JWE Token", response = JWEDecryptedResponse.class, notes = "Decrypts the secrets provided in JWT token with RSA Private key")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ok", response = JWEDecryptedResponse.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = JWEDecryptedResponse.class)
    })
    public Response decryptToken(@Context HttpServletRequest request, JWEDecryptRequest decryptRequest) {
        String privateKeyBase64 = decryptRequest.getPrivateKey();

        PrivateKey privateKey;
        try {
            privateKey = KeysSerializer.base64StringToPrivateKey(privateKeyBase64);
        } catch (Exception e) {
            return Response.serverError().entity("Cannot parse PrivateKey").build();
        }
        JWTDecryptor jwtDecryptor = cseFactory.jwtDecryptor(privateKey);

        String encryptedToken = decryptRequest.getEncryptedJWT();

        JWT decryptedJWT;
        try {
            decryptedJWT = jwtDecryptor.decryptUnsigned(encryptedToken);
        } catch (JWTEncryptionException e) {
            return Response.serverError().entity("Error decrypting token").build();
        }

        JWEDecryptedResponse response = new JWEDecryptedResponse();

        decryptedJWT.getPayloadClaims().forEach(response.getDecryptedSecrets()::put);

        return Response.ok(response).build();
    }


}
