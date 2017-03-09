package de.adorsys.cse.example.server.token;

import de.adorsys.cse.CseFactory;
import de.adorsys.cse.example.server.token.bean.JWSTokenRequest;
import de.adorsys.cse.example.server.token.bean.TokenStatusResponse;
import de.adorsys.cse.jwt.JWS;
import de.adorsys.cse.jwt.JWT;
import de.adorsys.cse.jwt.JWTSigner;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.text.ParseException;

@Api(value = "/v1/tokenEndpoint")
@Path("/v1/tokenEndpoint")
public class TokenReceiverResource {
    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Receive token", response = TokenStatusResponse.class, notes = "Receives JWT/JWS token, verifies HMAC signature if HMAC secret is provided and prints token's claims")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ok", response = TokenStatusResponse.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = String.class)
    })
    public Response returnSecretInToken(@Context HttpServletRequest request, JWSTokenRequest tokenRequest) {
        try {
            CseFactory factory = CseFactory.init();
            TokenStatusResponse tokenStatusResponse = new TokenStatusResponse();


            if (StringUtils.isNotEmpty(tokenRequest.getHmacSecret())) {
                JWS jws = (JWS) factory.parseToken(tokenRequest.getJwt());
                JWTSigner signer = factory.jwtHMacSigner();
                boolean signatureVerification = signer.verify(jws, tokenRequest.getHmacSecret());
                tokenStatusResponse.setSignatureValid(signatureVerification);
            }

            return Response.ok(tokenStatusResponse).build();

        }
        catch (ParseException e) {
            return Response.serverError().entity("Invalid token provided" + e.getMessage()).build();
        }
        catch (ClassNotFoundException e) {
            return Response.serverError().entity("Cannot initialize library" + e.getMessage()).build();
        }


    }
}
