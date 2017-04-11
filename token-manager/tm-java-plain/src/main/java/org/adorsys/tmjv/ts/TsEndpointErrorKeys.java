package org.adorsys.tmjv.ts;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;

public enum TsEndpointErrorKeys {
	MISSING_FIELD(Status.BAD_REQUEST);

	private final Status status;

	private TsEndpointErrorKeys(Status status) {
		this.status = status;
	}

	public Status getStatus() {
		return status;
	}
	
	public boolean match(String message){
		return StringUtils.equals(this.name(), message);
	}
	
	public Response error(){
		return Response.status(this.status).header("ERROR_KEY", this.name()).build();
	}

	public Response error(String hint){
		return Response.status(this.status).header("ERROR_KEY", this.name() + " " + hint).build();
	}
}
