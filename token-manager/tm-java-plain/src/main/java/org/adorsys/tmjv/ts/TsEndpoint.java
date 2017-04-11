package org.adorsys.tmjv.ts;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.adorsys.jtstamp.exception.TsMissingFieldException;
import org.adorsys.jtstamp.exception.TsSignatureException;
import org.adorsys.jtstamp.model.TsData;
import org.adorsys.jtstamp.service.TsService;
import org.adorsys.tmjv.key.ServerKeys;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * The timestamp endpoint Generate a timestamp and sign it toghether with the hash provided by the caller.
 * <p>
 *
 * @author fpo
 */
@Api(value = "/v1/timestamp", tags={"Timestamp Endpoint"})
@Path("/v1/timestamp")
@ApplicationScoped
public class TsEndpoint {

    @Inject
    private ServerKeys serverKeys;
    
    private TsService tsService;
    
    @PostConstruct
    public void postConstruct(){
    	tsService = new TsService(serverKeys.getKeys());
    }
    
    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Create Token", response = TsToken.class, notes = "Generate a timestamp and sign it toghether with the hash provided by the caller.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ok", response = TsToken.class),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public Response stamp(@Context HttpServletRequest request, @ApiParam(value = "Timestamp data") TsData data, @Context UriInfo uriInfo) {
    	
    	String iss = uriInfo.getAbsolutePath().toASCIIString();
    	
    	
        String s;
		try {
			s = tsService.stamp(data, iss);
		} catch (TsMissingFieldException e) {
			return TsEndpointErrorKeys.MISSING_FIELD.error(e.getMessage());
		} catch (TsSignatureException e) {
			throw new ServerErrorException(500, e.getCause());
		}
        TsToken token = new TsToken();
        token.setTimestamp(s);
        return Response.ok().entity(token).build();
    }
}
