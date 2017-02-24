package de.adorsys.cse.example.client;

import de.adorsys.cse.example.token.SecretRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
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
    @ApiOperation(value = "Enclose a secret into a JWE Token", response = String.class, notes = "Create and sign an access token based on information provided by the caller.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ok", response = String.class),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public Response returnSecretInToken(@Context HttpServletRequest request, SecretRequest secret) {
        System.out.println("secret = " + secret.getSecret());
        return Response.ok().entity("").build();
    }
}
