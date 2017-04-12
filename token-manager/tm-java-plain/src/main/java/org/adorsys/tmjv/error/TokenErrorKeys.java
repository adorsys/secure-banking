package org.adorsys.tmjv.error;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;

public enum TokenErrorKeys {
	BAD(Status.BAD_REQUEST),
	MALICIOUS(Status.UNAUTHORIZED),
	EARLY(Status.UNAUTHORIZED),
	EXPIRED(Status.UNAUTHORIZED),
	DISABLED(Status.UNAUTHORIZED),
	WRONG_ROLE(Status.UNAUTHORIZED),
	NO_ROLE(Status.UNAUTHORIZED);

	private final Status status;

	private TokenErrorKeys(Status status) {
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
}
