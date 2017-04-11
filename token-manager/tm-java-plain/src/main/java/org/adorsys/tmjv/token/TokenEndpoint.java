package org.adorsys.tmjv.token;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.adorsys.jjwk.selector.KeyPairRandomSelector;
import org.adorsys.jjwk.selector.JWSSignerAndAlgorithm;
import org.adorsys.jjwk.selector.JWSSignerAndAlgorithmBuilder;
import org.adorsys.tmjv.key.ServerKeys;
import org.apache.commons.lang3.StringUtils;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTClaimsSet.Builder;
import com.nimbusds.jwt.SignedJWT;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * The token endpoint allow us to obtain reuqest for a jwt token in excahnge
 * of a a simple token
 * <p>
 *
 * @author fpo
 */
@Api(value = "/v1/token", tags={"Token Endpoint"})
@Path("/v1/token")
public class TokenEndpoint {

    @Inject
    private ServerKeys serverKeys;

    @PostConstruct
    public void initialize() {
    }

    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Create Token", response = AccessToken.class, notes = "Create and sign an access token based on information provided by the caller. We assume the caller will be an authentication service and that the user is already authenticated.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ok", response = AccessToken.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = AccessToken.class)
    })
    public Response token(@Context HttpServletRequest request, Jwt jwt) {
        Builder builder = new JWTClaimsSet.Builder();

        if (StringUtils.isNotBlank(jwt.getAud())) {
            builder.audience(jwt.getAud());
        }
        if (jwt.getExp() != null) {
            builder.expirationTime(jwt.getExp());
        }
        if (StringUtils.isNotBlank(jwt.getIss())) {
            builder.issuer(jwt.getIss());
        }
        if (jwt.getIat() != null) {
            builder.issueTime(jwt.getIat());
        }
        if (StringUtils.isNotBlank(jwt.getJti())) {
            builder.jwtID(jwt.getJti());
        }
        if (jwt.getNbf() != null) {
            builder.notBeforeTime(jwt.getNbf());
        }
        if (StringUtils.isNotBlank(jwt.getSub())) {
            builder.subject(jwt.getSub());
        }
        if (jwt.getCnf() != null) {
            builder.claim("cnf", jwt.getCnf());
        }
        JWTClaimsSet claimsSet = builder.build();

        JOSEObjectType typ = JOSEObjectType.JWT;
        JWK jwk = KeyPairRandomSelector.randomKey(serverKeys.getKeys());
        JWSSignerAndAlgorithm signerAndAlgorithm = JWSSignerAndAlgorithmBuilder.build(jwk);
		JWSHeader jwsHeader = new JWSHeader(signerAndAlgorithm.getJwsAlgorithm(), typ , null, null, 
				null, null, null, null, null, null, jwk.getKeyID(), null, null);
        SignedJWT signedJWT = new SignedJWT(jwsHeader,claimsSet);
        try {
            signedJWT.sign(signerAndAlgorithm.getSigner());
        } catch (JOSEException e) {
            throw new ServerErrorException(500, e);
        }
        String s = signedJWT.serialize();
        AccessToken token = new AccessToken();
        token.setAccessToken(s);
        return Response.ok().entity(token).build();
    }

    @GET
    @Path("/")
    @Produces(MediaType.TEXT_PLAIN)
    @ApiOperation(value = "Server Info  Endpoint", notes = "Just produces token for authentication servers.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ok")
    })
    public Response info(@Context HttpServletRequest request) {
        return Response.ok().entity("Simple token manager!").build();
    }
}
