package org.adorsys.psd2.pop;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.nimbusds.jose.jwk.JWKSet;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ResponseHeader;

@Api(value = "/v1/pop", tags={"Proof of Pocession RFC7800"}, description = "Public key distribution endpoint for this server")
@Path("/v1/pop")
public class PoPResource {
	
	@Inject
	private ServerKeysHolder serverKeysHolder;
	
	@GET
	@Path("pop-keys.json")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Read server public keys", tags={"Read server public keys"}, response=JWKSet.class, notes = "Fetches publick keys of the target server. Keys are used to encrypt data sent to the server and also send a response encrytpion key to the server. See RFC7800")
	@ApiResponses(value = { @ApiResponse (code = 200, message = "Ok"),
			@ApiResponse(code = 400, message = "Bad request", responseHeaders=@ResponseHeader(name="ERROR_KEY", description="BAD_REQUEST"))})
	public Response getPublicKeys(){
		return Response.ok().entity(serverKeysHolder.getPublicKeySet().toJSONObject()).build();
	}
}
