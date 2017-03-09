package de.adorsys.cse.example.client.token;

import de.adorsys.cse.CseFactory;
import de.adorsys.cse.example.client.token.bean.JwtResponse;
import de.adorsys.cse.example.client.token.bean.SecretRequest;
import de.adorsys.cse.example.util.UUIDNonceGenerator;
import de.adorsys.cse.jwt.JWT;
import de.adorsys.cse.jwt.JWTBuilder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Api(value = "/v1/secret")
@Path("/v1/secret")
public class TokenGeneratorResource {
    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Enclose a secret into a JWE Token", response = JwtResponse.class, notes = "Create and sign an access token based on information provided by the caller.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ok", response = JwtResponse.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = JwtResponse.class)
    })
    public Response returnSecretInToken(@Context HttpServletRequest request, SecretRequest secret) {
        try {
            System.out.println("secret = " + ToStringBuilder.reflectionToString(secret));

            JWTBuilder builder = CseFactory.init().jwtBuilder();

            builder = builder
                    .withNonceGenerator(new UUIDNonceGenerator());

            if (StringUtils.isNotEmpty(secret.getSecret())) {
                builder = builder.withPayload(secret.getSecret());

            }

            if (secret.getExpirationTimeMs() != null) {
                builder = builder.withExpirationTimeInMs(secret.getExpirationTimeMs());
            }

            JWT jwt;
            if (StringUtils.isNoneEmpty(secret.getHmacSecret())) {
                jwt = builder.buildAndSign(secret.getHmacSecret());
            }
            else {
                jwt = builder.build();
            }
            String base64EncodedJWT = jwt.encode();
            System.out.println("response = " + base64EncodedJWT);

            JwtResponse jwtResponse = new JwtResponse();
            jwtResponse.setJwt(base64EncodedJWT);
            return Response.ok().entity(jwtResponse).build();
        } catch (Exception e) {
            throw new ServerErrorException(500, e);
        }
    }
}
